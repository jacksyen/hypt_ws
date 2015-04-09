package cn.com.tf.resource;

import org.glassfish.jersey.server.ResourceConfig;

public class MyApplication extends ResourceConfig  {
	
	public MyApplication() {
		register(PingResource.class);
	}

}
