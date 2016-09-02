package org.nextrtc.signalingserver.domain;

import lombok.Getter;
import org.nextrtc.signalingserver.api.dto.NextRTCConversation;
import org.nextrtc.signalingserver.repository.Conversations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Getter
@Component
@Scope("prototype")
public abstract class Conversation implements NextRTCConversation {

    protected final String id;

    @Autowired
    private Conversations conversations;

    public Conversation(String id) {
        this.id = id;
    }

    public abstract void join(Member sender);

    public void left(Member sender) {
        if (remove(sender)) {
            if (isWithoutMember()) {
                unregisterConversation(sender, this);
            }
        }
    }

    protected abstract boolean remove(Member leaving);

    public abstract boolean isWithoutMember();

    public abstract boolean has(Member from);

    private void unregisterConversation(Member sender, Conversation conversation) {
        conversations.remove(conversation.getId(), sender);
    }
}
