package org.nextrtc.signalingserver.factory;

import org.nextrtc.signalingserver.cases.ExchangeSignalsBetweenMembers;
import org.nextrtc.signalingserver.cases.LeftConversation;
import org.nextrtc.signalingserver.domain.Conversation;
import org.nextrtc.signalingserver.domain.MessageSender;
import org.nextrtc.signalingserver.domain.conversation.BroadcastConversation;
import org.nextrtc.signalingserver.domain.conversation.MeshConversation;
import org.nextrtc.signalingserver.domain.conversation.MeshWithMasterConversation;
import org.nextrtc.signalingserver.property.NextRTCProperties;

import javax.inject.Inject;

public class ManualConversationFactory extends AbstractConversationFactory {

    private MessageSender sender;
    private LeftConversation leftConversation;
    private ExchangeSignalsBetweenMembers exchange;

    @Inject
    public ManualConversationFactory(LeftConversation leftConversation,
                                     ExchangeSignalsBetweenMembers exchange,
                                     MessageSender sender,
                                     NextRTCProperties properties) {
        super(properties);
        this.leftConversation = leftConversation;
        this.exchange = exchange;
        this.sender = sender;
        registerConversationType("MESH", this::createMesh);
        registerConversationType("BROADCAST", this::createBroadcast);
        registerConversationType("MESH_WITH_MASTER", this::createMeshWithMaster);
    }

    private Conversation createMesh(String conversationName) {
        return new MeshConversation(conversationName, leftConversation, sender, exchange);
    }

    private Conversation createMeshWithMaster(String conversationName) {
        return new MeshWithMasterConversation(conversationName, leftConversation, sender, exchange);
    }

    private Conversation createBroadcast(String conversationName) {
        return new BroadcastConversation(conversationName, leftConversation, sender, exchange);
    }
}
