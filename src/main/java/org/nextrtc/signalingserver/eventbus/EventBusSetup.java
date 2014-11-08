package org.nextrtc.signalingserver.eventbus;

import java.util.Map;

import javax.annotation.PostConstruct;

import org.nextrtc.signalingserver.api.annotation.NextRTCEventListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.google.common.eventbus.EventBus;

@Component("nextRTCEventBusSetup")
@Scope("singleton")
public class EventBusSetup {

	@Autowired
	@Qualifier("nextRTCEventBus")
	private EventBus eventBus;

	@Autowired
	private ApplicationContext context;

	@PostConstruct
	public void setupHandlers(){
		Map<String, Object> beans = context.getBeansWithAnnotation(NextRTCEventListener.class);
		for (Object listeners : beans.values()) {
			eventBus.register(listeners);
		}
	}
}
