package org.nextrtc.signalingserver.eventbus;

import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.Subscribe;
import lombok.extern.log4j.Log4j;
import org.nextrtc.signalingserver.api.NextRTCEvents;
import org.nextrtc.signalingserver.api.NextRTCHandler;
import org.nextrtc.signalingserver.api.annotation.NextRTCEventListener;
import org.nextrtc.signalingserver.api.dto.NextRTCEvent;
import org.springframework.aop.framework.Advised;
import org.springframework.aop.support.AopUtils;

import java.util.Collection;

import static org.springframework.core.annotation.AnnotationUtils.getValue;

@Log4j
public abstract class AbstractEventDispatcher implements EventDispatcher {
    @Override
    @Subscribe
    @AllowConcurrentEvents
    public void handle(NextRTCEvent event) {
        getNextRTCEventListeners().stream()
                .filter(listener -> isNextRTCHandler(listener) && supportsCurrentEvent(listener, event))
                .forEach(listener -> doTry(() -> ((NextRTCHandler) listener).handleEvent(event)));

    }

    protected abstract Collection<Object> getNextRTCEventListeners();

    private void doTry(Runnable action) {
        try {
            action.run();
        } catch (Exception e) {
            log.error("Handler throws an exception", e);
        }
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
}
