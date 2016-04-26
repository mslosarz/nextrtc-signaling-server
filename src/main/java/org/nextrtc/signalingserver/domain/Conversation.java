package org.nextrtc.signalingserver.domain;

import com.google.common.collect.Sets;
import lombok.Getter;
import org.nextrtc.signalingserver.api.NextRTCEventBus;
import org.nextrtc.signalingserver.api.dto.NextRTCConversation;
import org.nextrtc.signalingserver.cases.ExchangeSignalsBetweenMembers;
import org.nextrtc.signalingserver.cases.SendingTools;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Set;

import static org.nextrtc.signalingserver.api.NextRTCEvents.MEMBER_JOINED;
import static org.nextrtc.signalingserver.api.NextRTCEvents.MEMBER_LEFT;

@Getter
@Component
@Scope("prototype")
public class Conversation implements NextRTCConversation{

	@Autowired
	private ExchangeSignalsBetweenMembers exchange;

	@Autowired
	@Qualifier("nextRTCEventBus")
	private NextRTCEventBus eventBus;

	@Autowired
	private SendingTools tools;

	private String id;

	private Set<Member> members = Sets.newConcurrentHashSet();

	@Autowired
	public Conversation(String id) {
		this.id = id;
	}

	public synchronized void join(Member sender, InternalMessage context) {
		informSenderThatHasBeenJoined(sender);

		informRestAndBeginSignalExchange(sender);

		members.add(sender);
		joinEvent(context);
	}

	private void informRestAndBeginSignalExchange(Member sender) {
		for (Member member : members) {
			tools.sendMessageToOthers(sender, member);
			exchange.begin(member, sender);
		}
    }

    private void informSenderThatHasBeenJoined(Member sender) {
        if (isWithoutMember()) {
			tools.sendMessageToFirstJoined(sender, id);
		} else {
			tools.sendMessageToJoining(sender, id);
		}
    }

	public boolean isWithoutMember() {
		return members.size() == 0;
	}

	public boolean has(Member member) {
		return member != null && members.contains(member);
	}

	public synchronized void remove(Member leaving, InternalMessage context) {
		members.remove(leaving);
		leftEvent(context);
		for (Member member : members) {
			tools.sendLeftMessage(leaving, member);
		}
	}

	public void execute(InternalMessage message) {
		exchange.execute(message);
	}

	private void joinEvent(InternalMessage context) {
		eventBus.post(MEMBER_JOINED.basedOn(context, this));
	}

	private void leftEvent(InternalMessage context) {
		eventBus.post(MEMBER_LEFT.basedOn(context, this));
	}


}
