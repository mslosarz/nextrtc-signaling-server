package org.nextrtc.signalingserver.factory;

import org.nextrtc.signalingserver.domain.Conversation;

import java.util.Optional;

public interface ConversationFactory {
    Conversation create(String conversationName, Optional<String> type);
}
