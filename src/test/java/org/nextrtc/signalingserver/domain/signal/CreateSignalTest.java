package org.nextrtc.signalingserver.domain.signal;

import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

import java.util.Optional;
import java.util.concurrent.ScheduledFuture;

import org.junit.Test;
import org.nextrtc.signalingserver.BaseTest;
import org.nextrtc.signalingserver.MessageMatcher;
import org.nextrtc.signalingserver.domain.*;
import org.nextrtc.signalingserver.repository.Conversations;
import org.springframework.beans.factory.annotation.Autowired;

public class CreateSignalTest extends BaseTest {

	@Autowired
	private Create create;

	@Autowired
	private Conversations conversations;

	@Test
	public void shouldCreateConversation() throws Exception {
		// given
		MessageMatcher matcher = new MessageMatcher();
		InternalMessage message = InternalMessage.create()//
				.from(Member.create()//
						.session(mockSession("sessionId", matcher))//
						.ping(mock(ScheduledFuture.class))//
						.build())//
				.signal(create)//
				.content("c1")//
				.build();

		// when
		create.executeMessage(message);

		// then
		Optional<Conversation> conv = conversations.findBy("c1");
		assertTrue(conv.isPresent());
		Message send = matcher.getMessage();
		assertNotNull(send);
		assertThat(send.getFrom(), is(EMPTY));
		assertThat(send.getTo(), is("sessionId"));
		assertThat(send.getSignal(), is("created"));
		assertThat(send.getContent(), is("c1"));
	}
}
