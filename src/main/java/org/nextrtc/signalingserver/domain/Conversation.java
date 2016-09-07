package org.nextrtc.signalingserver.domain;

import org.nextrtc.signalingserver.api.dto.NextRTCConversation;
import org.nextrtc.signalingserver.repository.Conversations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
public abstract class Conversation implements NextRTCConversation {

    protected final String id;

    @Autowired
    private Conversations conversations;

    public Conversation(String id) {
        this.id = id;
    }

    public abstract void join(Member sender);

    public void left(Member sender) {
        if (remove(sender)) {
            if (isWithoutMember()) {
                unregisterConversation(sender, this);
            }
        }
    }

    protected abstract boolean remove(Member leaving);

    protected void assignSenderToConversation(Member sender) {
        sender.assign(this);
    }

    public abstract boolean isWithoutMember();

    public abstract boolean has(Member from);

    private void unregisterConversation(Member sender, Conversation conversation) {
        conversations.remove(conversation.getId(), sender);
    }

    public String getId() {
        return this.id;
    }

    public abstract void exchangeSignals(InternalMessage message);

    protected void sendJoinedToConversation(Member sender, String id) {
        InternalMessage.create()//
                .to(sender)//
                .content(id)//
                .signal(Signal.JOINED)//
                .build()//
                .send();
    }

    protected void sendJoinedFrom(Member sender, Member member) {
        InternalMessage.create()//
                .from(sender)//
                .to(member)//
                .signal(Signal.JOINED)//
                .build()//
                .send();
    }

    protected void sendLeftMessage(Member leaving, Member recipient) {
        InternalMessage.create()//
                .from(leaving)//
                .to(recipient)//
                .signal(Signal.LEFT)//
                .build()//
                .send();
    }

    public abstract void broadcast(Member from, InternalMessage message);
}
