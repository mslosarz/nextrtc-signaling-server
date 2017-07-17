package org.nextrtc.signalingserver.factory;

import org.nextrtc.signalingserver.domain.Conversation;
import org.nextrtc.signalingserver.domain.conversation.BroadcastConversation;
import org.nextrtc.signalingserver.domain.conversation.MeshConversation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class SpringConversationFactory extends AbstractConversationFactory {
    private ApplicationContext context;

    @Autowired
    public SpringConversationFactory(ApplicationContext context) {
        this.context = context;
    }

    @Override
    protected Conversation createMesh(String conversationName) {
        return context.getBean(MeshConversation.class, conversationName);
    }

    @Override
    protected Conversation createBroadcast(String conversationName) {
        return context.getBean(BroadcastConversation.class, conversationName);
    }

}
