package org.nextrtc.signalingserver.eventbus;

import static org.springframework.core.annotation.AnnotationUtils.getValue;

import java.util.Collection;
import java.util.Map;

import org.nextrtc.signalingserver.api.NextRTCEvent;
import org.nextrtc.signalingserver.api.NextRTCEvents;
import org.nextrtc.signalingserver.api.NextRTCHandler;
import org.nextrtc.signalingserver.api.annotation.NextRTCEventListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.Subscribe;

@Component("nextRTCEventDispatcher")
@Scope("singleton")
@NextRTCEventListener
public class EventDispatcher {

	@Autowired
	private ApplicationContext context;

	@Subscribe
	@AllowConcurrentEvents
	public void handle(NextRTCEvent event) {
		Collection<Object> listeners = getNextRTCEventListeners();
		for (Object listener : listeners) {
			if (isNextRTCHandler(listener) && supportsCurrentEvent(listener, event)) {
				((NextRTCHandler) listener).handleEvent(event);
			}
		}
	}

	private boolean isNextRTCHandler(Object listener) {
		return listener instanceof NextRTCHandler;
	}

	private boolean supportsCurrentEvent(Object listener, NextRTCEvent event) {
		for (NextRTCEvents supportedEvent : getSupportedEvents(listener)) {
			if (isSupporting(event, supportedEvent)) {
				return true;
			}
		}
		return false;
	}

	private boolean isSupporting(NextRTCEvent msg, NextRTCEvents supportedEvent) {
		return supportedEvent.equals(msg.getType());
	}

	private NextRTCEvents[] getSupportedEvents(Object listener) {
		return (NextRTCEvents[]) getValue(listener.getClass().getAnnotation(NextRTCEventListener.class));
	}

	private Collection<Object> getNextRTCEventListeners() {
		Map<String, Object> beans = context.getBeansWithAnnotation(NextRTCEventListener.class);
		beans.remove("nextRTCEventDispatcher");
		return beans.values();
	}
}
