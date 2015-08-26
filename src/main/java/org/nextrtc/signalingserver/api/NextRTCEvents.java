package org.nextrtc.signalingserver.api;

import javax.websocket.Session;

import org.nextrtc.signalingserver.domain.InternalMessage;

public enum NextRTCEvents {
	SESSION_OPENED, //
	SESSION_CLOSED, //
	UNEXPECTED_SITUATION, //
	CONVERSATION_CREATED, //
	MEMBER_JOINDED, //
	MEMBER_LOCAL_STREAM_CREATED, //
	MEMBER_LEFT, //
	CONVERSATION_CLOSED, //
	;

	public NextRTCEvent basedOn(InternalMessage message) {
        return EventContext.builder().type(this).build();
	}

    public NextRTCEvent occurFor(Session session, String reason) {
        return EventContext.builder()//
                .type(this)//
                .sessionId(session.getId())//
                .message(reason)//
                .build();
    }

    public NextRTCEvent occurFor(Session session) {
        return EventContext.builder()//
                .type(this)//
                .sessionId(session.getId())//
                .build();
    }
}
