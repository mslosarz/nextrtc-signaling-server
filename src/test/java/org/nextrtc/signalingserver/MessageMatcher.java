package org.nextrtc.signalingserver;

import lombok.extern.slf4j.Slf4j;
import org.mockito.ArgumentMatcher;
import org.nextrtc.signalingserver.domain.Message;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

@Slf4j
public class MessageMatcher extends ArgumentMatcher<Message> {

    private final List<String> filter;
    private List<Message> messages = Collections.synchronizedList(new ArrayList<>());

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

    public CheckMessage has(Predicate<Message> condition) {
        return new CheckMessage().has(condition);
    }

    public class CheckMessage {
        private List<Predicate<Message>> predicates = new ArrayList<>();

        public CheckMessage has(Predicate<Message> condition) {
            predicates.add(condition);
            return this;
        }

        public boolean check() {
            return messages.stream()
                    .filter(this::matchAllPredicates)
                    .count() > 0;
        }

        private boolean matchAllPredicates(final Message m) {
            return predicates.stream().filter(p -> p.test(m)).count() == predicates.size();
        }
    }
}