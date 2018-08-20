package org.nextrtc.signalingserver.api;

import org.nextrtc.signalingserver.api.dto.NextRTCEvent;
import org.nextrtc.signalingserver.api.dto.NextRTCMember;
import org.nextrtc.signalingserver.domain.Connection;
import org.nextrtc.signalingserver.domain.Conversation;
import org.nextrtc.signalingserver.domain.EventContext;
import org.nextrtc.signalingserver.domain.InternalMessage;
import org.nextrtc.signalingserver.exception.Exceptions;

public enum NextRTCEvents {
    SESSION_OPENED,
    SESSION_CLOSED,
    CONVERSATION_CREATED,
    CONVERSATION_DESTROYED,
    UNEXPECTED_SITUATION,
    MEMBER_JOINED,
    MEMBER_LEFT,
    MEDIA_LOCAL_STREAM_REQUESTED,
    MEDIA_LOCAL_STREAM_CREATED,
    MEDIA_STREAMING,
    TEXT,
    MESSAGE;

    public NextRTCEvent basedOn(InternalMessage message, Conversation conversation) {
        return EventContext.builder()
                .from(message.getFrom())
                .to(message.getTo())
                .custom(message.getCustom())
                .conversation(conversation)
                .type(this)
                .build();
    }

    public NextRTCEvent basedOn(EventContext.EventContextBuilder builder) {
        return builder
                .type(this)
                .build();
    }

    public NextRTCEvent occurFor(Connection connection, String reason) {
        return EventContext.builder()
                .from(new InternalMember(connection))
                .type(this)
                .exception(Exceptions.UNKNOWN_ERROR.exception())
                .content(reason)
                .build();
    }

    public NextRTCEvent occurFor(Connection connection) {
        return EventContext.builder()
                .type(this)
                .from(new InternalMember(connection))
                .exception(Exceptions.UNKNOWN_ERROR.exception())
                .build();
    }

    public NextRTCEvent basedOn(InternalMessage message) {
        return EventContext.builder()
                .from(message.getFrom())
                .to(message.getTo())
                .custom(message.getCustom())
                .content(message.getContent())
                .conversation(message.getFrom().getConversation().orElse(null))
                .type(this)
                .build();
    }

    private static class InternalMember implements NextRTCMember {

        private final Connection connection;

        InternalMember(Connection connection) {
            this.connection = connection;
        }

        @Override
        public Connection getConnection() {
            return connection;
        }

        @Override
        public String getId() {
            if (connection == null) {
                return null;
            }
            return connection.getId();
        }

        @Override
        public String toString() {
            return getId();
        }
    }
}
