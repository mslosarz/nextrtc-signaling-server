package org.nextrtc.signalingserver.repository;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.nextrtc.signalingserver.BaseTest;
import org.nextrtc.signalingserver.domain.Conversation;
import org.nextrtc.signalingserver.factory.ConversationFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;
import static org.nextrtc.signalingserver.exception.Exceptions.CONVERSATION_NAME_OCCUPIED;

public class ConversationsTest extends BaseTest {

    @Autowired
    private Conversations conversations;

    @Autowired
    private ConversationFactory conversationFactory;

    @Rule
    public ExpectedException expect = ExpectedException.none();

    @Test
    public void shouldFindExistingConversation() throws Exception {
        // given
        Conversation conversation = conversations.save(conversationFactory.create("new", null));

        // when
        Optional<Conversation> found = conversations.findBy("new");

        // then
        assertTrue(found.isPresent());
        Conversation actual = found.get();
        assertEquals(conversation, actual);
        assertEquals(conversation.getId(), actual.getId());
    }

    @Test
    public void shouldThrowExceptionWhenConversationNameIsOccupied() throws Exception {
        // given
        Conversation conversation = conversationFactory.create("aaaa", null);
        conversations.save(conversation);

        // then
        expect.expectMessage(containsString(CONVERSATION_NAME_OCCUPIED.name()));

        // when
        conversations.save(conversation);
    }

    @Test
    public void shouldRemoveConversation() {
        // given
        Conversation saved = conversations.save(conversationFactory.create("new", null));

        // when
        Conversation removed = conversations.remove("new");

        // then
        assertThat(removed, is(saved));
        assertFalse(conversations.findBy("new").isPresent());

    }

    @Test
    public void shouldNotFindConversationWhenMemberIsDifferent() {
        // given
        Conversation saved = conversations.save(conversationFactory.create("new", null));
        saved.join(mockMember("BBBB"));
        // when
        Optional<Conversation> member = conversations.findBy(mockMember("AAAA"));

        // then
        assertFalse(member.isPresent());
    }

    @Test
    public void shouldFindConversationWhenMemberIsInConversation() {
        // given
        Conversation saved = conversations.save(conversationFactory.create("new", null));
        saved.join(mockMember("BBBB"));
        saved.join(mockMember("AAAA"));
        // when
        Optional<Conversation> member = conversations.findBy(mockMember("AAAA"));

        // then
        assertTrue(member.isPresent());
    }

}
