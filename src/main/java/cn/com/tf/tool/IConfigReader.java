package cn.com.tf.tool;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public interface IConfigReader {
	
	 	public InputStream getResourceAsStream(String filePath) throws FileNotFoundException;

	    public Properties getResourceAsProperties(String filePath) throws IOException;

	    public String[] getPropertyFiles();
}
