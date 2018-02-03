package org.nextrtc.signalingserver.modules;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;
import org.nextrtc.signalingserver.api.NextRTCEventBus;
import org.nextrtc.signalingserver.cases.ExchangeSignalsBetweenMembers;
import org.nextrtc.signalingserver.cases.LeftConversation;
import org.nextrtc.signalingserver.domain.MessageSender;
import org.nextrtc.signalingserver.factory.*;
import org.nextrtc.signalingserver.property.NextRTCProperties;

import javax.inject.Singleton;

@Module
public abstract class NextRTCFactories {

    @Provides
    @Singleton
    static ManualConversationFactory ManualConversationFactory(LeftConversation left,
                                                               MessageSender sender,
                                                               ExchangeSignalsBetweenMembers exchange,
                                                               NextRTCProperties properties) {
        return new ManualConversationFactory(left, exchange, sender, properties);
    }

    @Provides
    @Singleton
    static ManualMemberFactory ManualMemberFactory(NextRTCEventBus eventBus) {
        return new ManualMemberFactory(eventBus);
    }

    @Provides
    @Singleton
    static ManualConnectionContextFactory ManualConnectionContextFactory(
            NextRTCProperties properties,
            NextRTCEventBus eventBus,
            MessageSender sender) {
        return new ManualConnectionContextFactory(properties, eventBus, sender);
    }

    @Binds
    abstract ConversationFactory ConversationFactory(ManualConversationFactory manualConversationFactory);

    @Binds
    abstract MemberFactory MemberFactory(ManualMemberFactory memberFactory);

    @Binds
    abstract ConnectionContextFactory ConnectionContextFactory(ManualConnectionContextFactory manualConnectionContextFactory);


}
