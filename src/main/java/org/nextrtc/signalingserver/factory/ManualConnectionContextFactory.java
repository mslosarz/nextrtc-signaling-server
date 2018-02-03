package org.nextrtc.signalingserver.factory;

import org.nextrtc.signalingserver.api.NextRTCEventBus;
import org.nextrtc.signalingserver.cases.connection.ConnectionContext;
import org.nextrtc.signalingserver.domain.Member;
import org.nextrtc.signalingserver.domain.MessageSender;
import org.nextrtc.signalingserver.property.NextRTCProperties;

import javax.inject.Inject;

public class ManualConnectionContextFactory implements ConnectionContextFactory {

    private NextRTCProperties properties;
    private NextRTCEventBus eventBus;
    private MessageSender sender;

    @Inject
    public ManualConnectionContextFactory(NextRTCProperties properties,
                                          NextRTCEventBus eventBus,
                                          MessageSender sender) {
        this.properties = properties;
        this.eventBus = eventBus;
        this.sender = sender;

    }

    @Override
    public ConnectionContext create(Member from, Member to) {
        ConnectionContext connectionContext = new ConnectionContext(from, to);
        connectionContext.setBus(eventBus);
        connectionContext.setProperties(properties);
        connectionContext.setSender(sender);
        return connectionContext;
    }
}
