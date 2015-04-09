package cn.com.tf.server;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import cn.com.tf.cache.ITerminalCacheManager;
import cn.com.tf.cache.ITmnlVehiCacheManager;
import cn.com.tf.cache.IVehicleCacheManager;

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
	
	private void initCache(){
		//初始化终端缓存数据
		ITerminalCacheManager terminalCacheManager = (ITerminalCacheManager) app.getBean("terminalRedisImpl");
		terminalCacheManager.initCache();
		IVehicleCacheManager vehicleCacheManager = (IVehicleCacheManager) app.getBean("vehicleRedisImpl");
		vehicleCacheManager.initCache();
		ITmnlVehiCacheManager tmnlVehiCacheManager = (ITmnlVehiCacheManager) app.getBean("tmnlVehiRedisImpl");
		tmnlVehiCacheManager.initCache();
	}

}
