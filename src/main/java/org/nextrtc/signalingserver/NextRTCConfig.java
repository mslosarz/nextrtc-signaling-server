package org.nextrtc.signalingserver;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import org.nextrtc.signalingserver.api.NextRTCEventBus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

@Configuration
@ComponentScan(basePackageClasses = { NextRTCConfig.class })
public class NextRTCConfig {

	@Bean(name = "nextRTCEventBus")
	public NextRTCEventBus eventBus() {
		return new NextRTCEventBus();
	}

	@Bean
	public PropertySourcesPlaceholderConfigurer conf() {
		return new PropertySourcesPlaceholderConfigurer();
	}

	@Bean(name = "nextRTCPingScheduler")
	public ScheduledExecutorService scheduler() {
		return Executors.newScheduledThreadPool(10);
	}

}
