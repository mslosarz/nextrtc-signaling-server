package org.nextrtc.signalingserver.modules;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;
import org.nextrtc.signalingserver.api.NextRTCEventBus;
import org.nextrtc.signalingserver.cases.ExchangeSignalsBetweenMembers;
import org.nextrtc.signalingserver.cases.LeftConversation;
import org.nextrtc.signalingserver.factory.*;
import org.nextrtc.signalingserver.property.NextRTCProperties;

import javax.inject.Singleton;
import java.util.concurrent.ScheduledExecutorService;

@Module
public abstract class NextRTCFactories {

    @Provides
    @Singleton
    static ManualConversationFactory ManualConversationFactory(LeftConversation left,
                                                               ExchangeSignalsBetweenMembers exchange,
                                                               NextRTCProperties properties) {
        return new ManualConversationFactory(left, exchange, properties);
    }

    @Provides
    @Singleton
    static ManualMemberFactory ManualMemberFactory(NextRTCProperties properties,
                                                   ScheduledExecutorService scheduler,
                                                   NextRTCEventBus eventBus) {
        return new ManualMemberFactory(properties, scheduler, eventBus);
    }

    @Provides
    @Singleton
    static ManualConnectionContextFactory ManualConnectionContextFactory(
            NextRTCProperties properties,
            NextRTCEventBus eventBus) {
        return new ManualConnectionContextFactory(properties, eventBus);
    }

    @Binds
    abstract ConversationFactory ConversationFactory(ManualConversationFactory manualConversationFactory);

    @Binds
    abstract MemberFactory MemberFactory(ManualMemberFactory memberFactory);

    @Binds
    abstract ConnectionContextFactory ConnectionContextFactory(ManualConnectionContextFactory manualConnectionContextFactory);


}
