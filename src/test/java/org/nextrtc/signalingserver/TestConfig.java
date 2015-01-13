package org.nextrtc.signalingserver;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import com.google.common.eventbus.EventBus;

@Configuration
@ComponentScan(basePackages = "org.nextrtc.signalingserver")
public class TestConfig {

	@Bean(name = "nextRTCEventBus")
	public EventBus eventBus() {
		return new EventBus();
	}

}
