package org.nextrtc.signalingserver.factory;

import org.nextrtc.signalingserver.api.NextRTCEventBus;
import org.nextrtc.signalingserver.cases.connection.ConnectionContext;
import org.nextrtc.signalingserver.domain.Member;
import org.nextrtc.signalingserver.property.NextRTCProperties;

import javax.inject.Inject;

public class ManualConnectionContextFactory implements ConnectionContextFactory {

    private NextRTCProperties properties;
    private NextRTCEventBus eventBus;

    @Inject
    public ManualConnectionContextFactory(NextRTCProperties properties,
                                          NextRTCEventBus eventBus) {
        this.properties = properties;
        this.eventBus = eventBus;
    }

    @Override
    public ConnectionContext create(Member from, Member to) {
        ConnectionContext connectionContext = new ConnectionContext(from, to);
        connectionContext.setBus(eventBus);
        connectionContext.setProperties(properties);
        return connectionContext;
    }
}
