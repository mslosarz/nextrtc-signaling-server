package org.nextrtc.signalingserver;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import javax.websocket.RemoteEndpoint.Async;
import javax.websocket.Session;

import org.junit.runner.RunWith;
import org.mockito.ArgumentMatcher;
import org.mockito.Mockito;
import org.nextrtc.signalingserver.domain.Message;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@ContextConfiguration(classes = { TestConfig.class })
@RunWith(SpringJUnit4ClassRunner.class)
public abstract class BaseTest {

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

}
