package org.nextrtc.signalingserver.domain;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.nextrtc.signalingserver.api.annotation.NextRTCEvents.CONVERSATION_CREATED;
import static org.nextrtc.signalingserver.api.annotation.NextRTCEvents.SESSION_STARTED;

import java.util.List;

import javax.websocket.Session;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.nextrtc.signalingserver.TestConfig;
import org.nextrtc.signalingserver.api.NextRTCEvent;
import org.nextrtc.signalingserver.api.annotation.NextRTCEventListener;
import org.nextrtc.signalingserver.domain.ServerTest.ServerEventCheck;
import org.nextrtc.signalingserver.exception.Exceptions;
import org.nextrtc.signalingserver.exception.SignalingException;
import org.nextrtc.signalingserver.register.Members;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@ContextConfiguration(classes = { TestConfig.class, ServerEventCheck.class })
@RunWith(SpringJUnit4ClassRunner.class)
public class ServerTest {

	@NextRTCEventListener({ SESSION_STARTED, CONVERSATION_CREATED })
	public static class ServerEventCheck extends EventChecker {

	}

	@Rule
	public ExpectedException expect = ExpectedException.none();

	@Autowired
	private Server server;

	@Autowired
	private Members members;

	@Autowired
	private ServerEventCheck eventCheckerCall;

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

	@Test
	public void shouldRegisterUserOnWebSocketConnection() throws Exception {
		// given
		Session session = mock(Session.class);
		when(session.getId()).thenReturn("s1");

		// when
		server.register(session);

		// then
		assertTrue(members.findBy("s1").isPresent());

	}

	@Test
	public void shouldCreateConversationOnCreateSignal() throws Exception {
		// given
		Session session = mock(Session.class);
		when(session.getId()).thenReturn("s1");
		server.register(session);

		// when
		server.handle(Message.create()//
				.signal("create")//
				.build(), session);

		// then
		List<NextRTCEvent> events = eventCheckerCall.getEvents();
		assertThat(events.size(), is(2));
	}

	@Before
	public void resetObjects() {
		eventCheckerCall.reset();
		members.unregister("s1");
	}
}
