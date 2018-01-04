package org.nextrtc.signalingserver.domain;

import lombok.Getter;
import org.nextrtc.signalingserver.api.dto.NextRTCConversation;
import org.nextrtc.signalingserver.cases.LeftConversation;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Getter
@Component
@Scope("prototype")
public abstract class Conversation implements NextRTCConversation {
    protected static final ExecutorService parallel = Executors.newCachedThreadPool();
    protected final String id;

    private LeftConversation leftConversation;
    protected MessageSender messageSender;

    public Conversation(String id) {
        this.id = id;
    }

    public Conversation(String id,
                        LeftConversation leftConversation,
                        MessageSender messageSender) {
        this.id = id;
        this.leftConversation = leftConversation;
        this.messageSender = messageSender;
    }

    public abstract void join(Member sender);

    public synchronized void left(Member sender) {
        if (remove(sender) && isWithoutMember()) {
            leftConversation.destroy(this, sender);
        }
    }

    protected abstract boolean remove(Member leaving);

    protected void assignSenderToConversation(Member sender) {
        sender.assign(this);
    }

    public abstract boolean isWithoutMember();

    public abstract boolean has(Member from);

    public abstract void exchangeSignals(InternalMessage message);

    protected void sendJoinedToConversation(Member sender, String id) {
        messageSender.send(InternalMessage.create()//
                .to(sender)//
                .content(id)//
                .signal(Signal.JOINED)//
                .build());
    }

    protected void sendJoinedFrom(Member sender, Member member) {
        messageSender.send(InternalMessage.create()//
                .from(sender)//
                .to(member)//
                .signal(Signal.NEW_JOINED)//
                .content(sender.getId())
                .build());
    }

    protected void sendLeftMessage(Member leaving, Member recipient) {
        messageSender.send(InternalMessage.create()//
                .from(leaving)//
                .to(recipient)//
                .signal(Signal.LEFT)//
                .build());
    }

    public abstract void broadcast(Member from, InternalMessage message);

    @Inject
    public void setLeftConversation(LeftConversation leftConversation) {
        this.leftConversation = leftConversation;
    }

    @Inject
    public void setMessageSender(MessageSender messageSender) {
        this.messageSender = messageSender;
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + ": " + id;
    }
}
