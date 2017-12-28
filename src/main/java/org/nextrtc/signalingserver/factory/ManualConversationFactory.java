package org.nextrtc.signalingserver.factory;

import org.nextrtc.signalingserver.cases.ExchangeSignalsBetweenMembers;
import org.nextrtc.signalingserver.cases.LeftConversation;
import org.nextrtc.signalingserver.domain.Conversation;
import org.nextrtc.signalingserver.domain.MessageSender;
import org.nextrtc.signalingserver.domain.conversation.BroadcastConversation;
import org.nextrtc.signalingserver.domain.conversation.MeshConversation;

import javax.inject.Inject;

public class ManualConversationFactory extends AbstractConversationFactory {

    private MessageSender sender;
    private LeftConversation leftConversation;
    private ExchangeSignalsBetweenMembers exchange;

    @Inject
    public ManualConversationFactory(LeftConversation leftConversation,
                                     ExchangeSignalsBetweenMembers exchange,
                                     MessageSender sender) {
        this.leftConversation = leftConversation;
        this.exchange = exchange;
        this.sender = sender;
    }

    @Override
    protected Conversation createMesh(String conversationName) {
        return new MeshConversation(conversationName, leftConversation, sender, exchange);
    }

    @Override
    protected Conversation createBroadcast(String conversationName) {
        return new BroadcastConversation(conversationName, leftConversation, sender, exchange);
    }
}
