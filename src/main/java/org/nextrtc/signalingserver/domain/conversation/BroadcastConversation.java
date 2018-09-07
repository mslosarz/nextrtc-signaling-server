package org.nextrtc.signalingserver.domain.conversation;

import com.google.common.collect.Sets;
import org.nextrtc.signalingserver.api.dto.NextRTCMember;
import org.nextrtc.signalingserver.cases.ExchangeSignalsBetweenMembers;
import org.nextrtc.signalingserver.cases.LeftConversation;
import org.nextrtc.signalingserver.domain.Conversation;
import org.nextrtc.signalingserver.domain.InternalMessage;
import org.nextrtc.signalingserver.domain.Member;
import org.nextrtc.signalingserver.domain.Signal;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.io.IOException;
import java.util.Set;

@Component
@Scope("prototype")
public class BroadcastConversation extends Conversation {

    private ExchangeSignalsBetweenMembers exchange;
    private Member broadcaster;
    private Set<Member> audience = Sets.newConcurrentHashSet();

    public BroadcastConversation(String id) {
        super(id);
    }

    public BroadcastConversation(String id,
                                 LeftConversation left,
                                 ExchangeSignalsBetweenMembers exchange) {
        super(id, left);
        this.exchange = exchange;
    }

    @Override
    public synchronized void join(Member sender) {
        assignSenderToConversation(sender);

        informSenderThatHasBeenJoined(sender);

        beginSignalExchangeBetweenBroadcasterAndNewAudience(sender);

        if (!broadcaster.equals(sender)) {
            audience.add(sender);
        }
    }

    @Override
    public synchronized boolean remove(Member leaving) {
        if (broadcaster.equals(leaving)) {
            for (Member member : audience) {
                sendLeftMessage(broadcaster, member);
                sendEndMessage(broadcaster, member);
                member.unassignConversation(this);
            }
            audience.clear();
            broadcaster.unassignConversation(this);
            broadcaster = null;
            return true;
        }
        sendLeftMessage(leaving, broadcaster);
        boolean remove = audience.remove(leaving);
        if (remove) {
            leaving.unassignConversation(this);
        }
        return remove;
    }

    private void sendEndMessage(Member leaving, Member recipient) {
        recipient.send(InternalMessage.create()//
                .from(leaving)//
                .to(recipient)//
                .signal(Signal.END)//
                .content(id)//
                .build()//
        );
    }

    @Override
    public synchronized boolean isWithoutMember() {
        return broadcaster == null && audience.isEmpty();
    }

    @Override
    public synchronized boolean has(Member from) {
        return broadcaster != null
                && (broadcaster.equals(from) || audience.contains(from));
    }

    @Override
    public void exchangeSignals(InternalMessage message) {
        exchange.execute(message);
    }

    @Override
    public void broadcast(Member from, InternalMessage message) {
        audience.stream()
                .filter(member -> !member.equals(from))
                .forEach(to -> to.send(message.copy()
                        .from(from)
                        .to(to)
                        .build()
                ));
        if (from != broadcaster) {
            broadcaster.send(message.copy()
                    .from(from)
                    .to(broadcaster)
                    .build()
            );
        }
    }

    private void informSenderThatHasBeenJoined(Member sender) {
        if (isWithoutMember()) {
            broadcaster = sender;
            sendJoinedToBroadcaster(sender, id);
        } else {
            sendJoinedToConversation(sender, id);
            sendJoinedFrom(broadcaster, sender);
        }
    }

    private void beginSignalExchangeBetweenBroadcasterAndNewAudience(Member sender) {
        if (!sender.equals(broadcaster)) {
            sendJoinedFrom(sender, broadcaster);
            exchange.begin(broadcaster, sender);
        }
    }

    private void sendJoinedToBroadcaster(Member sender, String id) {
        sender.send(InternalMessage.create()//
                .to(sender)//
                .signal(Signal.CREATED)//
                .addCustom("type", "BROADCAST")
                .content(id)//
                .build()//
        );
    }

    @Override
    public void close() throws IOException {
        if (broadcaster != null) {
            remove(broadcaster);
        }
    }

    @Inject
    public void setExchange(ExchangeSignalsBetweenMembers exchange) {
        this.exchange = exchange;
    }

    @Override
    public Member getCreator() {
        return broadcaster;
    }

    @Override
    public Set<NextRTCMember> getMembers() {
        return Sets.newHashSet(audience);
    }
}
