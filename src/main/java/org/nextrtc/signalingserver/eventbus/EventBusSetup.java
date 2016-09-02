package org.nextrtc.signalingserver.eventbus;

import org.nextrtc.signalingserver.Names;
import org.nextrtc.signalingserver.api.NextRTCEventBus;
import org.nextrtc.signalingserver.api.annotation.NextRTCEventListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component("nextRTCEventBusSetup")
@Scope("singleton")
public class EventBusSetup {

    @Autowired
    @Qualifier(Names.EVENT_BUS)
    private NextRTCEventBus eventBus;

    @Autowired
    private ApplicationContext context;

    @PostConstruct
    public void setupHandlers() {
        context.getBeansWithAnnotation(NextRTCEventListener.class).values()
                .forEach(eventBus::register);
    }
}
