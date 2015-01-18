package org.nextrtc.signalingserver;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import javax.websocket.RemoteEndpoint.Async;
import javax.websocket.Session;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatcher;
import org.mockito.Mockito;
import org.nextrtc.signalingserver.domain.*;
import org.nextrtc.signalingserver.domain.signal.CreateSignal;
import org.nextrtc.signalingserver.domain.signal.JoinSignal;
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
	private JoinSignal join;

	@Autowired
	private Members members;

	@Autowired
	private Conversations conversations;

	@Before
	public void reset() {
		for (String id : conversations.getAllKeys()) {
			conversations.unregister(id);
		}
		for (String id : members.getAllKeys()) {
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
				.build();
	}

	protected Member mockMember(String string, ArgumentMatcher<Message> match) {
		return Member.create()//
				.session(mockSession(string, match))//
				.build();
	}

	protected Conversation createConversationWithOwner(String conversationName, String memberName, MessageMatcher match) {
		Member member = mockMember(memberName, match);
		members.register(member);
		create.executeMessage(InternalMessage.create()//
				.from(member)//
				.content(conversationName)//
				.build());
		match.reset();
		return conversations.findBy(conversationName).get();
	}

	protected Conversation joinMemberToConversation(String conversationName, String memberName, MessageMatcher match) {
		Member member = mockMember(memberName, match);
		members.register(member);
		join.executeMessage(InternalMessage.create()//
				.from(member)//
				.content(conversationName)//
				.build());
		match.reset();
		return conversations.findBy(conversationName).get();
	}

	protected Conversation joinMemberToConversation(String conversationName, String memberName) {
		return joinMemberToConversation(conversationName, memberName, new MessageMatcher());
	}

	protected Conversation createConversationWithOwner(String conversation, String member) {
		return createConversationWithOwner(conversation, member, new MessageMatcher());
	}
}
