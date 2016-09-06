package org.nextrtc.signalingserver.domain;

import org.nextrtc.signalingserver.EventChecker;
import org.nextrtc.signalingserver.api.annotation.NextRTCEventListener;

import static org.nextrtc.signalingserver.api.NextRTCEvents.CONVERSATION_CREATED;
import static org.nextrtc.signalingserver.api.NextRTCEvents.SESSION_OPENED;

@NextRTCEventListener({SESSION_OPENED, CONVERSATION_CREATED})
public class ServerEventCheck extends EventChecker {

}
