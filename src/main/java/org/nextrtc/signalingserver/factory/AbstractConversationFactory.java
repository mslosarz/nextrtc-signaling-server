package org.nextrtc.signalingserver.factory;

import org.nextrtc.signalingserver.domain.Conversation;
import org.nextrtc.signalingserver.property.NextRTCProperties;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

import static org.apache.commons.lang3.StringUtils.defaultString;
import static org.apache.commons.lang3.StringUtils.isBlank;

public abstract class AbstractConversationFactory implements ConversationFactory {
    private final Map<String, Function<String, Conversation>> supportedTypes = new ConcurrentHashMap<>();
    private final NextRTCProperties properties;

    AbstractConversationFactory(NextRTCProperties properties){
        this.properties = properties;
    }

    @Override
    public final Conversation create(String id, String optionalType) {
        String conversationName = getConversationName(id);
        if(supportedTypes.containsKey(defaultString(optionalType))){
            return supportedTypes.get(optionalType).apply(conversationName);
        }
        return supportedTypes.get(properties.getDefaultConversationType()).apply(conversationName);
    }

    public Function<String, Conversation> registerConversationType(String type, Function<String, Conversation> creator){
        return supportedTypes.put(type, creator);
    }

    private String getConversationName(String name) {
        return isBlank(name) ? UUID.randomUUID().toString() : name;
    }
}
