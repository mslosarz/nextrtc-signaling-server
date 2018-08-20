package org.nextrtc.signalingserver.domain;

import com.google.common.collect.Maps;
import org.nextrtc.signalingserver.api.NextRTCEvents;
import org.nextrtc.signalingserver.api.dto.NextRTCConversation;
import org.nextrtc.signalingserver.api.dto.NextRTCEvent;
import org.nextrtc.signalingserver.api.dto.NextRTCMember;
import org.nextrtc.signalingserver.exception.SignalingException;

import java.time.ZonedDateTime;
import java.util.Map;
import java.util.Optional;

import static java.util.Collections.unmodifiableMap;
import static java.util.Optional.ofNullable;
import static org.apache.commons.lang3.StringUtils.defaultString;

public class EventContext implements NextRTCEvent {

    private final NextRTCEvents type;
    private final ZonedDateTime published = ZonedDateTime.now();
    private final Map<String, String> custom = Maps.newHashMap();
    private final NextRTCMember from;
    private final NextRTCMember to;
    private final NextRTCConversation conversation;
    private final SignalingException exception;
    private final String content;

    private EventContext(NextRTCEvents type, NextRTCMember from, NextRTCMember to, NextRTCConversation conversation, SignalingException exception, String content) {
        this.type = type;
        this.from = from;
        this.to = to;
        this.conversation = conversation;
        this.exception = exception;
        this.content = content;
    }

    @Override
    public NextRTCEvents type() {
        return type;
    }

    @Override
    public ZonedDateTime published() {
        return published;
    }

    @Override
    public Optional<NextRTCMember> from() {
        return ofNullable(from);
    }

    @Override
    public Optional<NextRTCMember> to() {
        return ofNullable(to);
    }

    @Override
    public Optional<NextRTCConversation> conversation() {
        return ofNullable(conversation);
    }

    @Override
    public Optional<SignalingException> exception() {
        return ofNullable(exception);
    }

    @Override
    public Map<String, String> custom() {
        return unmodifiableMap(custom);
    }

    @Override
    public String content() {
        return defaultString(content);
    }

    @Override
    public String toString() {
        return String.format("%s (%s -> %s) | conv: %s | %s", type, from, to, conversation, custom);
    }

    public static EventContextBuilder builder() {
        return new EventContextBuilder();
    }

    public static class EventContextBuilder {
        private Map<String, String> custom;
        private NextRTCEvents type;
        private NextRTCMember from;
        private NextRTCMember to;
        private NextRTCConversation conversation;
        private SignalingException exception;
        private String content;

        public EventContextBuilder content(String content) {
            this.content = content;
            return this;
        }

        public EventContextBuilder type(NextRTCEvents type) {
            this.type = type;
            return this;
        }

        public EventContextBuilder custom(Map<String, String> custom) {
            this.custom = custom;
            return this;
        }

        public EventContextBuilder from(NextRTCMember from) {
            this.from = from;
            return this;
        }

        public EventContextBuilder to(NextRTCMember to) {
            this.to = to;
            return this;
        }

        public EventContextBuilder conversation(NextRTCConversation conversation) {
            this.conversation = conversation;
            return this;
        }

        public EventContextBuilder exception(SignalingException exception) {
            this.exception = exception;
            return this;
        }

        public NextRTCEvent build() {
            if (type == null) {
                throw new IllegalArgumentException("Type is required");
            }
            EventContext eventContext = new EventContext(type, from, to, conversation, exception, content);
            if (custom != null) {
                eventContext.custom.putAll(custom);
            }
            return eventContext;
        }
    }
}
