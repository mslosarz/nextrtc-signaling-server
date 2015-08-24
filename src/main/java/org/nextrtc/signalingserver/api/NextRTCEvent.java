package org.nextrtc.signalingserver.api;

import java.util.Optional;

public interface NextRTCEvent {

	NextRTCEvents getType();

    Optional<String> getSessionId();

}
