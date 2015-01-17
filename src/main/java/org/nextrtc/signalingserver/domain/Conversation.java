package org.nextrtc.signalingserver.domain;

import lombok.Getter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Getter
@Component
@Scope("prototype")
public class Conversation {

	private String id;

	@Autowired
	public Conversation(String id) {
		this.id = id;
	}

}
