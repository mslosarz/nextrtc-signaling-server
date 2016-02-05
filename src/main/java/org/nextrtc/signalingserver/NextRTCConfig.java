package org.nextrtc.signalingserver;

import java.util.concurrent.ScheduledExecutorService;

import org.nextrtc.signalingserver.api.NextRTCEventBus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.scheduling.concurrent.ScheduledExecutorFactoryBean;

@Configuration
@ComponentScan(basePackageClasses = { NextRTCConfig.class })
public class NextRTCConfig {

    @Value("${nextrtc.scheduler_size:10}")
    private int size;

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
        ScheduledExecutorFactoryBean factoryBean = new ScheduledExecutorFactoryBean();
        factoryBean.setThreadNamePrefix("NextRTCConfig");
        factoryBean.setPoolSize(size);
        factoryBean.afterPropertiesSet();
        return factoryBean.getObject();
	}
}
