package org.nextrtc.signalingserver.cases;

import org.nextrtc.signalingserver.cases.connection.ConnectionContext;
import org.nextrtc.signalingserver.domain.InternalMessage;
import org.nextrtc.signalingserver.domain.Member;
import org.nextrtc.signalingserver.domain.RTCConnections;
import org.nextrtc.signalingserver.factory.ConnectionContextFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

@Component
@Scope("prototype")
public class ExchangeSignalsBetweenMembers {

    private RTCConnections connections;
    private ConnectionContextFactory factory;

    @Inject
    public ExchangeSignalsBetweenMembers(RTCConnections connections, ConnectionContextFactory factory) {
        this.connections = connections;
        this.factory = factory;
    }

    public synchronized void begin(Member from, Member to) {
        connections.put(from, to, factory.create(from, to));
        connections.get(from, to).ifPresent(ConnectionContext::begin);
    }

    public synchronized void execute(InternalMessage message) {
        connections.get(message.getFrom(), message.getTo()).ifPresent(context -> context.process(message));
    }
}
