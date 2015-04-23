package cn.com.tf.server;

import java.net.InetSocketAddress;

import org.apache.commons.lang.StringUtils;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.keepalive.KeepAliveFilter;
import org.apache.mina.filter.keepalive.KeepAliveMessageFactory;
import org.apache.mina.filter.keepalive.KeepAliveRequestTimeoutHandler;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cn.com.tf.codec.Jt808CodecFactory;
import cn.com.tf.handler.ServerHandler;
import cn.com.tf.protocol.Jt808Message;
import cn.com.tf.tool.ConfigUtil;

/**
 * MINA服务器
 * @author tianfei
 *
 */
@Component
public class MinaServer {
	
	private transient static Logger logger = LoggerFactory.getLogger(MinaServer.class);
	private static final String SERVER_PORT = "server_port";
	private volatile boolean starting = false;
	
	@Autowired
	private ServerHandler serverHandler;
	
	private NioSocketAcceptor acceptor = null;
	
	/**
	 * 启动服务
	 */
	public void startServer(){
		synchronized (this) {
			if(acceptor == null){
				try{
				    int port = 8888;
				    String cport = ConfigUtil.getConfigReader().getResourceAsProperties("config.properties").getProperty(SERVER_PORT);
				    if(StringUtils.isNotBlank(cport)){
				        port = Integer.parseInt(cport);
				    }
					//创建非阻塞的SERVER端的SOCKET
					acceptor = new NioSocketAcceptor();
//			acceptor.getFilterChain().addLast("codec", new ProtocolCodecFilter(new TextLineCodecFactory(Charset.forName("UTF-8"),
//					LineDelimiter.WINDOWS.getValue(),LineDelimiter.WINDOWS.getValue())));
					acceptor.getFilterChain().addLast("codec", new ProtocolCodecFilter(new Jt808CodecFactory()));
					//设置读取数据的缓冲区大小
					acceptor.getSessionConfig().setReadBufferSize(1024);
					//读写通道10秒内无操作进入空闲状态
					acceptor.getSessionConfig().setIdleTime(IdleStatus.BOTH_IDLE, 10);
					//心跳机制设置
					KeepAliveRequestTimeoutHandler heartBeatHandler = new KeepAliveRequestTimeoutHandlerImpl();
					KeepAliveMessageFactory heartBeatFactory = new KeepAliveMessageFactoryImpl();
					KeepAliveFilter heartBeat = new KeepAliveFilter(heartBeatFactory,IdleStatus.BOTH_IDLE);
					heartBeat.setForwardEvent(true);
					heartBeat.setRequestInterval(15 * 60);//心跳15分钟超时
					acceptor.getFilterChain().addLast("heartbeat", heartBeat);
					//绑定业务处理器
					acceptor.setHandler(serverHandler);
					acceptor.bind(new InetSocketAddress(port));
					starting = true;
					logger.info("服务器启动成功！端口号："+port);
				} catch (Exception e) {
					logger.info("服务启动失败！错误信息："+e.getMessage());
				}
			} else {
				logger.error("MINA服务已经启动");
			}
		}
	}
	
	public void stopServer(){
		synchronized (this) {
			if(starting){
				//TODO:关闭当前服务所有的SESSION
			}
		}
	}
	
	public IoSession getSession(long sessionId){
		return acceptor.getManagedSessions().get(sessionId);
	}
	
	/**
	 * 心跳处理器
	 * @author tianfei
	 *
	 */
	private class KeepAliveMessageFactoryImpl implements KeepAliveMessageFactory{
		@Override
		public boolean isRequest(IoSession session, Object message) {
			if(message instanceof Jt808Message){
				Jt808Message msg = (Jt808Message) message;
				if(msg.getHead().getMessageId() == 0x002){
					logger.info("接收到心跳包");
					return true;
				}
			}
			return false;
		}

		@Override
		public boolean isResponse(IoSession session, Object message) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public Object getRequest(IoSession session) {
			//心跳超时，断开连接 
			logger.info(String.format("终端与平台超时断开连接，SIM：%s",serverHandler.getSimNo(session)));
			session.close(true);
			return null;
		}

		@Override
		public Object getResponse(IoSession session, Object request) {
			if(request instanceof Jt808Message){
				Jt808Message msg = (Jt808Message) request;
				if(msg.getHead().getMessageId() == 0x002){
					logger.info("发送心跳包响应");
				}
			}
			return null;
		}
	}
	
	/**
	 * 心跳超时处理器
	 * @author tianfei
	 *
	 */
	private static class KeepAliveRequestTimeoutHandlerImpl implements KeepAliveRequestTimeoutHandler{

		@Override
		public void keepAliveRequestTimedOut(KeepAliveFilter filter,
				IoSession session) throws Exception {
			logger.info("心跳超时！");
			session.close(true);
		}
		
	}
}
