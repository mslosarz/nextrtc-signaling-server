package org.nextrtc.signalingserver;

import com.google.common.collect.Lists;
import org.mockito.ArgumentMatcher;
import org.nextrtc.signalingserver.domain.Message;

import java.util.Arrays;
import java.util.List;

public class MessageMatcher extends ArgumentMatcher<Message> {

    private final List<String> filter;
    private List<Message> messages = Lists.newLinkedList();

    public MessageMatcher() {
        this.filter = Arrays.asList("ping");
    }

    public MessageMatcher(String... filter) {
        this.filter = Arrays.asList(filter);
    }

    @Override
    public boolean matches(Object argument) {
        if (argument instanceof Message) {
            Message msg = (Message) argument;
            if (!filter.contains(msg.getSignal())) {
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

    public List<Message> getMessages() {
        return this.messages;
    }
}