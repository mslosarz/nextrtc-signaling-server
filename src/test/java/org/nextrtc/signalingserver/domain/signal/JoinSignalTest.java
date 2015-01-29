package org.nextrtc.signalingserver.domain.signal;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.nextrtc.signalingserver.exception.Exceptions.CONVERSATION_NOT_FOUND;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.nextrtc.signalingserver.BaseTest;
import org.nextrtc.signalingserver.MessageMatcher;
import org.nextrtc.signalingserver.domain.Conversation;
import org.nextrtc.signalingserver.domain.InternalMessage;
import org.nextrtc.signalingserver.domain.Member;
import org.nextrtc.signalingserver.repository.Conversations;
import org.springframework.beans.factory.annotation.Autowired;

public class JoinSignalTest extends BaseTest {

	@Autowired
	private JoinSignal join;

	@Autowired
	private Conversations conversations;

	@Autowired
	private CreatedSignal create;

	@Rule
	public ExpectedException expect = ExpectedException.none();

	@Test
	public void shouldThrowAnExceptionWhenSomebodyTriesToJoinToNotExistingConversation() throws Exception {
		// given
		Member bob = mockMember("bob");

		// then
		expect.expectMessage(containsString(CONVERSATION_NOT_FOUND.getErrorCode()));

		// when
		join.executeMessage(InternalMessage.create()//
				.from(bob)//
				.content("not existing one")//
				.build());
	}

	@Test
	public void shouldJoinNewMemberToConversation() throws Exception {
		// given
		MessageMatcher sentToAlice = createConversationWithOwner("conv", "alice");
		MessageMatcher sentToBob = new MessageMatcher();
		Member bob = mockMember("bob", sentToBob);

		// when
		join.executeMessage(InternalMessage.create()//
				.from(bob)//
				.content("conv")//
				.build());

		// then
		Conversation conv = conversations.findBy("conv").get();
		assertThat(conv.getMembers().size(), is(2));

		assertThat(sentToAlice.getMessages().size(), is(2));

		assertThat(sentToAlice.getMessage(0).getTo(), is("alice"));
		assertThat(sentToAlice.getMessage(0).getFrom(), is("bob"));
		assertThat(sentToAlice.getMessage(0).getSignal(), is("joined"));

		assertThat(sentToAlice.getMessage(1).getTo(), is("alice"));
		assertThat(sentToAlice.getMessage(1).getFrom(), is("bob"));
		assertThat(sentToAlice.getMessage(1).getSignal(), is("offerRequest"));

		assertThat(sentToBob.getMessages().size(), is(1));
		assertThat(sentToBob.getMessage().getTo(), is("bob"));
		assertThat(sentToBob.getMessage().getSignal(), is("joined"));
		assertThat(sentToBob.getMessage().getContent(), is("conv"));
	}
}
