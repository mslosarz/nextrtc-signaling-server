package org.nextrtc.signalingserver.eventbus;

import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.Subscribe;
import org.nextrtc.signalingserver.api.dto.NextRTCEvent;

public interface EventDispatcher {
    @Subscribe
    @AllowConcurrentEvents
    void handle(NextRTCEvent event);
}
