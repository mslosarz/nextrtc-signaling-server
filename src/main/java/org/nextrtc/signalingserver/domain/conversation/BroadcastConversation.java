package org.nextrtc.signalingserver.domain.conversation;

import org.nextrtc.signalingserver.domain.Conversation;
import org.nextrtc.signalingserver.domain.Member;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
public class BroadcastConversation extends Conversation {
    public BroadcastConversation(String id) {
        super(id);
    }

    @Override
    public void join(Member sender) {

    }

    @Override
    public boolean remove(Member leaving) {
        return false;
    }

    @Override
    public boolean isWithoutMember() {
        return false;
    }

    @Override
    public boolean has(Member from) {
        return false;
    }
}
