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
			Message msg = (Message) argument;
			if (!"ping".equals(msg.getSignal())) {
				messages.add(msg);
			}
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

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (Message msg : messages) {
			sb.append(msg);
			sb.append(", ");
		}
		return sb.toString();
	}

}