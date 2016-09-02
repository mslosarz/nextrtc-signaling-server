package org.nextrtc.signalingserver.cases;

import org.nextrtc.signalingserver.domain.Conversation;
import org.nextrtc.signalingserver.domain.InternalMessage;
import org.nextrtc.signalingserver.domain.Member;
import org.nextrtc.signalingserver.domain.Signals;
import org.springframework.stereotype.Component;

import java.util.Optional;

import static org.nextrtc.signalingserver.exception.Exceptions.CONVERSATION_NOT_FOUND;
import static org.nextrtc.signalingserver.exception.Exceptions.INVALID_RECIPIENT;

@Component(Signals.LEFT_HANDLER)
public class LeftConversation implements SignalHandler {

    public void execute(InternalMessage context) {
        final Member leaving = context.getFrom();
        Conversation conversation = checkPrecondition(context, leaving.getConversation());

        conversation.left(leaving);
    }

    protected Conversation checkPrecondition(InternalMessage message, Optional<Conversation> conversation) {
        if (!conversation.isPresent()) {
            throw CONVERSATION_NOT_FOUND.exception();
        }
        if (!conversation.get().has(message.getFrom())) {
            throw INVALID_RECIPIENT.exception();
        }
        return conversation.get();
    }

}
