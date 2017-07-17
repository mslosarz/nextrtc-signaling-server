package org.nextrtc.signalingserver.factory;

import org.nextrtc.signalingserver.domain.Conversation;

import java.util.UUID;

import static org.apache.commons.lang3.StringUtils.defaultString;
import static org.apache.commons.lang3.StringUtils.isBlank;

public abstract class AbstractConversationFactory implements ConversationFactory {

    @Override
    public final Conversation create(String id, String optionalType) {
        String conversationName = getConversationName(id);
        switch (defaultString(optionalType)) {
            case "BROADCAST":
                return createBroadcast(conversationName);
            default:
                return createMesh(conversationName);
        }
    }

    protected abstract Conversation createMesh(String conversationName);

    protected abstract Conversation createBroadcast(String conversationName);

    private String getConversationName(String name) {
        return isBlank(name) ? UUID.randomUUID().toString() : name;
    }
}
