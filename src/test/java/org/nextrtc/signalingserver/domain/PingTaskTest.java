package org.nextrtc.signalingserver.domain;

import org.junit.Test;
import org.nextrtc.signalingserver.BaseTest;
import org.nextrtc.signalingserver.MessageMatcher;
import org.nextrtc.signalingserver.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;

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
        new PingTask(member.getConnection(), sender).run();

        // then
        assertThat(messages.getMessage().getSignal(), is(Signals.PING));
    }

    @Test
    public void shouldNotSendMessageWhenSessionIsEnded() {
        // given
        MessageMatcher messages = new MessageMatcher("");
        Connection connection = mockConnection("s1", messages);
        when(connection.isOpen()).thenReturn(false);

        // when
        new PingTask(connection, sender).run();

        // then
        assertThat(messages.getMessages(), hasSize(0));
    }

}