package org.nextrtc.signalingserver.eventbus;

import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.Subscribe;
import org.nextrtc.signalingserver.api.NextRTCEvents;
import org.nextrtc.signalingserver.api.NextRTCHandler;
import org.nextrtc.signalingserver.api.annotation.NextRTCEventListener;
import org.nextrtc.signalingserver.api.dto.NextRTCEvent;
import org.springframework.aop.framework.Advised;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Map;

import static org.springframework.core.annotation.AnnotationUtils.getValue;

@Component("nextRTCEventDispatcher")
@Scope("singleton")
@NextRTCEventListener
public class EventDispatcher {

	@Autowired
	private ApplicationContext context;

	@Subscribe
	@AllowConcurrentEvents
	public void handle(NextRTCEvent event) {
		getNextRTCEventListeners().stream()
				.filter(listener -> isNextRTCHandler(listener) && supportsCurrentEvent(listener, event))
				.forEach(listener -> {
					((NextRTCHandler) listener).handleEvent(event);
				});
	}

	private boolean isNextRTCHandler(Object listener) {
		return listener instanceof NextRTCHandler;
	}

	private boolean supportsCurrentEvent(Object listener, NextRTCEvent event) {
	    NextRTCEvents[] events = getSupportedEvents(listener);
		for (NextRTCEvents supportedEvent : events) {
			if (isSupporting(event, supportedEvent)) {
				return true;
			}
		}
		return false;
	}

	private boolean isSupporting(NextRTCEvent msg, NextRTCEvents supportedEvent) {
		return supportedEvent.equals(msg.type());
	}

	private NextRTCEvents[] getSupportedEvents(Object listener) {
	    try {
            if (AopUtils.isJdkDynamicProxy(listener)) {
                listener = ((Advised) listener).getTargetSource().getTarget();
            }
        } catch (Exception e) {
            return new NextRTCEvents[0];
        }
		return (NextRTCEvents[]) getValue(listener.getClass().getAnnotation(NextRTCEventListener.class));
	}

	private Collection<Object> getNextRTCEventListeners() {
		Map<String, Object> beans = context.getBeansWithAnnotation(NextRTCEventListener.class);
		beans.remove("nextRTCEventDispatcher");
		return beans.values();
	}
}
