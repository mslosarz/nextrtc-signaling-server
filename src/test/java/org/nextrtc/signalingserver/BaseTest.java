package org.nextrtc.signalingserver;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.concurrent.ScheduledFuture;

import javax.websocket.RemoteEndpoint.Async;
import javax.websocket.Session;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatcher;
import org.mockito.Mockito;
import org.nextrtc.signalingserver.domain.InternalMessage;
import org.nextrtc.signalingserver.domain.Member;
import org.nextrtc.signalingserver.domain.Message;
import org.nextrtc.signalingserver.domain.signal.Create;
import org.nextrtc.signalingserver.domain.signal.Join;
import org.nextrtc.signalingserver.repository.Conversations;
import org.nextrtc.signalingserver.repository.Members;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@ContextConfiguration(classes = { TestConfig.class })
@RunWith(SpringJUnit4ClassRunner.class)
public abstract class BaseTest {

	@Autowired
	private Create create;

	@Autowired
	private Join join;

	@Autowired
	private Members members;

	@Autowired
	private Conversations conversations;

	@Before
	public void reset() {
		for (String id : conversations.getAllIds()) {
			conversations.remove(id);
		}
		for (String id : members.getAllIds()) {
			members.unregister(id);
		}
	}

	protected Session mockSession(String string) {
		return mockSession(string, new MessageMatcher());
	}

	protected Session mockSession(String id, ArgumentMatcher<Message> match) {
		Session s = mock(Session.class);
		when(s.getId()).thenReturn(id);
		Async mockAsync = mockAsync(match);
		when(s.getAsyncRemote()).thenReturn(mockAsync);
		return s;
	}

	protected Session mockSession(String id, Async async, ArgumentMatcher<Message> match) {
		Session s = mock(Session.class);
		when(s.getId()).thenReturn(id);
		when(s.getAsyncRemote()).thenReturn(async);
		return s;
	}

	protected Async mockAsync(ArgumentMatcher<Message> match) {
		Async async = mock(Async.class);
		when(async.sendObject(Mockito.argThat(match))).thenReturn(null);
		return async;
	}

	protected Member mockMember(String string) {
		return Member.create()//
				.session(mockSession(string))//
				.ping(mock(ScheduledFuture.class))//
				.build();
	}

	protected Member mockMember(String string, ArgumentMatcher<Message> match) {
		return Member.create()//
				.session(mockSession(string, match))//
				.ping(mock(ScheduledFuture.class))//
				.build();
	}

	protected MessageMatcher createConversationWithOwner(String conversationName, String memberName) {
		MessageMatcher match = new MessageMatcher();
		Member member = mockMember(memberName, match);
		members.register(member);
		create.executeMessage(InternalMessage.create()//
				.from(member)//
				.content(conversationName)//
				.build());
		match.reset();
		return match;
	}

	protected MessageMatcher joinMemberToConversation(String conversationName, String memberName) {
		MessageMatcher match = new MessageMatcher();
		Member member = mockMember(memberName, match);
		members.register(member);
		join.executeMessage(InternalMessage.create()//
				.from(member)//
				.content(conversationName)//
				.build());
		match.reset();
		return match;
	}

}
