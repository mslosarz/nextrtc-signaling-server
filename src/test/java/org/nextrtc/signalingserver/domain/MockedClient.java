package org.nextrtc.signalingserver.domain;

import com.google.common.collect.Lists;
import org.apache.log4j.Logger;
import org.mockito.ArgumentMatcher;

import javax.websocket.Session;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

public class MockedClient extends ArgumentMatcher<Message> {
    private static final Logger log = Logger.getLogger(Server.class);
    private Server server;
    private Session session;
    private List<Message> messages = Lists.newLinkedList();
    private AtomicInteger candidates = new AtomicInteger();
    private Map<String, Consumer<Message>> behavior = new HashMap<>();

    {
        behavior.put(Signals.CREATED, (message) -> {
        });
        behavior.put(Signals.JOINED, (message) -> {
        });
        behavior.put(Signals.LEFT, (message) -> {
        });
        behavior.put(Signals.TEXT, (message) -> {
            log.info("text message: " + message);
        });
        behavior.put(Signals.END, (message) -> {
        });
        behavior.put(Signals.ERROR, (message) -> {
            log.warn("Received error!" + message);
        });
        behavior.put(Signals.NEW_JOINED, (message) -> {
        });
        behavior.put(Signals.OFFER_REQUEST, (message) -> {
            server.handle(Message.create()
                    .to(message.getFrom())
                    .signal(Signals.OFFER_RESPONSE)
                    .content("OFFER: " + session.getId())
                    .build(), session);
        });
        behavior.put(Signals.ANSWER_REQUEST, (message) -> {
            server.handle(Message.create()
                    .to(message.getFrom())
                    .signal(Signals.ANSWER_RESPONSE)
                    .content("ANSWER: " + session.getId())
                    .build(), session);
        });
        behavior.put(Signals.FINALIZE, (message) -> {
            server.handle(Message.create()
                    .to(message.getFrom())
                    .signal(Signals.CANDIDATE)
                    .content("CANDIDATE: " + session.getId() + " -> " + message.getFrom())
                    .build(), session);
        });
        behavior.put(Signals.CANDIDATE, (message) -> {
            if (candidates.getAndIncrement() < 1) {
                server.handle(Message.create()
                        .to(message.getFrom())
                        .signal(Signals.CANDIDATE)
                        .content("CANDIDATE: " + session.getId() + " -> " + message.getFrom())
                        .build(), session);
            }
        });
    }

    public MockedClient(Server server, Session session) {
        this.server = server;
        this.session = session;
    }

    @Override
    public boolean matches(Object argument) {
        if (argument instanceof Message) {
            Message msg = (Message) argument;
            if (!"ping".equals(msg.getSignal())) {
                messages.add(msg);
                behavior.get(msg.getSignal()).accept(msg);
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