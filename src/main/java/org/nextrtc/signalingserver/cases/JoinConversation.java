package org.nextrtc.signalingserver.cases;

import org.nextrtc.signalingserver.domain.Conversation;
import org.nextrtc.signalingserver.domain.InternalMessage;
import org.nextrtc.signalingserver.domain.Signals;
import org.nextrtc.signalingserver.exception.SignalingException;
import org.nextrtc.signalingserver.property.NextRTCProperties;
import org.nextrtc.signalingserver.repository.ConversationRepository;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.Optional;

import static org.nextrtc.signalingserver.exception.Exceptions.CONVERSATION_NOT_FOUND;
import static org.nextrtc.signalingserver.exception.Exceptions.MEMBER_IN_OTHER_CONVERSATION;

@Component(Signals.JOIN_HANDLER)
public class JoinConversation implements SignalHandler {

    private ConversationRepository conversations;
    private CreateConversation createConversation;
    private NextRTCProperties properties;

    @Inject
    public JoinConversation(ConversationRepository conversations,
                            CreateConversation createConversation,
                            NextRTCProperties properties) {
        this.conversations = conversations;
        this.createConversation = createConversation;
        this.properties = properties;
    }

    public synchronized void execute(InternalMessage context) {
        conversations.findBy(context.getFrom())
                .map(Conversation::getId)
                .map(MEMBER_IN_OTHER_CONVERSATION::exception)
                .ifPresent(SignalingException::throwException);

        Optional<Conversation> conversation = findConversationToJoin(context);

        if (conversation.isPresent()) {
            conversation.get().join(context.getFrom());
        } else {
            if (properties.isJoinOnlyToExisting()) {
                throw CONVERSATION_NOT_FOUND.exception();
            } else {
                createConversation.execute(context);
            }
        }
    }

    private Optional<Conversation> findConversationToJoin(InternalMessage message) {
        return conversations.findBy(message.getContent());
    }

}
