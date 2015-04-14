package cn.com.tf.handler.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import cn.com.tf.handler.IJt808Handler;
import cn.com.tf.protocol.EMsgAck;
import cn.com.tf.protocol.Jt808Message;
import cn.com.tf.protocol.Jt808MessageHead;
import cn.com.tf.protocol.impl.JT0102;
import cn.com.tf.protocol.impl.JT8001;
import cn.com.tf.tool.JT808Constants;

/**
 * 终端鉴权处理器
 * @author tianfei
 *
 */
@Component("jt0102Handler")
public class JT0102Handler extends IJt808Handler {
	private static Logger logger = LoggerFactory.getLogger(JT0102Handler.class);
	
	@Override
	public void handle(Jt808Message msg) {
		JT0102 body = (JT0102) msg.getBody();
		String code = body.getAuthorCode();
		//判断终端鉴权码是否正确 
		if(!JT808Constants.AUTHENTICATION_CODE.equals(code)){
			logger.error("终端鉴权失败，"+msg.getSimNo());
			optResult = EMsgAck.FAILURE.value();
		} else {
			//设置连接鉴权成功
			msg.getConn().setAuth(true);
		}
		//消息回复
		Jt808MessageHead head = msg.getHead();
		head.setMessageId(0x8001);
		JT8001 rbody = new JT8001(head.getFlowNo(),head.getMessageId(), optResult);
		Jt808Message response = new Jt808Message(head,rbody);
		writeResponse(response);
	}
}
