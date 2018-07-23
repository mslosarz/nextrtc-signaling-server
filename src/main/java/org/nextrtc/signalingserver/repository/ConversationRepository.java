package org.nextrtc.signalingserver.repository;

import org.nextrtc.signalingserver.domain.Conversation;
import org.nextrtc.signalingserver.domain.Member;

import java.io.Closeable;
import java.util.Collection;
import java.util.Optional;


public interface ConversationRepository extends Closeable {
    Optional<Conversation> findBy(String id);

    Optional<Conversation> findBy(Member from);

    Conversation remove(String id);

    Conversation save(Conversation conversation);

    Collection<String> getAllIds();
}
