package org.fkjava.weixin;

import org.fkjava.commons.EventListenerConfig;
import org.fkjava.commons.domain.event.EventInMessage;
import org.fkjava.commons.processors.EventMessageProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.util.xml.StaxUtils;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
@SpringBootApplication
@ComponentScan(basePackages = "org.fkjava")
@EnableJpaRepositories(basePackages = "org.fkjava")
@EntityScan(basePackages = "org.fkjava")
public class UnSubscribeApplication implements ApplicationContextAware, EventListenerConfig {
	private static final Logger LOG = LoggerFactory.getLogger(UnSubscribeApplication.class);
	private ApplicationContext ctx;

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		ctx = applicationContext;
	}

	@Bean()
	public XmlMapper xmlMapper() {
		XmlMapper mapper = new XmlMapper(StaxUtils.createDefensiveInputFactory());
		return mapper;
	}

	@Override
	public void handleEvent(EventInMessage event) {
		LOG.trace("事件处理程序收到的消息：{}", event);
		String eventType = event.getEvent();// 获取事件类型
		eventType = eventType.toLowerCase();// 转换为小写
		String beanName = eventType + "MessageProcessor";
		EventMessageProcessor mp = (EventMessageProcessor) ctx.getBean(beanName);
		if (mp == null) {
			LOG.error("事件 {} 没有找到对应的处理器", eventType);
		} else {
			mp.onMessage(event);
		}
	}

	public static void main(String[] args) {
		SpringApplication.run(UnSubscribeApplication.class, args);
	}
}
