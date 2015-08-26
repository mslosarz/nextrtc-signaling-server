package org.nextrtc.signalingserver.api;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;

import lombok.experimental.Builder;

import com.google.common.collect.Maps;

@Builder
public class EventContext implements NextRTCEvent {

    private final NextRTCEvents type;
    private final String sessionId;
    private final String conversationId;
    private final String message;
    private Map<String, String> custom = Maps.newHashMap();

    EventContext(NextRTCEvents type, //
            String sessionId, //
            String conversationId, //
            String message,//
            Map<String, String> custom) {
        this.type = type;
        this.sessionId = sessionId;
        this.conversationId = conversationId;
        this.message = message;
        if (custom != null) {
            this.custom.putAll(custom);
        }
    }

    @Override
    public NextRTCEvents getType() {
        return type;
    }

    @Override
    public Optional<String> getMessage() {
        return Optional.of(message);
    }

    @Override
    public Map<String, String> getCustom() {
        return Collections.unmodifiableMap(custom);
    }

    @Override
    public Optional<String> getConversationId() {
        return Optional.of(conversationId);
    }

    @Override
    public Optional<String> getSessionId() {
        return Optional.of(sessionId);
    }

    @Override
    public String toString() {
        return String.format("%s (%s) ---> %s", type, sessionId, message);
    }

}
