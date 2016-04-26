package org.nextrtc.signalingserver.repository;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.nextrtc.signalingserver.BaseTest;
import org.nextrtc.signalingserver.domain.Conversation;
import org.nextrtc.signalingserver.domain.InternalMessage;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static org.nextrtc.signalingserver.exception.Exceptions.CONVERSATION_NAME_OCCUPIED;

public class ConversationsTest extends BaseTest {

	@Autowired
	private Conversations conversations;

	@Rule
	public ExpectedException expect = ExpectedException.none();

	@Test
	public void shouldCreateConversation() throws Exception {
		// given

		// when
		Conversation createdConversation = conversations.create(InternalMessage.create()
				.content(null)
				.build());

		// then
		assertNotNull(createdConversation);
		assertNotNull(createdConversation.getId());
	}

	@Test
	public void shouldFindExistingConversation() throws Exception {
		// given
		Conversation conversation = conversations.create(InternalMessage.create()
				.content("new")
				.build());

		// when
		Optional<Conversation> found = conversations.findBy("new");

		// then
		assertTrue(found.isPresent());
		Conversation actual = found.get();
		assertEquals(conversation, actual);
		assertEquals(conversation.getId(), actual.getId());
	}

	@Test
	public void shouldCreateConversationWithRandomNameOnEmptyConversationName() throws Exception {
		// given

		// then
		final Conversation conversation = conversations.create(InternalMessage.create()
				.content("")
				.build());

		// when
		assertNotNull(conversation);
		assertThat(conversation.getId(), not(nullValue()));

	}

	@Test
	public void shouldThrowExceptionWhenConversationNameIsOccupied() throws Exception {
		// given
		conversations.create(InternalMessage.create()
				.content("aaaa")
				.build());

		// then
		expect.expectMessage(containsString(CONVERSATION_NAME_OCCUPIED.getErrorCode()));

		// when
		conversations.create(InternalMessage.create()
				.content("aaaa")
				.build());
	}

}
