package org.nextrtc.signalingserver.domain.conversation;

import org.junit.Test;
import org.nextrtc.signalingserver.BaseTest;
import org.nextrtc.signalingserver.MessageMatcher;
import org.nextrtc.signalingserver.cases.LeftConversation;
import org.nextrtc.signalingserver.domain.Conversation;
import org.nextrtc.signalingserver.domain.InternalMessage;
import org.nextrtc.signalingserver.domain.Member;
import org.nextrtc.signalingserver.repository.Conversations;
import org.springframework.beans.factory.annotation.Autowired;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;

public class BroadcastConversationTest extends BaseTest {

    @Autowired
    private Conversations conversations;

    @Autowired
    private LeftConversation leftConversation;

    @Test
    public void shouldJoinBroadcaster() {
        // given
        MessageMatcher match = new MessageMatcher();
        Member broadcaster = mockMember("John", match);

        // when
        createBroadcastConversation("broadcastId", broadcaster);

        // then
        assertThat(match.getMessage().getSignal(), is("created"));
        assertThat(match.getMessage().getContent(), is("broadcastId"));
        Conversation conversation = conversations.findBy("broadcastId").get();
        assertThat(conversation.isWithoutMember(), is(false));
        assertThat(conversation.has(broadcaster), is(true));
    }

    @Test
    public void shouldJoinBroadcasterAudience() {
        // given
        MessageMatcher match = new MessageMatcher();
        Member broadcaster = mockMember("John", match);
        MessageMatcher audienceMatch = new MessageMatcher();
        Member audience = mockMember("Audience", audienceMatch);
        createBroadcastConversation("broadcastId", broadcaster);

        // when
        joinConversation("broadcastId", audience);

        // then
        assertThat(match.getMessage(0).getSignal(), is("created"));
        assertThat(match.getMessage(0).getContent(), is("broadcastId"));
        assertThat(audienceMatch.getMessage(0).getSignal(), is("joined"));
        assertThat(audienceMatch.getMessage(0).getContent(), is("broadcastId"));
        Conversation conversation = conversations.findBy("broadcastId").get();
        assertThat(conversation.isWithoutMember(), is(false));
        assertThat(conversation.has(broadcaster), is(true));
        assertThat(conversation.has(audience), is(true));
    }

    @Test
    public void shouldAllowToLeftAudienceWithInfoToBroadcaster() {
        // given
        MessageMatcher match = new MessageMatcher();
        Member broadcaster = mockMember("John", match);
        MessageMatcher audienceMatch = new MessageMatcher();
        Member audience = mockMember("Audience", audienceMatch);
        createBroadcastConversation("broadcastId", broadcaster);
        joinConversation("broadcastId", audience);
        match.reset();
        audienceMatch.reset();

        // when
        leftConversation.execute(InternalMessage.create()
                .from(audience)
                .build());

        // then
        assertThat(match.getMessage(0).getSignal(), is("left"));
        assertThat(match.getMessage(0).getFrom(), is(audience.getId()));
        Conversation conversation = conversations.findBy("broadcastId").get();
        assertThat(conversation.isWithoutMember(), is(false));
        assertThat(conversation.has(broadcaster), is(true));
        assertThat(conversation.has(audience), is(false));
    }

    @Test
    public void shouldThrowOutConversationWhenBroadcasterLeft() {
        // given
        Member broadcaster = mockMember("John");
        MessageMatcher audienceMatch = new MessageMatcher();
        Member audience = mockMember("Audience", audienceMatch);
        createBroadcastConversation("broadcastId", broadcaster);
        joinConversation("broadcastId", audience);
        audienceMatch.reset();

        // when
        leftConversation.execute(InternalMessage.create()
                .from(broadcaster)
                .build());

        // then
        assertThat(audienceMatch.getMessage(0).getSignal(), is("left"));
        assertThat(audienceMatch.getMessage(0).getFrom(), is(broadcaster.getId()));
        assertFalse(conversations.findBy("broadcastId").isPresent());
    }
}