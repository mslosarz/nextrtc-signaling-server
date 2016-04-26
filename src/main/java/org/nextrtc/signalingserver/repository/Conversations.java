package org.nextrtc.signalingserver.repository;

import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;
import org.nextrtc.signalingserver.api.NextRTCEventBus;
import org.nextrtc.signalingserver.api.NextRTCEvents;
import org.nextrtc.signalingserver.domain.Conversation;
import org.nextrtc.signalingserver.domain.InternalMessage;
import org.nextrtc.signalingserver.domain.Member;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.apache.commons.lang3.StringUtils.isEmpty;
import static org.nextrtc.signalingserver.api.NextRTCEvents.CONVERSATION_CREATED;
import static org.nextrtc.signalingserver.exception.Exceptions.CONVERSATION_NAME_OCCUPIED;
import static org.nextrtc.signalingserver.exception.Exceptions.INVALID_CONVERSATION_NAME;

@Repository
public class Conversations {

	@Autowired
	@Qualifier("nextRTCEventBus")
	private NextRTCEventBus eventBus;

	@Autowired
	private ApplicationContext context;

	private Map<String, Conversation> conversations = Maps.newConcurrentMap();

	public Optional<Conversation> findBy(String id) {
		if (isEmpty(id)) {
			return Optional.empty();
		}
		return Optional.ofNullable(conversations.get(id));
	}

	public void remove(String id, InternalMessage context) {
		eventBus.post(NextRTCEvents.CONVERSATION_DESTROYED.basedOn(context, conversations.remove(id)));
	}

	public Conversation create(InternalMessage message) {
		String conversationName = getConversationName(message.getContent());
		final Conversation conversation = create(conversationName);
		postEvent(message, conversation);
		return conversation;
	}

	private void postEvent(InternalMessage message, Conversation conversation) {
		eventBus.post(CONVERSATION_CREATED.basedOn(message, conversation));
	}

	private Conversation create(String conversationName) {
		Conversation conversation = fetchConversationWithSpringContext(conversationName);
		registerInContainer(conversation);
		return conversation;
	}

	private String getConversationName(String name) {
		final String conversationName = StringUtils.isBlank(name) ? UUID.randomUUID().toString() : name;
		validate(conversationName);
		return conversationName;
	}

	private void registerInContainer(Conversation conversation) {
		conversations.put(conversation.getId(), conversation);
	}

	private Conversation fetchConversationWithSpringContext(String name) {
		return context.getBean(Conversation.class, name);
	}

	private void validate(String name) {
		if (isEmpty(name)) {
			throw INVALID_CONVERSATION_NAME.exception();
		}
		if (conversations.containsKey(name)) {
			throw CONVERSATION_NAME_OCCUPIED.exception();
		}
	}

	public Collection<String> getAllIds() {
		return conversations.keySet();
	}

	public Optional<Conversation> getBy(Member member) {
		for (String conversationIds : conversations.keySet()) {
			Conversation conversation = conversations.get(conversationIds);
			if (conversation.has(member)) {
				return Optional.of(conversation);
			}
		}
		return Optional.empty();
	}
}
