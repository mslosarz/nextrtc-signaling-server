package org.nextrtc.signalingserver.api;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.google.common.eventbus.EventBus;

@Service("nextRTCEventBus")
@Scope("singleton")
public class NextRTCEventBus {

	private EventBus eventBus;

	public NextRTCEventBus() {
		this.eventBus = new EventBus();
	}

	public void post(NextRTCEvent event) {
		eventBus.post(event);
	}

	@Deprecated
	public void post(Object o) {
		eventBus.post(o);
	}

	public void register(Object listeners) {
		eventBus.register(listeners);
	}

}
