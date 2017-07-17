package org.nextrtc.signalingserver.factory;

import org.nextrtc.signalingserver.domain.Conversation;
import org.nextrtc.signalingserver.domain.conversation.BroadcastConversation;
import org.nextrtc.signalingserver.domain.conversation.MeshConversation;

public class ManualConversationFactory extends AbstractConversationFactory {

    @Override
    protected Conversation createMesh(String conversationName) {
        return new MeshConversation(conversationName);
    }

    @Override
    protected Conversation createBroadcast(String conversationName) {
        return new BroadcastConversation(conversationName);
    }
}
