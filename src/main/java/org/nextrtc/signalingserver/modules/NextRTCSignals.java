package org.nextrtc.signalingserver.modules;

import dagger.Module;
import dagger.Provides;
import dagger.multibindings.IntoMap;
import dagger.multibindings.StringKey;
import org.nextrtc.signalingserver.api.NextRTCEventBus;
import org.nextrtc.signalingserver.cases.*;
import org.nextrtc.signalingserver.domain.MessageSender;
import org.nextrtc.signalingserver.domain.Signals;
import org.nextrtc.signalingserver.factory.ConversationFactory;
import org.nextrtc.signalingserver.property.NextRTCProperties;
import org.nextrtc.signalingserver.repository.ConversationRepository;

import javax.inject.Singleton;

@Module
public abstract class NextRTCSignals {


    @Provides
    @Singleton
    @IntoMap
    @StringKey(Signals.ANSWER_RESPONSE_HANDLER)
    static SignalHandler AnswerResponseHandler() {
        return new AnswerResponseHandler();
    }

    @Provides
    @Singleton
    @IntoMap
    @StringKey(Signals.CANDIDATE_HANDLER)
    static SignalHandler CandidateHandler() {
        return new CandidateHandler();
    }

    @Provides
    @Singleton
    @IntoMap
    @StringKey(Signals.CREATE_HANDLER)
    static SignalHandler CreateConversationEntry(CreateConversation conversation) {
        return conversation;
    }

    @Provides
    @Singleton
    static CreateConversation CreateConversation(NextRTCEventBus eventBus,
                                                 ConversationRepository conversations,
                                                 ConversationFactory factory) {
        return new CreateConversation(eventBus, conversations, factory);
    }

    @Provides
    @Singleton
    @IntoMap
    @StringKey(Signals.JOIN_HANDLER)
    static SignalHandler JoinConversation(ConversationRepository conversations,
                                          CreateConversation create,
                                          NextRTCProperties properties) {
        return new JoinConversation(conversations, create, properties);
    }

    @Provides
    @Singleton
    @IntoMap
    @StringKey(Signals.LEFT_HANDLER)
    static SignalHandler LeftConversation(NextRTCEventBus eventBus,
                                          ConversationRepository conversations) {
        return new LeftConversation(eventBus, conversations);
    }

    @Provides
    @Singleton
    @IntoMap
    @StringKey(Signals.OFFER_RESPONSE_HANDLER)
    static SignalHandler OfferResponseHandler() {
        return new OfferResponseHandler();
    }

    @Provides
    @Singleton
    @IntoMap
    @StringKey(Signals.TEXT_HANDLER)
    static SignalHandler TextMessage(NextRTCEventBus eventBus,
                                     MessageSender sender) {
        return new TextMessage(eventBus, sender);
    }


}
