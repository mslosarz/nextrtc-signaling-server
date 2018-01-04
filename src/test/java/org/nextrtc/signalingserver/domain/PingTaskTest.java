package org.nextrtc.signalingserver.domain;

import org.junit.Test;
import org.nextrtc.signalingserver.BaseTest;
import org.nextrtc.signalingserver.MessageMatcher;
import org.nextrtc.signalingserver.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;

import javax.websocket.Session;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

public class PingTaskTest extends BaseTest {

    @Autowired
    private MessageSender sender;

    @Autowired
    private MemberRepository members;

    @Test
    public void shouldSendMessageWhenSessionIsOpen() {
        // given
        MessageMatcher messages = new MessageMatcher("");
        Member member = mockMember("s1", messages);
        members.register(member);


        // when
        new PingTask(member.getSession(), sender).run();

        // then
        assertThat(messages.getMessage().getSignal(), is(Signals.PING));
    }

    @Test
    public void shouldNotSendMessageWhenSessionIsEnded() {
        // given
        MessageMatcher messages = new MessageMatcher("");
        Session session = mockSession("s1", messages);
        when(session.isOpen()).thenReturn(false);

        // when
        new PingTask(session, sender).run();

        // then
        assertThat(messages.getMessages(), hasSize(0));
    }

}