package org.nextrtc.signalingserver.factory;

import org.nextrtc.signalingserver.domain.Conversation;
import org.nextrtc.signalingserver.domain.conversation.BroadcastConversation;
import org.nextrtc.signalingserver.domain.conversation.MeshConversation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

import static org.apache.commons.lang3.StringUtils.isBlank;

@Component
public class SpringConversationFactory implements ConversationFactory {
    @Autowired
    private ApplicationContext context;

    @Override
    public Conversation create(String id, Optional<String> optionalType) {
        String conversationName = getConversationName(id);
        String type = optionalType.orElse("MESH");
        Conversation conversation = null;
        if (type.equalsIgnoreCase("BROADCAST")) {
            conversation = context.getBean(BroadcastConversation.class, conversationName);
        } else if (type.equalsIgnoreCase("MESH")) {
            conversation = context.getBean(MeshConversation.class, conversationName);
        }
        return conversation;
    }

    private String getConversationName(String name) {
        return isBlank(name) ? UUID.randomUUID().toString() : name;
    }

}
