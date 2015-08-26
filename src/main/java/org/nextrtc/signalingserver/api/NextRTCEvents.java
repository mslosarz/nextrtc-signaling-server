package org.nextrtc.signalingserver.api;

import javax.websocket.Session;

import org.nextrtc.signalingserver.domain.Conversation;
import org.nextrtc.signalingserver.domain.InternalMessage;

public enum NextRTCEvents {
	SESSION_OPENED, //
	SESSION_CLOSED, //
    CONVERSATION_CREATED, //
    CONVERSATION_DESTROYED, //
	UNEXPECTED_SITUATION, //
	MEMBER_JOINDED, //
    MEMBER_LEFT, //
    MEDIA_LOCAL_STREAM_REQUESTED, //
    MEDIA_LOCAL_STREAM_CREATED, //
    MEDIA_STREAMING, //
	;

    public NextRTCEvent basedOn(InternalMessage message, Conversation conv) {
        return EventContext.builder()//
                .sessionId(message.getFrom().getId())//
                .custom(message.getCustom())//
                .conversationId(conv.getId())//
                .type(this)//
                .build();
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
