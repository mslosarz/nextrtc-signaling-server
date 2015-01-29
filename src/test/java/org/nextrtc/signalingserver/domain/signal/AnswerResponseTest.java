package org.nextrtc.signalingserver.domain.signal;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.nextrtc.signalingserver.BaseTest;
import org.nextrtc.signalingserver.MessageMatcher;
import org.nextrtc.signalingserver.domain.InternalMessage;
import org.nextrtc.signalingserver.domain.Member;
import org.nextrtc.signalingserver.repository.Members;
import org.springframework.beans.factory.annotation.Autowired;

public class AnswerResponseTest extends BaseTest {

	@Autowired
	private AnswerResponse answerResponse;

	@Autowired
	private Members members;

	@Rule
	public ExpectedException expect = ExpectedException.none();

	@Test
	public void shouldThrowExceptionWhenRecipientIsntGiven() throws Exception {
		// given
		createConversationWithOwner("conv", "alice");
		joinMemberToConversation("conv", "bob");

		// then
		expect.expectMessage(containsString("0002"));

		// when
		answerResponse.executeMessage(InternalMessage.create()//
				.from(members.findBy("alice").get())//
				.to(null)//
				.content("Session Description Protocol")//
				.build());
	}

	@Test
	public void shouldThrowExceptionWhenRecipientIsntInConversation() throws Exception {
		// given
		createConversationWithOwner("conv", "alice");
		Member bob = mockMember("bob");

		// then
		expect.expectMessage(containsString("0002"));

		// when
		answerResponse.executeMessage(InternalMessage.create()//
				.from(members.findBy("alice").get())//
				.to(bob)//
				.content("Session Description Protocol")//
				.build());
	}

	@Test
	public void shouldSendFinalizeResponse() throws Exception {
		// given
		MessageMatcher messagesToAlice = createConversationWithOwner("conv", "alice");
		joinMemberToConversation("conv", "bob");

		// when
		answerResponse.executeMessage(InternalMessage.create()//
				.from(members.findBy("alice").get())//
				.to(members.findBy("bob").get())//
				.content("Session Description Protocol From Alice")//
				.build());

		// then
		assertThat(messagesToAlice.getMessages().size(), is(1));
		assertThat(messagesToAlice.getMessage().getFrom(), is("bob"));
		assertThat(messagesToAlice.getMessage().getTo(), is("alice"));
		assertThat(messagesToAlice.getMessage().getContent(), is("Session Description Protocol From Alice"));
		assertThat(messagesToAlice.getMessage().getSignal(), is("finalize"));
	}

}
