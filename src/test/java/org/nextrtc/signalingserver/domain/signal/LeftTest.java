package org.nextrtc.signalingserver.domain.signal;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.nextrtc.signalingserver.BaseTest;
import org.nextrtc.signalingserver.MessageMatcher;
import org.nextrtc.signalingserver.domain.InternalMessage;
import org.nextrtc.signalingserver.repository.Conversations;
import org.nextrtc.signalingserver.repository.Members;
import org.springframework.beans.factory.annotation.Autowired;

public class LeftTest extends BaseTest {

	@Autowired
	private Left left;

	@Autowired
	private Members members;

	@Autowired
	private Conversations conversations;

	@Test
	public void shouldSendLeft() throws Exception {
		// given
		createConversationWithOwner("conv", "alice");
		MessageMatcher bobMessages = joinMemberToConversation("conv", "bob");
		bobMessages.reset();

		// when
		left.executeMessage(InternalMessage.create()//
				.from(members.findBy("alice").get())//
				.signal(left)//
				.build());

		// then
		assertThat(bobMessages.getMessages().size(), is(1));
		assertThat(bobMessages.getMessage().getFrom(), is("alice"));
		assertThat(bobMessages.getMessage().getTo(), is("bob"));
		assertThat(bobMessages.getMessage().getSignal(), is("left"));
	}

	@Test
	public void shouldRemoveConversationWhenLastMemberLeft() throws Exception {
		// given
		MessageMatcher aliceMessage = createConversationWithOwner("conv", "alice");
		MessageMatcher bobMessages = joinMemberToConversation("conv", "bob");
		bobMessages.reset();
		aliceMessage.reset();

		// when
		left.executeMessage(InternalMessage.create()//
				.from(members.findBy("alice").get())//
				.build());
		left.execute(InternalMessage.create()//
				.from(members.findBy("bob").get())//
				.build());

		// then
		assertThat(bobMessages.getMessages().size(), is(1));
		assertThat(bobMessages.getMessage().getFrom(), is("alice"));
		assertThat(bobMessages.getMessage().getTo(), is("bob"));
		assertThat(bobMessages.getMessage().getSignal(), is("left"));

		assertThat(aliceMessage.getMessages().size(), is(0));

		assertFalse(conversations.findBy("conv").isPresent());
	}
}
