package org.nextrc.signalingserver.eventbus;

import org.nextrtc.signalingserver.eventbus.EventBusSetup;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import com.google.common.eventbus.EventBus;

@Configuration
@ComponentScan(basePackageClasses = EventBusSetup.class)
public class TestBus {

	@Bean(name = "nextRTCEventBus")
	public EventBus eventBus() {
		return new EventBus();
	}

}
