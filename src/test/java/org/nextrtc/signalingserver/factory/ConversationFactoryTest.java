package org.nextrtc.signalingserver.factory;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.nextrtc.signalingserver.BaseTest;
import org.nextrtc.signalingserver.domain.Conversation;
import org.nextrtc.signalingserver.domain.conversation.BroadcastConversation;
import org.nextrtc.signalingserver.domain.conversation.MeshConversation;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

import static java.util.Optional.of;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

public class ConversationFactoryTest extends BaseTest {

    @Autowired
    private ConversationFactory conversationFactory;

    @Rule
    public ExpectedException expect = ExpectedException.none();

    @Test
    public void shouldCreateConversation() throws Exception {
        // given

        // when
        Conversation createdConversation = conversationFactory.create(null, Optional.empty());

        // then
        assertNotNull(createdConversation);
        assertNotNull(createdConversation.getId());
    }

    @Test
    public void shouldCreateConversationWithRandomNameOnEmptyConversationName() throws Exception {
        // given

        // then
        final Conversation conversation = conversationFactory.create("", Optional.empty());

        // when
        assertNotNull(conversation);
        assertThat(conversation.getId(), not(nullValue()));

    }

    @Test
    public void shouldCreateBroadcastConversationWhenInCustomPayloadTypeIsBroadcast() throws Exception {
        // given

        // when
        Conversation conversation = conversationFactory.create("new conversation", of("BROADCAST"));

        // then
        assertThat(conversation.getId(), is("new conversation"));
        assertTrue(conversation instanceof BroadcastConversation);
    }

    @Test
    public void shouldCreateMeshConversationWhenInCustomPayloadTypeIsMesh() throws Exception {
        // given

        // when
        Conversation conversation = conversationFactory.create("new conversation", of("MESH"));

        // then
        assertThat(conversation.getId(), is("new conversation"));
        assertTrue(conversation instanceof MeshConversation);
    }

    @Test
    public void shouldCreateMeshConversationByDefault() throws Exception {
        // given

        // when
        Conversation conversation = conversationFactory.create("new conversation", Optional.empty());

        // then
        assertNotNull(conversation.getId());
        assertTrue(conversation instanceof MeshConversation);
    }


}
