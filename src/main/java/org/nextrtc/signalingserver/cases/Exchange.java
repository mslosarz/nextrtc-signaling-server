package org.nextrtc.signalingserver.cases;

import org.nextrtc.signalingserver.domain.Conversation;
import org.nextrtc.signalingserver.domain.InternalMessage;
import org.nextrtc.signalingserver.domain.Member;

import static org.nextrtc.signalingserver.exception.Exceptions.INVALID_RECIPIENT;

public abstract class Exchange implements SignalHandler {

    @Override
    public final void execute(InternalMessage message) {
        Conversation conversation = checkPrecondition(message.getFrom());
        exchange(message, conversation);
    }

    protected abstract void exchange(InternalMessage message, Conversation conversation);

    private Conversation checkPrecondition(Member from) {
        if (!from.getConversation().isPresent()) {
            throw INVALID_RECIPIENT.exception();
        }
        return from.getConversation().get();
    }
}
