package org.nextrtc.signalingserver.api;

import org.nextrtc.signalingserver.api.dto.NextRTCEvent;

public interface NextRTCHandler {

    void handleEvent(NextRTCEvent event);

}
