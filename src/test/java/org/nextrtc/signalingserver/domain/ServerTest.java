package org.nextrtc.signalingserver.domain;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.nextrtc.signalingserver.BaseTest;
import org.nextrtc.signalingserver.EventChecker;
import org.nextrtc.signalingserver.MessageMatcher;
import org.nextrtc.signalingserver.api.NextRTCEvents;
import org.nextrtc.signalingserver.api.annotation.NextRTCEventListener;
import org.nextrtc.signalingserver.api.dto.NextRTCEvent;
import org.nextrtc.signalingserver.domain.ServerTest.LocalStreamCreated;
import org.nextrtc.signalingserver.domain.ServerTest.ServerEventCheck;
import org.nextrtc.signalingserver.exception.Exceptions;
import org.nextrtc.signalingserver.exception.SignalingException;
import org.nextrtc.signalingserver.repository.Members;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import javax.websocket.CloseReason;
import javax.websocket.Session;
import java.util.List;

import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.nextrtc.signalingserver.api.NextRTCEvents.CONVERSATION_CREATED;
import static org.nextrtc.signalingserver.api.NextRTCEvents.SESSION_OPENED;

@ContextConfiguration(classes = { ServerEventCheck.class, LocalStreamCreated.class })
public class ServerTest extends BaseTest {

	@NextRTCEventListener({ SESSION_OPENED, CONVERSATION_CREATED })
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
	public void shouldCreateConversation() throws Exception {
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
        // s1Matcher.reset();
		server.handle(Message.create()//
				.signal("join")//
				.content(conversationKey)//
				.build(), s2);

		// then
        assertThat(s1Matcher.getMessages().size(), is(3));
        assertMessage(s1Matcher, 0, EMPTY, "s1", "created", conversationKey);
        assertMessage(s1Matcher, 1, "s2", "s1", "joined", EMPTY);
        assertMessage(s1Matcher, 2, "s2", "s1", "offerRequest", EMPTY);

		assertThat(s2Matcher.getMessages().size(), is(1));
		assertMessage(s2Matcher, 0, EMPTY, "s2", "joined", conversationKey);
	}

    @NextRTCEventListener({ NextRTCEvents.MEDIA_LOCAL_STREAM_CREATED })
	public static class LocalStreamCreated extends EventChecker {

	}

	@Autowired
	private LocalStreamCreated eventLocalStream;

	@Test
	public void shouldCreateConversationJoinMemberAndPassOfferResponseToRestMembers() throws Exception {
		// given
		MessageMatcher s1Matcher = new MessageMatcher();
		MessageMatcher s2Matcher = new MessageMatcher();
		Session s1 = mockSession("s1", s1Matcher);
		Session s2 = mockSession("s2", s2Matcher);
		server.register(s1);
		server.register(s2);

		server.handle(Message.create()//
				.signal("create")//
				.build(), s1);
		String conversationKey = s1Matcher.getMessage().getContent();
		server.handle(Message.create()//
				.signal("join")//
				.content(conversationKey)//
				.build(), s2);
		s1Matcher.reset();
		s2Matcher.reset();

		// when
		// s2 has to create local stream
		server.handle(Message.create()//
				.to("s2")//
				.signal("offerResponse")//
				.content("s2 spd")//
				.build(), s1);

		// then
        assertThat(s2Matcher.getMessages().size(), is(1));
        assertMessage(s2Matcher, 0, "s1", "s2", "answerRequest", "s2 spd");

        assertThat(s1Matcher.getMessages().size(), is(0));
	}

	@Test
	public void shouldCreateConversationJoinMemberAndPassOfferResponseToRestTwoMembers() throws Exception {
		// given
		MessageMatcher s1Matcher = new MessageMatcher();
		MessageMatcher s2Matcher = new MessageMatcher();
		MessageMatcher s3Matcher = new MessageMatcher();
		Session s1 = mockSession("s1", s1Matcher);
		Session s2 = mockSession("s2", s2Matcher);
		Session s3 = mockSession("s3", s3Matcher);
		server.register(s1);
		server.register(s2);
		server.register(s3);

		server.handle(Message.create()//
				.signal("create")//
				.build(), s1);
		String conversationKey = s1Matcher.getMessage().getContent();
		server.handle(Message.create()//
				.signal("join")//
				.content(conversationKey)//
				.build(), s2);
		server.handle(Message.create()//
				.signal("join")//
				.content(conversationKey)//
				.build(), s3);
		s1Matcher.reset();
		s2Matcher.reset();
		s3Matcher.reset();
		// when
		// s2 has to create local stream
		server.handle(Message.create()//
				.to("s1")//
				.signal("offerResponse")//
				.content("s2 spd")//
				.build(), s2);
		// s3 has to create local stream
		server.handle(Message.create()//
				.to("s1")//
				.signal("offerResponse")//
				.content("s3 spd")//
				.build(), s3);

		// then
        assertThat(s2Matcher.getMessages().size(), is(1));
        assertMessage(s2Matcher, 0, "s1", "s2", "answerRequest", "s2 spd");
        assertThat(s3Matcher.getMessages().size(), is(1));
        assertMessage(s3Matcher, 0, "s1", "s3", "answerRequest", "s3 spd");
        assertThat(s1Matcher.getMessages().size(), is(0));
	}

