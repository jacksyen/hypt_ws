package cn.com.tf.resource;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.springframework.stereotype.Component;

@Component
@Path("test")
public class PingResource {
	
	@GET
	@Path("t")
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	public String test(){
		System.out.println("hello!");
		return "js.p";
	}

}
