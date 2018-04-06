package org.nextrtc.signalingserver.eventbus;

import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.Subscribe;
import lombok.extern.slf4j.Slf4j;
import org.nextrtc.signalingserver.api.NextRTCEvents;
import org.nextrtc.signalingserver.api.NextRTCHandler;
import org.nextrtc.signalingserver.api.dto.NextRTCEvent;

import java.util.Collection;

@Slf4j
public abstract class AbstractEventDispatcher implements EventDispatcher {
    @Override
    @Subscribe
    @AllowConcurrentEvents
    public void handle(NextRTCEvent event) {
        getNextRTCEventListeners().parallelStream()
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
        if (events == null) {
            return false;
        }
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

    protected abstract NextRTCEvents[] getSupportedEvents(Object listener);
}
