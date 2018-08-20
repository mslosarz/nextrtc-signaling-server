package org.nextrtc.signalingserver.domain.conversation;

import org.nextrtc.signalingserver.cases.ExchangeSignalsBetweenMembers;
import org.nextrtc.signalingserver.cases.LeftConversation;
import org.nextrtc.signalingserver.domain.Member;
import org.nextrtc.signalingserver.domain.MessageSender;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.HashSet;

@Component
@Scope("prototype")
public class MeshWithMasterConversation extends AbstractMeshConversation {
    private Member owner;

    public MeshWithMasterConversation(String id) {
        super(id);
    }

    public MeshWithMasterConversation(String id, LeftConversation left, MessageSender sender, ExchangeSignalsBetweenMembers exchange) {
        super(id, left, sender, exchange);
    }

    @Override
    public String getTypeName() {
        return "MESH_WITH_MASTER";
    }

    @Override
    public synchronized void join(Member sender) {
        if(isWithoutMember()){
            owner = sender;
        }
        super.join(sender);
    }

    @Override
    public synchronized boolean remove(Member leaving) {
        boolean remove = super.remove(leaving);
        if(remove && owner.equals(leaving)){
            new HashSet<>(members()).forEach(super::remove);
        }
        return remove;
    }

    @Override
    public Member getCreator() {
        return owner;
    }
}
