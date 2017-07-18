package org.nextrtc.signalingserver.cases;

import org.nextrtc.signalingserver.api.NextRTCEventBus;
import org.nextrtc.signalingserver.domain.Conversation;
import org.nextrtc.signalingserver.domain.InternalMessage;
import org.nextrtc.signalingserver.domain.Member;
import org.nextrtc.signalingserver.domain.Signals;
import org.nextrtc.signalingserver.repository.ConversationRepository;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.Optional;

import static org.nextrtc.signalingserver.api.NextRTCEvents.CONVERSATION_DESTROYED;
import static org.nextrtc.signalingserver.domain.EventContext.builder;
import static org.nextrtc.signalingserver.exception.Exceptions.CONVERSATION_NOT_FOUND;

@Component(Signals.LEFT_HANDLER)
public class LeftConversation implements SignalHandler {

    private NextRTCEventBus eventBus;
    private ConversationRepository conversations;

    @Inject
    public LeftConversation(NextRTCEventBus eventBus, ConversationRepository conversations) {
        this.eventBus = eventBus;
        this.conversations = conversations;
    }

    public void execute(InternalMessage context) {
        final Member leaving = context.getFrom();
        Conversation conversation = checkPrecondition(leaving.getConversation());

        conversation.left(leaving);
    }

    public void destroy(Conversation toRemove, Member last) {
        eventBus.post(CONVERSATION_DESTROYED.basedOn(
                builder()
                        .conversation(conversations.remove(toRemove.getId()))
                        .from(last)));
    }

    private Conversation checkPrecondition(Optional<Conversation> conversation) {
        if (!conversation.isPresent()) {
            throw CONVERSATION_NOT_FOUND.exception();
        }
        return conversation.get();
    }

}
