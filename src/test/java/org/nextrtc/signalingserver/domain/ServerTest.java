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
import org.nextrtc.signalingserver.BaseTest;
import org.nextrtc.signalingserver.EventChecker;
import org.nextrtc.signalingserver.MessageMatcher;
import org.nextrtc.signalingserver.api.NextRTCEvent;
import org.nextrtc.signalingserver.api.annotation.NextRTCEventListener;
import org.nextrtc.signalingserver.domain.ServerTest.ServerEventCheck;
import org.nextrtc.signalingserver.exception.Exceptions;
import org.nextrtc.signalingserver.exception.SignalingException;
import org.nextrtc.signalingserver.repository.Members;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

@ContextConfiguration(classes = { ServerEventCheck.class })
public class ServerTest extends BaseTest {

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
		Session session = mockSession("s1");
		server.register(session);

		// when
		server.handle(Message.create()//
				.signal("create")//
				.build(), session);

		// then
		List<NextRTCEvent> events = eventCheckerCall.getEvents();
		assertThat(events.size(), is(2));
	}

	@Test
	public void shouldCreateConversationCreate() throws Exception {
		// given
		MessageMatcher s1Matcher = new MessageMatcher();
		MessageMatcher s2Matcher = new MessageMatcher();
		Session s1 = mockSession("s1", s1Matcher);
		Session s2 = mockSession("s2", s2Matcher);
		server.register(s1);
		server.register(s2);

		// when
		server.handle(Message.create()//
				.signal("create")//
				.build(), s1);

		// then
		assertThat(s1Matcher.getMessages().size(), is(1));
		assertThat(s1Matcher.getMessage().getSignal(), is("created"));
		assertThat(s2Matcher.getMessages().size(), is(0));
	}

	@Test
	public void shouldCreateConversationThenJoinAndSendOfferRequest() throws Exception {
		// given
		MessageMatcher s1Matcher = new MessageMatcher();
		MessageMatcher s2Matcher = new MessageMatcher();
		Session s1 = mockSession("s1", s1Matcher);
		Session s2 = mockSession("s2", s2Matcher);
		server.register(s1);
		server.register(s2);

		// when
		server.handle(Message.create()//
				.signal("create")//
				.build(), s1);
		String conversationKey = s1Matcher.getMessage().getContent();
		s1Matcher.reset();
		server.handle(Message.create()//
				.signal("join")//
				.content(conversationKey)//
				.build(), s2);

		// then
		assertThat(s1Matcher.getMessages().size(), is(2));
		assertThat(s1Matcher.getMessage().getFrom(), is("s2"));
		assertThat(s1Matcher.getMessage().getTo(), is("s1"));
		assertThat(s1Matcher.getMessage().getSignal(), is("joined"));

		assertThat(s1Matcher.getMessage(1).getFrom(), is("s2"));
		assertThat(s1Matcher.getMessage(1).getTo(), is("s1"));
		assertThat(s1Matcher.getMessage(1).getSignal(), is("offerRequest"));

		assertThat(s2Matcher.getMessages().size(), is(1));
		assertThat(s2Matcher.getMessage().getSignal(), is("joined"));
		assertThat(s2Matcher.getMessage().getContent(), is(conversationKey));
	}

	@Before
	public void resetObjects() {
		eventCheckerCall.reset();
		members.unregister("s1");
	}
}
