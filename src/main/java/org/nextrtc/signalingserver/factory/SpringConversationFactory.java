package org.nextrtc.signalingserver.factory;

import org.nextrtc.signalingserver.domain.Conversation;
import org.nextrtc.signalingserver.domain.conversation.BroadcastConversation;
import org.nextrtc.signalingserver.domain.conversation.MeshConversation;
import org.nextrtc.signalingserver.domain.conversation.MeshWithMasterConversation;
import org.nextrtc.signalingserver.property.NextRTCProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class SpringConversationFactory extends AbstractConversationFactory {
    private ApplicationContext context;

    @Autowired
    public SpringConversationFactory(ApplicationContext context, NextRTCProperties properties) {
        super(properties);
        this.context = context;
        registerConversationType("MESH", this::createMesh);
        registerConversationType("MESH_WITH_MASTER", this::createMeshWithMaster);
        registerConversationType("BROADCAST", this::createBroadcast);
    }

    private Conversation createMesh(String conversationName) {
        return context.getBean(MeshConversation.class, conversationName);
    }

    private Conversation createMeshWithMaster(String conversationName) {
        return context.getBean(MeshWithMasterConversation.class, conversationName);
    }

    private Conversation createBroadcast(String conversationName) {
        return context.getBean(BroadcastConversation.class, conversationName);
    }

}
