package org.nextrtc.signalingserver;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import org.nextrtc.signalingserver.api.NextRTCEventBus;
import org.springframework.context.annotation.*;

@Configuration
@ComponentScan(basePackages = "org.nextrtc.signalingserver")
@PropertySource("classpath:application.properties")
public class TestConfig {

	@Primary
	@Bean(name = "nextRTCEventBus")
	public NextRTCEventBus eventBus() {
		return new NextRTCEventBus();
	}

	@Primary
	@Bean(name = "nextRTCPingScheduler")
	public ScheduledExecutorService scheduler() {
		return Executors.newScheduledThreadPool(1);
	}

}
