package org.nextrtc.signalingserver.eventbus;

import org.nextrtc.signalingserver.api.NextRTCHandler;
import org.nextrtc.signalingserver.api.annotation.NextRTCEventListener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@NextRTCEventListener
public class ManualEventDispatcher extends AbstractEventDispatcher {

    List<Object> events = new ArrayList<>();

    protected Collection<Object> getNextRTCEventListeners() {
        return events;
    }

    public void addListener(NextRTCHandler handler) {
        if (handler != null) {
            events.add(handler);
        }
    }
}
