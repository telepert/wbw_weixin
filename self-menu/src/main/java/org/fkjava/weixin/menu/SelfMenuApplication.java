package org.fkjava.weixin.menu;

import org.fkjava.commons.EventListenerConfig;
import org.fkjava.commons.domain.event.EventInMessage;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("org.fkjava")
@EntityScan("org.fkjava")
public class SelfMenuApplication implements EventListenerConfig {
	public static void main(String[] args) {
		SpringApplication.run(SelfMenuApplication.class, args);
	}

	@Override
	public void handleEvent(EventInMessage event) {
	}
}
