package org.nextrtc.signalingserver.cases;

import org.nextrtc.signalingserver.domain.Conversation;
import org.nextrtc.signalingserver.domain.InternalMessage;
import org.nextrtc.signalingserver.domain.Member;
import org.nextrtc.signalingserver.domain.Signals;
import org.springframework.stereotype.Component;

import java.util.Optional;

import static org.nextrtc.signalingserver.exception.Exceptions.CONVERSATION_NOT_FOUND;

@Component(Signals.LEFT_HANDLER)
public class LeftConversation implements SignalHandler {

    public void execute(InternalMessage context) {
        final Member leaving = context.getFrom();
        Conversation conversation = checkPrecondition(leaving.getConversation());

        conversation.left(leaving);
    }

    private Conversation checkPrecondition(Optional<Conversation> conversation) {
        if (!conversation.isPresent()) {
            throw CONVERSATION_NOT_FOUND.exception();
        }
        return conversation.get();
    }

}
