package org.nextrtc.signalingserver.domain;

import static com.google.common.collect.FluentIterable.from;

import java.util.Collection;
import java.util.Set;

import lombok.Getter;

import org.nextrtc.signalingserver.cases.ExchangeSignalsBetweenMembers;
import org.nextrtc.signalingserver.cases.JoinMember;
import org.nextrtc.signalingserver.cases.LeftConversation;
import org.nextrtc.signalingserver.repository.Conversations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.google.common.base.Predicate;
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
			for (Member member : getMembersWithout(sender)) {
				join.sendMessageToOthers(sender, member);
				exchange.begin(sender, member);
			}
		}
		members.add(sender);
	}

	private boolean isConversationWithoutMember() {
		return members.size() == 0;
	}

	private Collection<Member> getMembersWithout(Member sender) {
		return from(members).filter(without(sender)).toSet();
	}

	private Predicate<Member> without(final Member sender) {
		return new Predicate<Member>() {
			@Override
			public boolean apply(Member input) {
				return !sender.equals(input);
			}
		};
	}

	public boolean has(Member member) {
		if (member == null) {
			return false;
		}
		return members.contains(member);
	}

	public synchronized void left(Member member) {
		members.remove(member);
		if (isConversationWithoutMember()) {
			conversations.remove(id);
		}
		left.executeFor(member);
	}

	public void process(InternalMessage buildInternalMessage) {

	}

	public void unbind(Member member) {

	}

}
