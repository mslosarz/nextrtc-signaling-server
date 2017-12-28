package org.nextrtc.signalingserver.domain.conversation;

import com.google.common.collect.Sets;
import org.nextrtc.signalingserver.cases.ExchangeSignalsBetweenMembers;
import org.nextrtc.signalingserver.cases.LeftConversation;
import org.nextrtc.signalingserver.domain.*;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.Set;

@Component
@Scope("prototype")
public class MeshConversation extends Conversation {
    private ExchangeSignalsBetweenMembers exchange;

    private Set<Member> members = Sets.newConcurrentHashSet();

    public MeshConversation(String id) {
        super(id);
    }

    public MeshConversation(String id,
                            LeftConversation left,
                            MessageSender sender,
                            ExchangeSignalsBetweenMembers exchange) {
        super(id, left, sender);
        this.exchange = exchange;
    }

    @Override
    public synchronized void join(Member sender) {
        assignSenderToConversation(sender);

        informSenderThatHasBeenJoined(sender);

        informRestAndBeginSignalExchange(sender);

        members.add(sender);
    }

    private void informRestAndBeginSignalExchange(Member sender) {
        for (Member to : members) {
            parallel.execute(() -> {
                sendJoinedFrom(sender, to);
                sendJoinedFrom(to, sender);
                exchange.begin(to, sender);
            });
        }
    }

    private void informSenderThatHasBeenJoined(Member sender) {
        if (isWithoutMember()) {
            sendJoinedToFirst(sender, id);
        } else {
            sendJoinedToConversation(sender, id);
        }
    }

    public synchronized boolean isWithoutMember() {
        return members.isEmpty();
    }

    public synchronized boolean has(Member member) {
        return member != null && members.contains(member);
    }

    @Override
    public void exchangeSignals(InternalMessage message) {
        exchange.execute(message);
    }

    @Override
    public void broadcast(Member from, InternalMessage message) {
        members.stream()
                .filter(member -> !member.equals(from))
                .forEach(to -> messageSender.send(message.copy()
                        .from(from)
                        .to(to)
                        .build()
                ));
    }

    @Override
    public synchronized boolean remove(Member leaving) {
        boolean remove = members.remove(leaving);
        if (remove) {
            leaving.unassignConversation(this);
            parallel.execute(() -> {
                for (Member member : members) {
                    sendLeftMessage(leaving, member);
                }
            });
        }
        return remove;
    }

    private void sendJoinedToFirst(Member sender, String id) {
        messageSender.send(InternalMessage.create()//
                .to(sender)//
                .signal(Signal.CREATED)//
                .addCustom("type", "MESH")
                .content(id)//
                .build()//
        );
    }

    @Inject
    public void setExchange(ExchangeSignalsBetweenMembers exchange) {
        this.exchange = exchange;
    }
}
