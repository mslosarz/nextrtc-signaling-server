package org.nextrtc.signalingserver.domain.signal;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import javax.websocket.Session;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.nextrtc.signalingserver.TestConfig;
import org.nextrtc.signalingserver.domain.Conversation;
import org.nextrtc.signalingserver.domain.InternalMessage;
import org.nextrtc.signalingserver.domain.Member;
import org.nextrtc.signalingserver.register.Conversations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.google.common.base.Optional;

@ContextConfiguration(classes = { TestConfig.class })
@RunWith(SpringJUnit4ClassRunner.class)
public class CreateSignalTest {

	@Autowired
	private CreateSignal create;

	@Autowired
	private Conversations conversations;

	@Test
	public void shouldCreateConversation() throws Exception {
		// given
		InternalMessage message = InternalMessage.create()//
				.from(Member.builder()//
						.session(mockSession("sessionId"))//
						.build())//
				.signal(create)//
				.content("c1")//
				.build();

		// when
		create.executeMessage(message);

		// then
		Optional<Conversation> conv = conversations.findBy("c1");
		assertTrue(conv.isPresent());
	}

	private Session mockSession(String id) {
		Session s = mock(Session.class);
		when(s.getId()).thenReturn(id);
		return s;
	}

}
