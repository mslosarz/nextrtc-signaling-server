package org.nextrtc.signalingserver.cases;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.nextrtc.signalingserver.BaseTest;
import org.nextrtc.signalingserver.MessageMatcher;
import org.nextrtc.signalingserver.domain.InternalMessage;
import org.nextrtc.signalingserver.domain.Member;
import org.nextrtc.signalingserver.repository.Members;
import org.springframework.beans.factory.annotation.Autowired;

import static junit.framework.TestCase.assertTrue;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class JoinConversationTest extends BaseTest {

    @Autowired
    private JoinConversation joinConversation;

    @Autowired
    private Members members;

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
        MessageMatcher match = new MessageMatcher();
        Member member = mockMember("Jan", match);
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

}