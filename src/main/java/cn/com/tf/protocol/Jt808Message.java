package cn.com.tf.protocol;

import java.nio.ByteBuffer;

import cn.com.tf.tool.Tools;

/**
 * JT808消息体
 * @author tianfei
 *
 */
public class Jt808Message {
	
	private Jt808MessageHead head;		//头消息体
	
	private Jt808MessageBody body;		//消息体
	
	public Jt808Message(){}
	
	public Jt808Message(Jt808MessageHead head,Jt808MessageBody body){
		this.head = head;
		this.body = body;
		head.setBodyLength((short)body.getLength());
	}
	
	public Jt808Message(ByteBuffer buff){
		this.head = new Jt808MessageHead(buff);
	}
	
	public String getMessageID(){
		return Tools.ToHexString4Short((short) head.getMessageId());
	}
	
	public String getSimNo(){
		return head.getTephone();
	}

	public Jt808MessageHead getHead() {
		return head;
	}

	public void setHead(Jt808MessageHead head) {
		this.head = head;
	}

	public Jt808MessageBody getBody() {
		return body;
	}

	public void setBody(Jt808MessageBody body) {
		this.body = body;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return head.toString()+"  消息体内容："+body.toString();
	}
	
	public static void main(String[] args){
		System.out.println(Integer.toHexString(1 & 0xffff | 0x10000).substring(1));
	}
}
