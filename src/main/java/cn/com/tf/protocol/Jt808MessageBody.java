package cn.com.tf.protocol;

import java.nio.ByteBuffer;

/**
 * JT808消息体
 * @author tianfei
 *
 */
public abstract class Jt808MessageBody {
	
	/**
	 * 实体长度
	 * @return
	 */
	public abstract int getLength();
	
	/**
	 * 编译消息体
	 * @return
	 */
	public abstract byte[] encodeBody();
	
	/**
	 * 解析消息体
	 * @param b
	 */
	public abstract void decodeBody(ByteBuffer buff);

}
