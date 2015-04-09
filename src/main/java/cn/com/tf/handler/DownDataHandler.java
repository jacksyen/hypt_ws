package cn.com.tf.handler;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import cn.com.tf.protocol.Jt808Message;
import cn.com.tf.server.Connection;
import cn.com.tf.server.MinaServer;

/**
 * 下行数据处理器
 * @author tianfei
 *
 */
@Component
public class DownDataHandler {
	
	private transient Logger logger = LoggerFactory.getLogger(DownDataHandler.class);
	
	@Autowired
	private ServerHandler serverHandler;
	
	@Autowired
	private MinaServer minaServer;
	
	private volatile boolean startFlag = false;
	
	/**
	 * 下行数据队列
	 */
	private static BlockingQueue<Jt808Message> downdataQueue = new LinkedBlockingQueue<Jt808Message>();
	
	private ExecutorService executors = Executors.newFixedThreadPool(20);
	
	public static void put(Jt808Message msg){
		try {
			downdataQueue.put(msg);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void startHandler(){
		synchronized (this) {
			if (!startFlag) {
				Thread thread = new Thread(new Runnable() {
					
					@Override
					public void run() {
						logger.info("启动下午数据处理器成功！");
						// TODO Auto-generated method stub
						while(true){
							try {
								executors.execute(new HandlerThread(downdataQueue.take()));
								TimeUnit.MILLISECONDS.sleep(200);
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}
				});
				thread.start();
			}
		}
	}
	
	private class HandlerThread implements Runnable {
		
		private Jt808Message response;
		
		public HandlerThread(Jt808Message msg){
			this.response = msg;
		}

		@Override
		public void run() {
			// 下发消息
			Connection conn = serverHandler.getConnection(response.getSimNo());
			if(conn != null){
				IoSession session = minaServer.getSession(conn.getSessionId());
				if(session != null && session.isConnected()){
					logger.info("发送消息："+response.toString());
					session.write(response);
				}
			}
		}
	}
}
