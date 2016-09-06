package org.nextrtc.signalingserver.domain;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.nextrtc.signalingserver.Names;
import org.nextrtc.signalingserver.api.NextRTCEventBus;
import org.nextrtc.signalingserver.api.dto.NextRTCMember;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.websocket.Session;
import java.util.Optional;
import java.util.concurrent.ScheduledFuture;

import static org.nextrtc.signalingserver.api.NextRTCEvents.MEMBER_JOINED;
import static org.nextrtc.signalingserver.api.NextRTCEvents.MEMBER_LEFT;
import static org.nextrtc.signalingserver.domain.EventContext.builder;

@Component
@Scope("prototype")
public class Member implements NextRTCMember {

    private String id;
    private Session session;
    private Conversation conversation;

    @Autowired
    @Qualifier(Names.EVENT_BUS)
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

    public void assign(Conversation conversation) {
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

    public String getId() {
        return this.id;
    }

    public Session getSession() {
        return this.session;
    }

    public boolean hasSameConversation(Member to) {
        if (to == null) {
            return false;
        }
        return conversation.equals(to.conversation);
    }
}
