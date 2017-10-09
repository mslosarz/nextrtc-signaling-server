package org.nextrtc.signalingserver.cases;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.nextrtc.signalingserver.BaseTest;
import org.nextrtc.signalingserver.MessageMatcher;
import org.nextrtc.signalingserver.domain.InternalMessage;
import org.nextrtc.signalingserver.domain.Member;
import org.nextrtc.signalingserver.exception.SignalingException;
import org.nextrtc.signalingserver.property.NextRTCProperties;
import org.nextrtc.signalingserver.repository.ConversationRepository;
import org.nextrtc.signalingserver.repository.Members;
import org.springframework.beans.factory.annotation.Autowired;

import static junit.framework.TestCase.assertTrue;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.nextrtc.signalingserver.exception.Exceptions.CONVERSATION_NOT_FOUND;
import static org.nextrtc.signalingserver.exception.Exceptions.MEMBER_IN_OTHER_CONVERSATION;

public class JoinConversationTest extends BaseTest {

    @Autowired
    private JoinConversation joinConversation;

    @Autowired
    private Members members;

    @Autowired
    private ConversationRepository conversations;

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test
    public void shouldCreateNewConversationIfConversationDoesntExists() throws Exception {
        // given
        MessageMatcher match = new MessageMatcher();
        Member member = mockMember("Jan", match);
        members.register(member);

        // when
        joinConversation.execute(InternalMessage.create()//
                .from(member)//
                .content("new conversation")//
                .build());

        // then
        assertThat(match.getMessage().getSignal(), is("created"));
        assertThat(match.getMessage().getTo(), is("Jan"));
        assertThat(match.getMessage().getContent(), is("new conversation"));
        assertThat(match.getMessage().getCustom().size(), is(1));
        assertThat(match.getMessage().getCustom().get("type"), is("MESH"));
    }

    @Test
    public void shouldCreateNewConversationIfConversationDoesntExists_andHandleType() throws Exception {
        // given
        MessageMatcher match = new MessageMatcher();
        Member member = mockMember("Jan", match);
        members.register(member);

        // when
        joinConversation.execute(InternalMessage.create()//
                .from(member)//
                .content("new conversation")//
                .addCustom("type", "BROADCAST")
                .build());

        // then
        assertThat(match.getMessage().getSignal(), is("created"));
        assertThat(match.getMessage().getTo(), is("Jan"));
        assertThat(match.getMessage().getContent(), is("new conversation"));
        assertThat(match.getMessage().getCustom().size(), is(1));
        assertThat(match.getMessage().getCustom().get("type"), is("BROADCAST"));
    }

    @Test
    public void shouldJoinMemberToConversation() throws Exception {
        // given
        Member member = mockMember("Jan");
        members.register(member);
        Member stach = mockMember("Stach");
        members.register(stach);
        createConversation("conv", stach);

        // when
        joinConversation.execute(InternalMessage.create()//
                .from(member)//
                .content("conv")//
                .build());

        // then
        assertTrue(member.getConversation().isPresent());
    }

    @Test
    public void shouldThrowExceptionWhenUserIsInOtherConversation() throws Exception {
        // given
        Member jan = mockMember("Jan");
        members.register(jan);
        Member stach = mockMember("Stach");
        members.register(stach);
        Member stefan = mockMember("Stefan");
        members.register(stefan);
        createConversation("conv", stach);
        createConversation("conv2", stefan);
        joinConversation.execute(InternalMessage.create()//
                .from(jan)//
                .content("conv2")//
                .build());

        // then
        exception.expect(SignalingException.class);
        exception.expectMessage(MEMBER_IN_OTHER_CONVERSATION.name());

        // when
        joinConversation.execute(InternalMessage.create()//
                .from(jan)//
                .content("conv")//
                .build());
    }

    @Test
    public void shouldThrownAnExceptionWhenJoinToExistingConversationIsSetToTrueAndConversationDoesntExist() throws Exception {
        // given
        NextRTCProperties properties = mock(NextRTCProperties.class);
        when(properties.isJoinOnlyToExisting()).thenReturn(true);
        Member jan = mockMember("Jan");
        members.register(jan);

        // then
        exception.expect(SignalingException.class);
        exception.expectMessage(CONVERSATION_NOT_FOUND.name());

        // when
        new JoinConversation(conversations, null, properties).execute(InternalMessage.create()//
                .from(jan)//
                .content("conv")//
                .build());
    }



}