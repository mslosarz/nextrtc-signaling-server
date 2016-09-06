package org.nextrtc.signalingserver.cases;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.nextrtc.signalingserver.BaseTest;
import org.nextrtc.signalingserver.domain.Member;
import org.nextrtc.signalingserver.domain.Signal;
import org.nextrtc.signalingserver.repository.Conversations;
import org.nextrtc.signalingserver.repository.Members;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.assertFalse;
import static org.nextrtc.signalingserver.domain.InternalMessage.create;

public class LeftConversationTest extends BaseTest {

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Autowired
    private Members members;

    @Autowired
    private Conversations conversations;

    @Autowired
    private LeftConversation leftConversation;

    @Test
    public void shouldThrowAnExceptionWhenConversationDoesntExists() throws Exception {
        // given
        Member john = mockMember("Jan");
        members.register(john);

        // then
        exception.expectMessage("CONVERSATION_NOT_FOUND");

        // when
        leftConversation.execute(create()
                .signal(Signal.LEFT)
                .from(john)
                .build());
    }

    @Test
    public void shouldLeaveConversation() throws Exception {
        // given
        Member john = mockMember("Jan");
        members.register(john);
        createConversation("conversationId", john);

        // when
        leftConversation.execute(create()
                .from(john)
                .build());

        // then
        assertFalse(john.getConversation().isPresent());
    }

    @Test
    public void shouldRemoveConversationIfLastMemberLeft() throws Exception {
        // given
        Member john = mockMember("Jan");
        Member stan = mockMember("Stan");
        members.register(john);
        createConversation("conversationId", john);
        joinConversation("conversationId", stan);

        // when
        leftConversation.execute(create()
                .from(john)
                .build());
        leftConversation.execute(create()
                .from(stan)
                .build());

        // then
        assertFalse(john.getConversation().isPresent());
        assertFalse(stan.getConversation().isPresent());
        assertFalse(conversations.findBy("conversationId").isPresent());
    }
}