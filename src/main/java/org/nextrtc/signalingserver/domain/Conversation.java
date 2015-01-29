package org.nextrtc.signalingserver.domain;

import static com.google.common.collect.FluentIterable.from;

import java.util.Collection;
import java.util.Set;

import lombok.Getter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.google.common.base.Predicate;
import com.google.common.collect.Sets;

@Getter
@Component
@Scope("prototype")
public class Conversation {

	private Set<Member> members = Sets.newConcurrentHashSet();

	private String id;

	@Autowired
	public Conversation(String id) {
		this.id = id;
	}

	public void joinOwner(Member owner) {
		members.add(owner);
	}

	public void joinMember(Member member) {
		members.add(member);

	}

	public Collection<Member> getMembersWithout(Member sender) {
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

}
