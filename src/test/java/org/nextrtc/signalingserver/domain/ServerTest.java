package org.nextrtc.signalingserver.domain;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import javax.websocket.Session;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.nextrtc.signalingserver.TestConfig;
import org.nextrtc.signalingserver.exception.Exceptions;
import org.nextrtc.signalingserver.exception.SignalingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@ContextConfiguration(classes = { TestConfig.class })
@RunWith(SpringJUnit4ClassRunner.class)
public class ServerTest {

	@Rule
	public ExpectedException expect = ExpectedException.none();

	@Autowired
	private Server server;

	@Test
	public void shouldThrowExceptionWhenMemberDoesntExists() throws Exception {
		// given
		Session session = mock(Session.class);
		when(session.getId()).thenReturn("s1");

		// then
		expect.expect(SignalingException.class);
		expect.expectMessage(Exceptions.MEMBER_NOT_FOUND.name());

		// when
		server.handle(mock(Message.class), session);
	}

}
