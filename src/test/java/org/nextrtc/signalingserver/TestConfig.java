package org.nextrtc.signalingserver;

import org.mockito.Answers;
import org.mockito.Mockito;
import org.nextrtc.signalingserver.api.NextRTCEventBus;
import org.springframework.context.annotation.*;

import java.util.concurrent.ScheduledExecutorService;

@Configuration
@ComponentScan(basePackages = "org.nextrtc.signalingserver")
@PropertySource("classpath:nextrtc.properties")
public class TestConfig {

    @Primary
    @Bean(name = Names.EVENT_BUS)
    public NextRTCEventBus eventBus() {
        return new NextRTCEventBus();
    }

    @Primary
    @Bean(name = Names.SCHEDULER_NAME)
    public ScheduledExecutorService scheduler() {
        return Mockito.mock(ScheduledExecutorService.class, Answers.RETURNS_DEEP_STUBS.get());
    }

}
