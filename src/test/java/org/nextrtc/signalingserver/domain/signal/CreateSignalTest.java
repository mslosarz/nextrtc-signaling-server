package org.nextrtc.signalingserver.domain.signal;

import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.nextrtc.signalingserver.BaseTest;
import org.nextrtc.signalingserver.MessageMatcher;
import org.nextrtc.signalingserver.domain.*;
import org.nextrtc.signalingserver.repository.Conversations;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.base.Optional;

public class CreateSignalTest extends BaseTest {

	@Autowired
	private CreateSignal create;

	@Autowired
	private Conversations conversations;

	@Test
	public void shouldCreateConversation() throws Exception {
		// given
		MessageMatcher matcher = new MessageMatcher();
		InternalMessage message = InternalMessage.create()//
				.from(Member.builder()//
						.session(mockSession("sessionId", matcher))//
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
		assertThat(send.getParameters(), notNullValue());
	}
}
