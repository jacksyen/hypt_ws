package cn.com.tf.handler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import cn.com.hypt.db.model.Terminal;
import cn.com.tf.cache.ITerminalCacheManager;
import cn.com.tf.protocol.EMsgAck;
import cn.com.tf.protocol.Jt808Message;
import cn.com.tf.protocol.Jt808MessageHead;
import cn.com.tf.protocol.impl.JT8001;

/**
 * 上行数据包处理器
 * @author tianfei
 *
 */
public class UpDataHandler {
	
	private static Logger logger = LoggerFactory.getLogger(UpDataHandler.class);
	
	/**
	 * 上行消息队列
	 */
	private static BlockingQueue<Jt808Message> updataQueue = new LinkedBlockingQueue<Jt808Message>();
	
	private ExecutorService executorService =  Executors.newFixedThreadPool(20);
	
	private volatile boolean startFlag = false;
	
	private Map<String, IJt808Handler> codeHandler = new HashMap<String, IJt808Handler>();
	
	/**
	 * 立即响应列表
	 */
	private List<String> platformCommReponse = new ArrayList<String>();
	
	@Autowired
	private ITerminalCacheManager terminalCacheManager;
	
	/**
	 * 启动处理器
	 */
	public void startHandler(){
		synchronized (this) {
			//启动上行处理器 
			if(!startFlag){
				startFlag = true;
				Thread thread = new Thread(new Runnable() {
					public void run() {
						logger.info("启动上行数据处理器成功！");
						while(true){
							try {
								executorService.execute(new HandlerThread(updataQueue.take()));	//阻塞方法
								Thread.sleep(300);
							} catch (InterruptedException e) {
								Thread.currentThread().interrupt();
							}
						}
					}
				});
				thread.start();
			}
		}
	}
	
	/**
	 * 存放上行消息
	 * @param msg
	 */
	public void add(Jt808Message msg) {
		try {
			updataQueue.put(msg);
		} catch (InterruptedException e) {
		}
	}
	
	/**
	 * 处理器线程
	 * @author tianfei
	 *
	 */
	private class HandlerThread implements Runnable {
		
		private Jt808Message msg;
		
		public HandlerThread(Jt808Message msg){
			this.msg = msg;
		}

		@Override
		public void run() {
			//判断终端是否已经鉴权
			if(!msg.getConn().isAuth() && msg.getHead().getMessageId() != 0x0100 && msg.getHead().getMessageId() != 0x0102){
				logger.error("终端未鉴权，SIM："+msg.getSimNo());
				return;
			}
			//判断终端是否存在
			Terminal tmnl = terminalCacheManager.getTerminalBySimNo(msg.getSimNo());
			if(tmnl == null){
				logger.error("sim卡号："+msg.getSimNo()+"的终端未在该平台注册！");
				return;
			}
			//调用 消息处理器
			IJt808Handler h =  codeHandler.get(msg.getMessageID());
			if(h == null){
				logger.info("没有相应的处理器或平台不需要处理，消息ID:"+msg.getMessageID());
				return;
			}
			//平台通用响应
			if(platformCommReponse.contains(msg.getMessageID())){
				//回复消息
				JT8001 rbody = new JT8001(msg.getHead().getFlowNo(), msg.getHead().getMessageId(), EMsgAck.SUCESS.value());
				Jt808MessageHead head = msg.getHead();
				head.setMessageId(0x8001);
				Jt808Message response = new Jt808Message(head,rbody);
				h.writeResponse(response);
			}
			//消息处理器调用
			h.handle(msg);
		}
	}

	public void setCodeHandler(Map<String, IJt808Handler> codeHandler) {
		this.codeHandler = codeHandler;
	}

	public void setPlatformCommReponse(List<String> platformCommReponse) {
		this.platformCommReponse = platformCommReponse;
	}
}