	@Test
	public void shouldExchangeSpds() throws Exception {
		// given
		MessageMatcher s1Matcher = new MessageMatcher();
		MessageMatcher s2Matcher = new MessageMatcher();
		Session s1 = mockSession("s1", s1Matcher);
		Session s2 = mockSession("s2", s2Matcher);
		server.register(s1);
		server.register(s2);

		server.handle(Message.create()//
				.signal("create")//
				.build(), s1);
		// -> created
		String conversationKey = s1Matcher.getMessage().getContent();
		server.handle(Message.create()//
				.signal("join")//
				.content(conversationKey)//
				.build(), s2);
		// -> joined
		// -> offerRequest
		server.handle(Message.create()//
				.to("s1")//
				.signal("offerResponse")//
				.content("s2 spd")//
				.build(), s2);
		// -> answerRequest
		s1Matcher.reset();
		s2Matcher.reset();
		// when
		server.handle(Message.create()//
				.to("s2")//
				.signal("answerResponse")//
				.content("s1 spd")//
				.build(), s1);

		// then
        assertThat(s1Matcher.getMessages().size(), is(1));
        assertMessage(s1Matcher, 0, "s2", "s1", "finalize", "s1 spd");

        assertThat(s2Matcher.getMessages().size(), is(0));
	}

	@Test
    public void shouldExchangeCandidates() throws Exception {
		// given
		MessageMatcher s1Matcher = new MessageMatcher();
		MessageMatcher s2Matcher = new MessageMatcher();
		Session s1 = mockSession("s1", s1Matcher);
		Session s2 = mockSession("s2", s2Matcher);
		server.register(s1);
		server.register(s2);

		server.handle(Message.create()//
				.signal("create")//
				.build(), s1);
		// -> created
		String conversationKey = s1Matcher.getMessage().getContent();
		server.handle(Message.create()//
				.signal("join")//
				.content(conversationKey)//
				.build(), s2);
		// -> joined
		// -> offerRequest
		server.handle(Message.create()//
				.to("s1")//
				.signal("offerResponse")//
				.content("s2 spd")//
				.build(), s2);
		// -> answerRequest
		server.handle(Message.create()//
				.to("s2")//
				.signal("answerResponse")//
				.content("s1 spd")//
				.build(), s1);
		// -> finalize
		s1Matcher.reset();
		s2Matcher.reset();
		server.handle(Message.create()//
				.to("s1")//
				.signal("candidate")//
				.content("candidate s2")//
				.build(), s2);
		server.handle(Message.create()//
				.to("s2")//
				.signal("candidate")//
				.content("candidate s1")//
				.build(), s1);
		// when

		// then
		assertThat(s1Matcher.getMessages().size(), is(1));
		assertMessage(s1Matcher, 0, "s2", "s1", "candidate", "candidate s2");

		assertThat(s2Matcher.getMessages().size(), is(1));
		assertMessage(s2Matcher, 0, "s1", "s2", "candidate", "candidate s1");
	}

	@Test
	public void shouldUnregisterSession() throws Exception {
		// given
		MessageMatcher s1Matcher = new MessageMatcher();
		MessageMatcher s2Matcher = new MessageMatcher();
		Session s1 = mockSession("s1", s1Matcher);
		Session s2 = mockSession("s2", s2Matcher);
		server.register(s1);
		server.register(s2);

		server.handle(Message.create()//
				.signal("create")//
				.build(), s1);
		// -> created
		String conversationKey = s1Matcher.getMessage().getContent();
		server.handle(Message.create()//
				.signal("join")//
				.content(conversationKey)//
				.build(), s2);
		s1Matcher.reset();
		s2Matcher.reset();
		server.unregister(s1, mock(CloseReason.class));
		// when

		// then
		assertThat(s1Matcher.getMessages().size(), is(0));
		assertThat(s2Matcher.getMessages().size(), is(1));
		assertMessage(s2Matcher, 0, "s1", "s2", "left", EMPTY);
	}

	private void assertMessage(MessageMatcher matcher, int number, String from, String to, String signal, String content) {
		assertThat(matcher.getMessage(number).getFrom(), is(from));
		assertThat(matcher.getMessage(number).getTo(), is(to));
		assertThat(matcher.getMessage(number).getSignal(), is(signal));
		assertThat(matcher.getMessage(number).getContent(), is(content));
	}

	@Before
	public void resetObjects() {
		eventCheckerCall.reset();
		eventLocalStream.reset();
		members.unregister("s1");
		members.unregister("s2");
		members.unregister("s3");
	}
}
