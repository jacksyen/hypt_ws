package cn.com.tf.resource;

import net.sf.json.JSONObject;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("test")
public class PingResource {
	
	@RequestMapping(value="/welcome",method=RequestMethod.GET)
	public String test(){
		System.out.println("hello!");
		return "index";
	}
	
	@RequestMapping(value="/json",method=RequestMethod.POST ,consumes="application/json")
	public @ResponseBody String json(@RequestBody JSONObject jj){
		System.out.println(jj.toString());
		JSONObject json = new JSONObject();
		json.put("name", "tianfei");
		
		return json.toString();
	}

}
