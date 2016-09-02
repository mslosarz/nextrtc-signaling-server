package org.nextrtc.signalingserver.domain.conversation;

import com.google.common.collect.Sets;
import org.nextrtc.signalingserver.Names;
import org.nextrtc.signalingserver.api.NextRTCEventBus;
import org.nextrtc.signalingserver.cases.ExchangeSignalsBetweenMembers;
import org.nextrtc.signalingserver.cases.SendingTools;
import org.nextrtc.signalingserver.domain.Conversation;
import org.nextrtc.signalingserver.domain.InternalMessage;
import org.nextrtc.signalingserver.domain.Member;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@Scope("prototype")
public class MeshConversation extends Conversation {
    @Autowired
    private ExchangeSignalsBetweenMembers exchange;

    @Autowired
    @Qualifier(Names.EVENT_BUS)
    private NextRTCEventBus eventBus;

    @Autowired
    private SendingTools tools;

    private Set<Member> members = Sets.newConcurrentHashSet();

    public MeshConversation(String id) {
        super(id);
    }

    @Override
    public void join(Member sender) {
        assignSenderToConversation(sender);

        informSenderThatHasBeenJoined(sender);

        informRestAndBeginSignalExchange(sender);

        members.add(sender);
    }

    private void assignSenderToConversation(Member sender) {
        sender.assign(this);
    }

    private void informRestAndBeginSignalExchange(Member sender) {
        for (Member to : members) {
            tools.sendJoinedFrom(sender, to);
            exchange.begin(to, sender);
        }
    }

    private void informSenderThatHasBeenJoined(Member sender) {
        if (isWithoutMember()) {
            tools.sendJoinedToFirst(sender, id);
        } else {
            tools.sendJoinedToConversation(sender, id);
        }
    }

    public boolean isWithoutMember() {
        return members.size() == 0;
    }

    public boolean has(Member member) {
        return member != null && members.contains(member);
    }

    @Override
    public synchronized boolean remove(Member leaving) {
        boolean remove = members.remove(leaving);
        if (remove) {
            leaving.unassignConversation(this);
            for (Member member : members) {
                tools.sendLeftMessage(leaving, member);
            }
        }
        return false;
    }

    public void execute(InternalMessage message) {
        exchange.execute(message);
    }
}
