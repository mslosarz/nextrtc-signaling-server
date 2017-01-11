package org.nextrtc.signalingserver.cases;

import org.nextrtc.signalingserver.domain.Conversation;
import org.nextrtc.signalingserver.domain.InternalMessage;
import org.nextrtc.signalingserver.domain.Signals;
import org.nextrtc.signalingserver.repository.Conversations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component(Signals.JOIN_HANDLER)
public class JoinConversation implements SignalHandler {

    @Autowired
    private Conversations conversations;

    @Autowired
    @Qualifier(Signals.CREATE_HANDLER)
    private CreateConversation createConversation;

    public void execute(InternalMessage context) {
        Optional<Conversation> conversation = findConversationToJoin(context);
        if (conversation.isPresent()) {
            conversation.get().join(context.getFrom());
        } else {
            createConversation.execute(context);
        }
    }

    private Optional<Conversation> findConversationToJoin(InternalMessage message) {
        return conversations.findBy(message.getContent());
    }

}
