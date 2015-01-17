package org.nextrtc.signalingserver.register;

import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.nextrtc.signalingserver.exception.Exceptions.CONVERSATION_NAME_OCCUPIED;
import static org.nextrtc.signalingserver.exception.Exceptions.INVALID_CONVERSATION_NAME;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.nextrtc.signalingserver.TestConfig;
import org.nextrtc.signalingserver.domain.Conversation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.google.common.base.Optional;

@ContextConfiguration(classes = { TestConfig.class })
@RunWith(SpringJUnit4ClassRunner.class)
public class ConversationsTest {

	@Autowired
	private Conversations conversations;

	@Rule
	public ExpectedException expect = ExpectedException.none();

	@Test
	public void shouldCreateConversation() throws Exception {
		// given

		// when
		Conversation createdConversation = conversations.create();

		// then
		assertNotNull(createdConversation);
		assertNotNull(createdConversation.getId());
	}

	@Test
	public void shouldFindExistingConversation() throws Exception {
		// given
		Conversation conversation = conversations.create("new");

		// when
		Optional<Conversation> found = conversations.findBy("new");

		// then
		assertTrue(found.isPresent());
		Conversation actual = found.get();
		assertEquals(conversation, actual);
		assertEquals(conversation.getId(), actual.getId());
	}

	@Test
	public void shouldThrowExceptionOnEmptyConversationName() throws Exception {
		// given

		// then
		expect.expectMessage(containsString(INVALID_CONVERSATION_NAME.getErrorCode()));

		// when
		conversations.create("");
	}

	@Test
	public void shouldThrowExceptionWhenConversationNameIsOccupied() throws Exception {
		// given
		conversations.create("aaaa");

		// then
		expect.expectMessage(containsString(CONVERSATION_NAME_OCCUPIED.getErrorCode()));

		// when
		conversations.create("aaaa");
	}

}
