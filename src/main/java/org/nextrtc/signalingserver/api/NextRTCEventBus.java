package org.nextrtc.signalingserver.api;

import com.google.common.eventbus.EventBus;
import lombok.extern.slf4j.Slf4j;
import org.nextrtc.signalingserver.Names;
import org.nextrtc.signalingserver.api.dto.NextRTCEvent;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

@Slf4j
@Service(Names.EVENT_BUS)
@Scope("singleton")
public class NextRTCEventBus {
    private EventBus eventBus;

    public NextRTCEventBus() {
        this.eventBus = new EventBus();
    }

    public void post(NextRTCEvent event) {
        if (event.type() != NextRTCEvents.MESSAGE) {
            log.info("POSTED EVENT: " + event);
        }
        eventBus.post(event);
    }

    @Deprecated
    public void post(Object o) {
        eventBus.post(o);
    }

    public void register(Object listeners) {
        log.info("REGISTERED LISTENER: " + listeners);
        eventBus.register(listeners);
    }

}
