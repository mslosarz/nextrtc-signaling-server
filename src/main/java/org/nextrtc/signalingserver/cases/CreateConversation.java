package org.nextrtc.signalingserver.cases;

import org.nextrtc.signalingserver.domain.Conversation;
import org.nextrtc.signalingserver.domain.InternalMessage;
import org.nextrtc.signalingserver.domain.Signals;
import org.nextrtc.signalingserver.exception.SignalingException;
import org.nextrtc.signalingserver.repository.Conversations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static org.nextrtc.signalingserver.exception.Exceptions.MEMBER_IN_OTHER_CONVERSATION;

@Component(Signals.CREATE_HANDLER)
public class CreateConversation implements SignalHandler {

    @Autowired
    private Conversations conversations;

    public void execute(InternalMessage context) {
        conversations.findBy(context.getFrom())
                .map(Conversation::getId)
                .map(MEMBER_IN_OTHER_CONVERSATION::exception)
                .ifPresent(SignalingException::throwException);


        Conversation conversation = conversations.create(context);

        conversation.join(context.getFrom());
    }

}
