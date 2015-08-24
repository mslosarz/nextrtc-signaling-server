package org.nextrtc.signalingserver.api;

import java.util.Optional;

import lombok.experimental.Builder;

import org.nextrtc.signalingserver.domain.Conversation;
import org.nextrtc.signalingserver.domain.Member;

@Builder
public class EventContext implements NextRTCEvent {

    private final NextRTCEvents type;
    private final String sessionId;
    private final Conversation conversation;
    private final Member member;
    private final String message;

    @Override
    public NextRTCEvents getType() {
        return type;
    }

    public Optional<Conversation> forConversation() {
        return Optional.ofNullable(conversation);
    }

    public Optional<Member> forMember() {
        return Optional.ofNullable(member);
    }

    public Optional<String> withMessage() {
        return Optional.of(message);
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
