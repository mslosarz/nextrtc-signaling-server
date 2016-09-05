package org.nextrtc.signalingserver.cases;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.nextrtc.signalingserver.BaseTest;
import org.nextrtc.signalingserver.MessageMatcher;
import org.nextrtc.signalingserver.domain.Member;
import org.nextrtc.signalingserver.domain.Signal;
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
    private LeftConversation leftConversation;

    @Test
    public void shouldThrowAnExceptionWhenConversationDoesntExists() throws Exception {
        // given
        MessageMatcher johnMatcher = new MessageMatcher();
        Member john = mockMember("Jan", johnMatcher);
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
        MessageMatcher johnMatcher = new MessageMatcher();
        Member john = mockMember("Jan", johnMatcher);
        members.register(john);
        createConversation("conversationId", john);

        // when
        leftConversation.execute(create()
                .from(john)
                .build());

        // then
        assertFalse(john.getConversation().isPresent());
    }
}