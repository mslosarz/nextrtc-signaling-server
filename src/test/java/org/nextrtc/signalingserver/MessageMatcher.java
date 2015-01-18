package org.nextrtc.signalingserver;

import lombok.Getter;

import org.mockito.ArgumentMatcher;
import org.nextrtc.signalingserver.domain.Message;

@Getter
public class MessageMatcher extends ArgumentMatcher<Message> {

	private Message message;

	@Override
	public boolean matches(Object argument) {
		if (argument instanceof Message) {
			message = (Message) argument;
			return true;
		}
		return false;
	}

}