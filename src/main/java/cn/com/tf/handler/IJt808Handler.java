package cn.com.tf.handler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cn.com.tf.cache.IDataAcquireCacheManager;
import cn.com.tf.cache.ITerminalCacheManager;
import cn.com.tf.cache.ITmnlVehiCacheManager;
import cn.com.tf.cache.IVehicleCacheManager;
import cn.com.tf.protocol.EMsgAck;
import cn.com.tf.protocol.Jt808Message;

/**
 * jt808处理器接口
 * @author tianfei
 *
 */
@Component
public abstract class  IJt808Handler {
	
	/**
	 * 操作结果
	 */
	protected byte optResult = EMsgAck.SUCESS.value();
	
	@Autowired
	protected ITerminalCacheManager terminalCacheManager;
	
	@Autowired
	protected ITmnlVehiCacheManager tmnlVehicleCacheManager;
	
	@Autowired
	protected IVehicleCacheManager vehicleCacheManager;
	
	@Autowired
	protected IDataAcquireCacheManager dataAcquireCacheManager;
	
	@Autowired
	protected DownDataHandler downDataHandler;
	
	/**
	 * JT808事件处理
	 * @param msg
	 */
	protected abstract void handle(Jt808Message msg);
	
	/**
	 * 返回请求响应
	 * @param response
	 */
	protected void writeResponse(Jt808Message response) {
		downDataHandler.put(response);
	}

}
