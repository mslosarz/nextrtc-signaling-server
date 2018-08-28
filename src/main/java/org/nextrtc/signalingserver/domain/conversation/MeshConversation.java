package org.nextrtc.signalingserver.domain.conversation;

import org.nextrtc.signalingserver.cases.ExchangeSignalsBetweenMembers;
import org.nextrtc.signalingserver.cases.LeftConversation;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
public class MeshConversation extends AbstractMeshConversation {
    public MeshConversation(String id) {
        super(id);
    }

    public MeshConversation(String id, LeftConversation left, ExchangeSignalsBetweenMembers exchange) {
        super(id, left, exchange);
    }

    @Override
    public String getTypeName() {
        return "MESH";
    }
}
