package org.nextrtc.signalingserver.domain;

import com.google.common.collect.Sets;
import lombok.Getter;
import org.nextrtc.signalingserver.api.dto.NextRTCConversation;
import org.nextrtc.signalingserver.cases.ExchangeSignalsBetweenMembers;
import org.nextrtc.signalingserver.cases.JoinMember;
import org.nextrtc.signalingserver.cases.LeftMember;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Set;

@Getter
@Component
@Scope("prototype")
public class Conversation implements NextRTCConversation{

	@Autowired
	private ExchangeSignalsBetweenMembers exchange;

	@Autowired
	private JoinMember join;

	@Autowired
	private LeftMember left;

	private String id;

	private Set<Member> members = Sets.newConcurrentHashSet();

	@Autowired
	public Conversation(String id) {
		this.id = id;
	}

	public synchronized void join(Member sender) {
        informSenderThatHasBeenJoined(sender);

        informRestAndBeginSignalExchange(sender);

        members.add(sender);
    }

    private void informRestAndBeginSignalExchange(Member sender) {
        for (Member member : members) {
			join.sendMessageToOthers(sender, member);
			exchange.begin(member, sender);
		}
    }

    private void informSenderThatHasBeenJoined(Member sender) {
        if (isWithoutMember()) {
            join.sendMessageToFirstJoined(sender, id);
        } else {
            join.sendMessageToJoining(sender, id);
        }
    }

	public boolean isWithoutMember() {
		return members.size() == 0;
	}

	public boolean has(Member member) {
		return member != null && members.contains(member);
	}

	public synchronized void left(Member leaving) {
		members.remove(leaving);
		for (Member member : members) {
			left.executeFor(leaving, member);
		}
	}

	public void execute(InternalMessage message) {
		exchange.execute(message);
	}

}
