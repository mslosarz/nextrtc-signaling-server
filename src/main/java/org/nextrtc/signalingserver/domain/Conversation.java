package org.nextrtc.signalingserver.domain;

import java.util.Set;

import lombok.Getter;

import org.nextrtc.signalingserver.cases.ExchangeSignalsBetweenMembers;
import org.nextrtc.signalingserver.cases.JoinMember;
import org.nextrtc.signalingserver.cases.LeftConversation;
import org.nextrtc.signalingserver.repository.Conversations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.google.common.collect.Sets;

@Getter
@Component
@Scope("prototype")
public class Conversation {

	@Autowired
	private Conversations conversations;

	@Autowired
	private JoinMember join;

	@Autowired
	private ExchangeSignalsBetweenMembers exchange;

	@Autowired
	private LeftConversation left;

	private Set<Member> members = Sets.newConcurrentHashSet();

	private String id;

	@Autowired
	public Conversation(String id) {
		this.id = id;
	}

	public synchronized void joinMember(Member sender) {
		if (isConversationWithoutMember()) {
			join.sendMessageToFirstJoined(sender, id);
		} else {
			join.sendMessageToJoining(sender, id);
			for (Member member : members) {
				join.sendMessageToOthers(sender, member);
				exchange.begin(sender, member);
			}
		}
		members.add(sender);
	}

	private boolean isConversationWithoutMember() {
		return members.size() == 0;
	}

	public boolean has(Member member) {
		if (member == null) {
			return false;
		}
		return members.contains(member);
	}

	public synchronized void left(Member leaving) {
		members.remove(leaving);
		if (isConversationWithoutMember()) {
			conversations.remove(id);
		}
		for (Member member : members) {
			left.executeFor(leaving, member);
		}
	}

	public synchronized void process(InternalMessage message) {
		exchange.processCommunication(message);
	}
}
