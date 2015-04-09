package cn.com.tf.handler;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cn.com.hypt.db.model.Terminal;
import cn.com.tf.cache.IDataAcquireCacheManager;
import cn.com.tf.cache.ITerminalCacheManager;
import cn.com.tf.protocol.Jt808Message;

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
	
	@Autowired
	private ITerminalCacheManager terminalCacheManager;
	
	@Autowired
	private IDataAcquireCacheManager dataAcquireCacheManager;
	
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
			//TODO:判断终端是否存在
			
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
			Terminal tmnl = terminalCacheManager.getTerminalBySimNo(msg.getSimNo());
			if(tmnl == null){
				logger.error("sim卡号："+msg.getSimNo()+"的终端未在该平台注册！");
				return;
			}
			//设置终端在线
			dataAcquireCacheManager.setIsOnline(tmnl.getTerminalId(), true);
			IJt808Handler h =  codeHandler.get(msg.getMessageID());
			if(h != null){
				h.handle(msg);
			} else {
				logger.info("没有相应的处理器！"+msg.getMessageID());
			}
		}
	}

	public void setCodeHandler(Map<String, IJt808Handler> codeHandler) {
		this.codeHandler = codeHandler;
	}
}
