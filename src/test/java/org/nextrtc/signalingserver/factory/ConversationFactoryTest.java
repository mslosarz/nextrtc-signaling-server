package org.nextrtc.signalingserver.factory;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.nextrtc.signalingserver.BaseTest;
import org.nextrtc.signalingserver.domain.Conversation;
import org.nextrtc.signalingserver.domain.conversation.BroadcastConversation;
import org.nextrtc.signalingserver.domain.conversation.MeshConversation;
import org.springframework.beans.factory.annotation.Autowired;

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
        Conversation createdConversation = conversationFactory.create(null, null);

        // then
        assertNotNull(createdConversation);
        assertNotNull(createdConversation.getId());
    }

    @Test
    public void shouldCreateConversationWithRandomNameOnEmptyConversationName() throws Exception {
        // given

        // then
        final Conversation conversation = conversationFactory.create("", null);

        // when
        assertNotNull(conversation);
        assertThat(conversation.getId(), not(nullValue()));

    }

    @Test
    public void shouldCreateBroadcastConversationWhenInCustomPayloadTypeIsBroadcast() throws Exception {
        // given

        // when
        Conversation conversation = conversationFactory.create("new conversation", "BROADCAST");

        // then
        assertThat(conversation.getId(), is("new conversation"));
        assertTrue(conversation instanceof BroadcastConversation);
    }

    @Test
    public void shouldCreateMeshConversationWhenInCustomPayloadTypeIsMesh() throws Exception {
        // given

        // when
        Conversation conversation = conversationFactory.create("new conversation", "MESH");

        // then
        assertThat(conversation.getId(), is("new conversation"));
        assertTrue(conversation instanceof MeshConversation);
    }

    @Test
    public void shouldCreateMeshConversationByDefault() throws Exception {
        // given

        // when
        Conversation conversation = conversationFactory.create("new conversation", null);

        // then
        assertNotNull(conversation.getId());
        assertTrue(conversation instanceof MeshConversation);
    }


}
