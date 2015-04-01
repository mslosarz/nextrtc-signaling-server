package org.nextrtc.signalingserver;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import com.google.common.eventbus.EventBus;

@Configuration
@ComponentScan(basePackageClasses = { NextRTCConfig.class })
public class NextRTCConfig {

	@Bean(name = "nextRTCEventBus")
	public EventBus eventBus() {
		return new EventBus();
	}

	@Bean(name = "nextRTCPingScheduler")
	public ScheduledExecutorService scheduler() {
		return Executors.newScheduledThreadPool(10);
	}

}
