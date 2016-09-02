package org.nextrtc.signalingserver.cases;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.nextrtc.signalingserver.BaseTest;
import org.nextrtc.signalingserver.MessageMatcher;
import org.nextrtc.signalingserver.domain.InternalMessage;
import org.nextrtc.signalingserver.domain.Member;
import org.nextrtc.signalingserver.exception.Exceptions;
import org.nextrtc.signalingserver.repository.Members;
import org.springframework.beans.factory.annotation.Autowired;

public class JoinConversationTest extends BaseTest {

    @Autowired
    private JoinConversation joinConversation;

    @Autowired
    private Members members;

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test
    public void shouldThrowExceptionWhenConversationDoesntExists() throws Exception {
        // given
        MessageMatcher match = new MessageMatcher();
        Member member = mockMember("Jan", match);
        members.register(member);

        // then
        exception.expectMessage(Exceptions.CONVERSATION_NOT_FOUND.name());
        // when
        joinConversation.execute(InternalMessage.create()//
                .from(member)//
                .content("new conversation")//
                .build());

    }

}