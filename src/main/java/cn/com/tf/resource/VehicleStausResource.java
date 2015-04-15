package cn.com.tf.resource;

import java.util.Date;

import net.sf.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.com.tf.cache.IDataAcquireCacheManager;
import cn.com.tf.cache.IRunningStatusCacheManager;
import cn.com.tf.cache.ITerminalCacheManager;
import cn.com.tf.cache.ITmnlVehiCacheManager;
import cn.com.tf.cache.ITripCacheManager;
import cn.com.tf.cache.IVehicleCacheManager;
import cn.com.tf.job.GenerateDailyStatJob;
import cn.com.tf.job.GenerateTripJob;
import cn.com.tf.model.RunningState;
import cn.com.tf.tool.DateUtil;

/**
 * 车辆状态查询接口
 * @author tianfei
 *
 */
@Controller
@RequestMapping("vstatus")
public class VehicleStausResource {
	
	@Autowired
	private IDataAcquireCacheManager dataAcquireCacheManager;
	
	@Autowired
	private IRunningStatusCacheManager runningStatusCacheManager;
	
	@Autowired
	private ITripCacheManager tripCacheManager;
	
	@Autowired
	private GenerateDailyStatJob generateDailyStatJob;
	
	@Autowired
	private GenerateTripJob generateTripJob;
	
	@Autowired
	private ITerminalCacheManager terminalCacheManager;
	
	@Autowired
	private IVehicleCacheManager vehicleCacheManager;
	
	@Autowired
	private ITmnlVehiCacheManager tmnlVehCacheManager;
	
	@RequestMapping
	public @ResponseBody String desc(){
		JSONObject json = new JSONObject();
		json.put("gps?vid", "车辆当前位置信息");
		json.put("rstatus?vid", "查询车辆当前运行状态");
		json.put("trip?vid", "查询车辆当前轨迹点");
		json.put("dailyJob", "生成日统计");
		json.put("tripJob", "生成轨迹");
		
		return json.toString();
	}

	/**
	 * 查询车辆当前位置信息
	 * @param vehicleId
	 * @return
	 */
	@RequestMapping(value = "gps",method=RequestMethod.GET)
	public @ResponseBody String getVehicleGps(@RequestParam("vid")int vehicleId){
		JSONObject json = dataAcquireCacheManager.getGps(vehicleId);
		if(json != null){
			return json.toString();
		}
		return "没有GPS";
	}
	
	/**
	 * 查询车辆当前运行状态
	 * @param vehicleId
	 * @return
	 */
	@RequestMapping(value="rstatus",method=RequestMethod.GET)
	public @ResponseBody String getRunningSatus(@RequestParam("vid")int vehicleId) { 
		Date occurTime = DateUtil.addDate(DateUtil.formatDate(new Date()), 0);
		RunningState rs = runningStatusCacheManager.findLatestRunningState(vehicleId, occurTime);
		if(rs != null){
			return JSONObject.fromObject(rs).toString();
		}
		return "没有车辆运行状态";
	}
	
	/**
	 * 查询车辆当前轨迹点
	 * @param vehicleId
	 * @return
	 */
	@RequestMapping(value="trip",method=RequestMethod.GET)
	public @ResponseBody String getTrip(@RequestParam("vid")int vehicleId){
		Date occurTime = DateUtil.addDate(DateUtil.formatDate(new Date()), 0);
		int occurDay = Integer.parseInt(DateUtil.DATEFORMATER().format(occurTime));
		JSONObject json = tripCacheManager.getGpsTrip(vehicleId, occurDay);
		if(json != null){
			return json.toString();
		}
		return "没有轨迹点";
	}
	
	/**
	 * 生成日统计
	 * @return
	 */
	@RequestMapping(value="dailyJob",method=RequestMethod.GET)
	public @ResponseBody String generateDailyStatJob(){
		generateDailyStatJob.execute();
		return "success";
	}
	
	/**
	 * 生成日行程
	 * @return
	 */
	@RequestMapping(value="tripJob",method=RequestMethod.GET)
	public @ResponseBody String generateTripJob(){
		generateTripJob.execute();
		
		return "success";
	}
	
	/**
	 * 刷新缓存信息
	 * @return
	 */
	@RequestMapping(value="refreshCache",method=RequestMethod.GET)
	public @ResponseBody String refreshCache(){
		terminalCacheManager.initCache();
		vehicleCacheManager.initCache();
		tmnlVehCacheManager.initCache();
		
		return "success";
	}
	
	
}
