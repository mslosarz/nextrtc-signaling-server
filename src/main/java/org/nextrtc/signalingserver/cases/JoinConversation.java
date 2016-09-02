package org.nextrtc.signalingserver.cases;

import org.nextrtc.signalingserver.domain.Conversation;
import org.nextrtc.signalingserver.domain.InternalMessage;
import org.nextrtc.signalingserver.domain.Signals;
import org.nextrtc.signalingserver.repository.Conversations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static org.nextrtc.signalingserver.exception.Exceptions.CONVERSATION_NOT_FOUND;

@Component(Signals.JOIN_HANDLER)
public class JoinConversation {

    @Autowired
    private Conversations conversations;

    public void execute(InternalMessage context) {
        Conversation conversation = findConversationToJoin(context);

        conversation.join(context.getFrom());
    }

    private Conversation findConversationToJoin(InternalMessage message) {
        return conversations.findBy(message.getContent()).orElseThrow(CONVERSATION_NOT_FOUND::exception);
    }

}
