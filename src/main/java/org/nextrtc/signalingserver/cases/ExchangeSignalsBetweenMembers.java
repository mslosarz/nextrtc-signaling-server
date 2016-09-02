package org.nextrtc.signalingserver.cases;

import org.nextrtc.signalingserver.cases.connection.ConnectionContext;
import org.nextrtc.signalingserver.domain.InternalMessage;
import org.nextrtc.signalingserver.domain.Member;
import org.nextrtc.signalingserver.domain.RTCConnections;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
public class ExchangeSignalsBetweenMembers {

    @Autowired
    private RTCConnections connections;

    @Autowired
    private ApplicationContext context;

    public synchronized void begin(Member from, Member to) {
        connections.put(from, to, context.getBean(ConnectionContext.class, from, to));
        connections.get(from, to).begin();
    }

    public synchronized void execute(InternalMessage message) {
        connections.get(message.getFrom(), message.getTo()).process(message);
    }
}
