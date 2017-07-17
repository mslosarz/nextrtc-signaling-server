package org.nextrtc.signalingserver.repository;

import org.nextrtc.signalingserver.domain.Conversation;
import org.nextrtc.signalingserver.domain.Member;

import java.util.Collection;
import java.util.Optional;


public interface ConversationRepository {
    Optional<Conversation> findBy(String id);

    Optional<Conversation> findBy(Member from);

    Conversation remove(String id);

    Conversation save(Conversation conversation);

    Collection<String> getAllIds();
}
