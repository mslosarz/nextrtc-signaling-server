package org.nextrtc.signalingserver.api;

import lombok.extern.log4j.Log4j;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.google.common.eventbus.EventBus;

@Log4j
@Service("nextRTCEventBus")
@Scope("singleton")
public class NextRTCEventBus {

	private EventBus eventBus;

	public NextRTCEventBus() {
		this.eventBus = new EventBus();
	}

	public void post(NextRTCEvent event) {
        log.info("POSTED EVENT: " + event);
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
