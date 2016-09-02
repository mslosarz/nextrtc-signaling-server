package org.nextrtc.signalingserver.cases;

import org.nextrtc.signalingserver.domain.InternalMessage;

public interface SignalHandler {
    void execute(InternalMessage message);
}
