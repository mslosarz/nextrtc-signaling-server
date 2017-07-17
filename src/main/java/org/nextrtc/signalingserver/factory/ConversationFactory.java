package org.nextrtc.signalingserver.factory;

import org.nextrtc.signalingserver.domain.Conversation;

public interface ConversationFactory {
    Conversation create(String conversationName, String type);
}
