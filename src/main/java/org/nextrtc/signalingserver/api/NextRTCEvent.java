package org.nextrtc.signalingserver.api;

import java.util.Map;
import java.util.Optional;

public interface NextRTCEvent {

	NextRTCEvents getType();

    Optional<String> getSessionId();

    Optional<String> getConversationId();

    Optional<String> getMessage();

    Map<String, String> getCustom();

}
