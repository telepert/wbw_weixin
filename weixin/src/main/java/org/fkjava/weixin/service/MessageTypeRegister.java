package org.fkjava.weixin.service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.fkjava.commons.domain.InMessage;
import org.fkjava.commons.domain.event.EventInMessage;
import org.fkjava.commons.domain.text.TextInMessage;
//注册消息的负责类
public class MessageTypeRegister {

	private static Map<String, Class<? extends InMessage>> typeMap = new ConcurrentHashMap<>();

	static {
		// 使用静态代码块直接完成类型和类之间的映射..映射是啥？？。。。。。
		register("text", TextInMessage.class);
		register("event", EventInMessage.class);
		register("location", TextInMessage.class);
		register("image", TextInMessage.class);
		register("video", TextInMessage.class);
		register("shortvideo", TextInMessage.class);
		register("voice", TextInMessage.class);
	}

	public static void register(String type, Class<? extends InMessage> cla) {//type类型，cla对象
		typeMap.put(type, cla);
	}
	public static Class<? extends InMessage> getClass(String type) {
		return typeMap.get(type);
	}
}
