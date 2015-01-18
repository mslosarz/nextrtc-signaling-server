package org.nextrtc.signalingserver.api.annotation;

import org.nextrtc.signalingserver.api.NextRTCEvent;
import org.nextrtc.signalingserver.domain.InternalMessage;

public enum NextRTCEvents implements NextRTCEvent {
	SESSION_STARTED, //
	SESSION_CLOSED, //
	UNEXPECTED_SITUATION, //
	CONVERSATION_CREATED, //
	MEMBER_JOINDED, //
	MEMBER_LOCAL_STREAM_CREATED, //
	;

	public NextRTCEvents getType() {
		return this;
	}

	public NextRTCEvent basedOn(InternalMessage message) {
		return this;
	}
}
