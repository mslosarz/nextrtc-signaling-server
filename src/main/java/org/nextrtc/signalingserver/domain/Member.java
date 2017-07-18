package org.nextrtc.signalingserver.domain;

import lombok.Getter;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.nextrtc.signalingserver.api.NextRTCEventBus;
import org.nextrtc.signalingserver.api.dto.NextRTCMember;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import javax.websocket.Session;
import java.util.Optional;
import java.util.concurrent.ScheduledFuture;

import static org.nextrtc.signalingserver.api.NextRTCEvents.MEMBER_JOINED;
import static org.nextrtc.signalingserver.api.NextRTCEvents.MEMBER_LEFT;
import static org.nextrtc.signalingserver.domain.EventContext.builder;

@Getter
@Component
@Scope("prototype")
public class Member implements NextRTCMember {

    private String id;
    private Session session;
    private Conversation conversation;

    private NextRTCEventBus eventBus;

    private ScheduledFuture<?> ping;

    public Member(Session session, ScheduledFuture<?> ping) {
        this.id = session.getId();
        this.session = session;
        this.ping = ping;
    }

    public Optional<Conversation> getConversation() {
        return Optional.ofNullable(conversation);
    }

    public void markLeft() {
        ping.cancel(true);
    }

    void assign(Conversation conversation) {
        this.conversation = conversation;
        eventBus.post(MEMBER_JOINED.basedOn(
                builder()
                        .conversation(conversation)
                        .from(this)));
    }

    public void unassignConversation(Conversation conversation) {
        eventBus.post(MEMBER_LEFT.basedOn(
                builder()
                        .conversation(conversation)
                        .from(this)));
        this.conversation = null;
    }

    public boolean hasSameConversation(Member to) {
        return to != null && conversation.equals(to.conversation);
    }

    @Inject
    public void setEventBus(NextRTCEventBus eventBus) {
        this.eventBus = eventBus;
    }

    @Override
    public String toString() {
        return String.format("%s", id);
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Member)) {
            return false;
        }
        Member m = (Member) o;
        return new EqualsBuilder()//
                .append(m.id, id)//
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()//
                .append(id)//
                .build();
    }
}
