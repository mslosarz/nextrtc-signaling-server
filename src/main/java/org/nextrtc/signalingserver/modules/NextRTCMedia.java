package org.nextrtc.signalingserver.modules;

import dagger.Module;
import dagger.Provides;
import org.nextrtc.signalingserver.cases.ExchangeSignalsBetweenMembers;
import org.nextrtc.signalingserver.domain.DefaultMessageSender;
import org.nextrtc.signalingserver.domain.RTCConnections;
import org.nextrtc.signalingserver.factory.ConnectionContextFactory;
import org.nextrtc.signalingserver.property.NextRTCProperties;

import javax.inject.Singleton;
import java.util.concurrent.ScheduledExecutorService;

@Module
public abstract class NextRTCMedia {

    @Provides
    static DefaultMessageSender defaultMessageSender() {
        return new DefaultMessageSender();
    }

    @Provides
    @Singleton
    static RTCConnections RTCConnections(ScheduledExecutorService scheduler, NextRTCProperties properties) {
        return new RTCConnections(scheduler, properties);
    }

    @Provides
    static ExchangeSignalsBetweenMembers ExchangeSignalsBetweenMembers(RTCConnections connections, ConnectionContextFactory factory) {
        return new ExchangeSignalsBetweenMembers(connections, factory);
    }

}
