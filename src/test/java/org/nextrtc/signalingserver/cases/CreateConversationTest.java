package org.nextrtc.signalingserver.cases;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.nextrtc.signalingserver.BaseTest;
import org.nextrtc.signalingserver.EventChecker;
import org.nextrtc.signalingserver.MessageMatcher;
import org.nextrtc.signalingserver.api.NextRTCEvents;
import org.nextrtc.signalingserver.api.annotation.NextRTCEventListener;
import org.nextrtc.signalingserver.domain.Conversation;
import org.nextrtc.signalingserver.domain.InternalMessage;
import org.nextrtc.signalingserver.domain.Member;
import org.nextrtc.signalingserver.domain.ServerEventCheck;
import org.nextrtc.signalingserver.exception.SignalingException;
import org.nextrtc.signalingserver.repository.Conversations;
import org.nextrtc.signalingserver.repository.Members;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.test.context.ContextConfiguration;

import java.util.Optional;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.nextrtc.signalingserver.api.NextRTCEvents.CONVERSATION_CREATED;
import static org.nextrtc.signalingserver.exception.Exceptions.CONVERSATION_NAME_OCCUPIED;
import static org.nextrtc.signalingserver.exception.Exceptions.MEMBER_IN_OTHER_CONVERSATION;

@ContextConfiguration(classes = ServerEventCheck.class)
public class CreateConversationTest extends BaseTest {

    @Component
    @NextRTCEventListener(CONVERSATION_CREATED)
    public static class ServerEventCheck extends EventChecker {

    }

    @Autowired
    private CreateConversation create;

    @Autowired
    private Conversations conversations;

    @Autowired
    private Members members;

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Autowired
    private ServerEventCheck eventCall;

    @Test
    public void shouldCreateConversation() throws Exception {
        // given
        MessageMatcher match = new MessageMatcher();
        Member member = mockMember("Jan", match);
        members.register(member);

        // when
        create.execute(InternalMessage.create()//
                .from(member)//
                .content("new conversation")//
                .build());

        // then
        Optional<Conversation> optional = conversations.findBy("new conversation");
        assertThat(optional.isPresent(), is(true));
        Conversation conv = optional.get();
        assertThat(conv.has(member), is(true));
        assertThat(match.getMessage().getSignal(), is("created"));
        assertThat(match.getMessage().getCustom().get("type"), is("MESH"));
        assertThat(eventCall.getEvents().size(), is(1));
        assertThat(eventCall.getEvents().get(0).type(), is(NextRTCEvents.CONVERSATION_CREATED));
    }

    @Test
    public void shouldCreateConversation_BROADCAST() throws Exception {
        // given
        MessageMatcher match = new MessageMatcher();
        Member member = mockMember("Jan", match);
        members.register(member);

        // when
        create.execute(InternalMessage.create()//
                .from(member)//
                .content("new conversation")//
                .addCustom("type", "BROADCAST")
                .build());

        // then
        Optional<Conversation> optional = conversations.findBy("new conversation");
        assertThat(optional.isPresent(), is(true));
        Conversation conv = optional.get();
        assertThat(conv.has(member), is(true));
        assertThat(match.getMessage().getSignal(), is("created"));
        assertThat(match.getMessage().getCustom().get("type"), is("BROADCAST"));
        assertThat(eventCall.getEvents().size(), is(1));
        assertThat(eventCall.getEvents().get(0).type(), is(NextRTCEvents.CONVERSATION_CREATED));
    }

    @Test
    public void shouldThrowExceptionWhenConversationExists() throws Exception {
        // given
        Member other = mockMember("Other");
        members.register(other);
        create.execute(InternalMessage.create()//
                .from(other)//
                .content("new conversation")//
                .build());

        Member member = mockMember("Jan");
        members.register(member);

        // then
        exception.expect(SignalingException.class);
        exception.expectMessage(CONVERSATION_NAME_OCCUPIED.name());

        // when
        create.execute(InternalMessage.create()//
                .from(member)//
                .content("new conversation")//
                .build());
    }

    @Test
    public void shouldThrowExceptionWhenUserIsInOtherConversation() throws Exception {
        // given
        MessageMatcher match = new MessageMatcher();
        Member member = mockMember("Jan", match);
        members.register(member);
        create.execute(InternalMessage.create()//
                .from(member)//
                .content("new conversation")//
                .build());

        // then
        exception.expect(SignalingException.class);
        exception.expectMessage(MEMBER_IN_OTHER_CONVERSATION.name());

        // when
        create.execute(InternalMessage.create()//
                .from(member)//
                .content("second conversation")//
                .build());
    }
}
