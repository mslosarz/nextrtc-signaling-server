package org.nextrtc.signalingserver;

import org.nextrtc.signalingserver.api.NextRTCEventBus;
import org.nextrtc.signalingserver.property.NextRTCProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.concurrent.ScheduledExecutorFactoryBean;

import java.util.concurrent.ScheduledExecutorService;

@Configuration
@ComponentScan(basePackageClasses = {NextRTCConfig.class})
public class NextRTCConfig {

    @Autowired
    private NextRTCProperties properties;

    @Bean(name = Names.EVENT_BUS)
    public NextRTCEventBus eventBus() {
        return new NextRTCEventBus();
    }

    @Bean
    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        PropertySourcesPlaceholderConfigurer propertyPlaceholderConfigurer = new PropertySourcesPlaceholderConfigurer();
        propertyPlaceholderConfigurer.setLocation(new ClassPathResource("nextrtc.properties"));
        return propertyPlaceholderConfigurer;
    }

    @Bean(name = Names.SCHEDULER_NAME)
    public ScheduledExecutorService scheduler() {
        ScheduledExecutorFactoryBean factoryBean = new ScheduledExecutorFactoryBean();
        factoryBean.setThreadNamePrefix("NextRTCConfig");
        factoryBean.setPoolSize(properties.getSchedulerPoolSize());
        factoryBean.afterPropertiesSet();
        return factoryBean.getObject();
    }
}
