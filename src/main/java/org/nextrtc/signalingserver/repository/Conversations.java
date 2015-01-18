package org.nextrtc.signalingserver.repository;

import static com.google.common.base.Optional.fromNullable;
import static org.apache.commons.lang3.StringUtils.isEmpty;
import static org.nextrtc.signalingserver.exception.Exceptions.CONVERSATION_NAME_OCCUPIED;
import static org.nextrtc.signalingserver.exception.Exceptions.INVALID_CONVERSATION_NAME;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;

import org.nextrtc.signalingserver.domain.Conversation;
import org.nextrtc.signalingserver.domain.Member;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Repository;

import com.google.common.base.Optional;
import com.google.common.collect.Maps;

@Repository
public class Conversations {

	@Autowired
	private ApplicationContext context;

	private Map<String, Conversation> conversations = Maps.newConcurrentMap();

	public Optional<Conversation> findBy(String id) {
		if (isEmpty(id)) {
			return Optional.absent();
		}
		return fromNullable(conversations.get(id));
	}

	public void unregister(String id) {
		conversations.remove(id);
	}

	public Conversation create() {
		return create(UUID.randomUUID().toString());
	}

	public Conversation create(String name) {
		if (isEmpty(name)) {
			throw INVALID_CONVERSATION_NAME.exception();
		}
		if (conversations.containsKey(name)) {
			throw CONVERSATION_NAME_OCCUPIED.exception();
		}
		Conversation conversation = context.getBean(Conversation.class, name);
		conversations.put(name, conversation);
		return conversation;
	}

	public Collection<String> getAllKeys() {
		return conversations.keySet();
	}

	public Optional<Conversation> getBy(Member from) {
		for (String conversationIds : conversations.keySet()) {
			Conversation conversation = conversations.get(conversationIds);
			if (conversation.has(from)) {
				return Optional.of(conversation);
			}
		}
		return Optional.absent();
	}
}
