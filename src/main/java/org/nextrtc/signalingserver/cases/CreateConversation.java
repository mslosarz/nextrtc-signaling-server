package org.nextrtc.signalingserver.cases;

import org.nextrtc.signalingserver.Names;
import org.nextrtc.signalingserver.api.NextRTCEventBus;
import org.nextrtc.signalingserver.domain.Conversation;
import org.nextrtc.signalingserver.domain.InternalMessage;
import org.nextrtc.signalingserver.domain.Signals;
import org.nextrtc.signalingserver.exception.SignalingException;
import org.nextrtc.signalingserver.factory.ConversationFactory;
import org.nextrtc.signalingserver.repository.ConversationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import static org.nextrtc.signalingserver.api.NextRTCEvents.CONVERSATION_CREATED;
import static org.nextrtc.signalingserver.exception.Exceptions.MEMBER_IN_OTHER_CONVERSATION;

@Component(Signals.CREATE_HANDLER)
public class CreateConversation implements SignalHandler {

    @Autowired
    @Qualifier(Names.EVENT_BUS)
    private NextRTCEventBus eventBus;

    @Autowired
    private ConversationRepository conversations;

    @Autowired
    private ConversationFactory factory;

    public void execute(InternalMessage context) {
        conversations.findBy(context.getFrom())
                .map(Conversation::getId)
                .map(MEMBER_IN_OTHER_CONVERSATION::exception)
                .ifPresent(SignalingException::throwException);


        String id = context.getContent();

        Conversation conversation = conversations.save(factory.create(id, context.getCustom().get("type")));
        eventBus.post(CONVERSATION_CREATED.basedOn(context, conversation));

        conversation.join(context.getFrom());
    }

}
