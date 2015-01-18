package org.nextrtc.signalingserver;

import java.util.List;

import lombok.Getter;

import org.mockito.ArgumentMatcher;
import org.nextrtc.signalingserver.domain.Message;

import com.google.common.collect.Lists;

@Getter
public class MessageMatcher extends ArgumentMatcher<Message> {

	private List<Message> messages = Lists.newLinkedList();

	@Override
	public boolean matches(Object argument) {
		if (argument instanceof Message) {
			messages.add((Message) argument);

			return true;
		}
		return false;
	}

	public Message getMessage() {
		return messages.get(0);
	}

	public Message getMessage(int number) {
		if (messages.size() <= number) {
			return null;
		}
		return messages.get(number);
	}

	public void reset() {
		messages.clear();
	}

}