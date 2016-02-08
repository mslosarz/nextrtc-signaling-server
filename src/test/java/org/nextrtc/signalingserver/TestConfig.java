package org.nextrtc.signalingserver;

import java.util.concurrent.ScheduledExecutorService;

import org.mockito.Answers;
import org.mockito.Mockito;
import org.nextrtc.signalingserver.api.NextRTCEventBus;
import org.springframework.context.annotation.*;

@Configuration
@ComponentScan(basePackages = "org.nextrtc.signalingserver")
@PropertySource("classpath:nextrtc.properties")
public class TestConfig {

	@Primary
	@Bean(name = "nextRTCEventBus")
	public NextRTCEventBus eventBus() {
		return new NextRTCEventBus();
	}

	@Primary
	@Bean(name = "nextRTCPingScheduler")
	public ScheduledExecutorService scheduler() {
        return Mockito.mock(ScheduledExecutorService.class, Answers.RETURNS_DEEP_STUBS.get());
	}

}
