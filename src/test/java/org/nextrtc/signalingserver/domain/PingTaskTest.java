package org.nextrtc.signalingserver.domain;

import org.junit.Test;
import org.nextrtc.signalingserver.BaseTest;
import org.nextrtc.signalingserver.MessageMatcher;

import javax.websocket.Session;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

public class PingTaskTest extends BaseTest {

    @Test
    public void shouldSendMessageWhenSessionIsOpen() {
        // given
        MessageMatcher messages = new MessageMatcher("");
        Session session = mockSession("s1", messages);
        when(session.isOpen()).thenReturn(true);
        assertTrue(session.isOpen());

        // when
        new PingTask(session).run();

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
        new PingTask(session).run();

        // then
        assertThat(messages.getMessages(), hasSize(0));
    }

}