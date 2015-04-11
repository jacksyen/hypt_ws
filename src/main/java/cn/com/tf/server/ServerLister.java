package cn.com.tf.server;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import cn.com.tf.cache.ITerminalCacheManager;
import cn.com.tf.cache.ITmnlVehiCacheManager;
import cn.com.tf.cache.IVehicleCacheManager;
import cn.com.tf.handler.DownDataHandler;
import cn.com.tf.handler.GpsHandler;
import cn.com.tf.handler.UpDataHandler;

/**
 * 启动服务事件
 * @author tianfei
 *
 */
public class ServerLister implements ServletContextListener {
	
	private ApplicationContext app;
	
	private MinaServer minaServer = null;
	
	@Override
	public void contextInitialized(ServletContextEvent event) {
		app = WebApplicationContextUtils.getWebApplicationContext(event.getServletContext());
		//初始化缓存数据
		initCache();
		//启动业务服务
		startBusi();
		// 启动MINA服务
		minaServer = (MinaServer) app.getBean("minaServer");
		minaServer.startServer();
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		if(minaServer != null){
			minaServer.stopServer();
		}
	}
	
	/**
	 * 初始化缓存
	 */
	private void initCache(){
		//初始化终端缓存数据
		ITerminalCacheManager terminalCacheManager = (ITerminalCacheManager) app.getBean("terminalRedisImpl");
		terminalCacheManager.initCache();
		IVehicleCacheManager vehicleCacheManager = (IVehicleCacheManager) app.getBean("vehicleRedisImpl");
		vehicleCacheManager.initCache();
		ITmnlVehiCacheManager tmnlVehiCacheManager = (ITmnlVehiCacheManager) app.getBean("tmnlVehiRedisImpl");
		tmnlVehiCacheManager.initCache();
	}
	
	/**
	 *启动业务处理服务
	 */
	private void startBusi(){
		//上行处理器
		UpDataHandler upDataHandler = (UpDataHandler) app.getBean("upDataHandler");
		//下行处理器
		DownDataHandler downDataHandler = (DownDataHandler) app.getBean("downDataHandler");
		//GPS处理器'
		GpsHandler gpsHandler = (GpsHandler) app.getBean("gpsHandler");
		//TODO:启动上行、下行消息处理器 
		upDataHandler.startHandler();
		downDataHandler.startHandler();
		gpsHandler.startHandler();
	}

}
