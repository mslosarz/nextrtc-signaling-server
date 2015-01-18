package org.nextrtc.signalingserver.domain;

import java.util.Set;

import lombok.Getter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

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

}
