package org.nextrtc.signalingserver;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import org.springframework.context.annotation.*;

import com.google.common.eventbus.EventBus;

@Configuration
@ComponentScan(basePackages = "org.nextrtc.signalingserver")
@PropertySource("classpath:application.properties")
public class TestConfig {

	@Primary
	@Bean(name = "nextRTCEventBus")
	public EventBus eventBus() {
		return new EventBus();
	}

	@Primary
	@Bean(name = "nextRTCPingScheduler")
	public ScheduledExecutorService scheduler() {
		return Executors.newScheduledThreadPool(1);
	}

}
