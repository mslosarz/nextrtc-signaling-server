package org.nextrtc.signalingserver;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import javax.websocket.RemoteEndpoint.Async;
import javax.websocket.Session;

import org.junit.runner.RunWith;
import org.mockito.ArgumentMatcher;
import org.mockito.Mockito;
import org.nextrtc.signalingserver.domain.*;
import org.nextrtc.signalingserver.domain.signal.CreateSignal;
import org.nextrtc.signalingserver.repository.Conversations;
import org.nextrtc.signalingserver.repository.Members;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@ContextConfiguration(classes = { TestConfig.class })
@RunWith(SpringJUnit4ClassRunner.class)
public abstract class BaseTest {

	@Autowired
	private CreateSignal create;

	@Autowired
	private Members members;

	@Autowired
	private Conversations conversations;

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
				.build();
	}

	protected Member mockMember(String string, ArgumentMatcher<Message> match) {
		return Member.create()//
				.session(mockSession(string, match))//
				.build();
	}

	protected Conversation createConversationWithOwner(String conversationName, String memberName, MessageMatcher match) {
		Member member = mockMember(memberName);
		members.register(member);
		create.executeMessage(InternalMessage.create()//
				.from(mockMember(memberName, match))//
				.content(conversationName)//
				.build());
		match.reset();
		return conversations.findBy(conversationName).get();
	}

	protected Conversation createConversationWithOwner(String conversationName, String memberName) {
		return createConversationWithOwner(conversationName, memberName, new MessageMatcher());
	}
}
