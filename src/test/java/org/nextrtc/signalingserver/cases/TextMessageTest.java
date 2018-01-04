package org.nextrtc.signalingserver.cases;

import org.junit.Test;
import org.nextrtc.signalingserver.BaseTest;
import org.nextrtc.signalingserver.MessageMatcher;
import org.nextrtc.signalingserver.domain.Member;
import org.nextrtc.signalingserver.domain.Signal;
import org.nextrtc.signalingserver.repository.Members;
import org.springframework.beans.factory.annotation.Autowired;

import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.nextrtc.signalingserver.domain.InternalMessage.create;

public class TextMessageTest extends BaseTest {

    @Autowired
    private TextMessage textMessage;

    @Autowired
    private Members members;

    @Test
    public void shouldSendMessageFromOneToAnother() throws Exception {
        // given
        MessageMatcher johnMatcher = new MessageMatcher();
        MessageMatcher stanMatcher = new MessageMatcher();
        Member john = mockMember("Jan", johnMatcher);
        Member stan = mockMember("Stefan", stanMatcher);
        members.register(john);
        members.register(stan);
        createConversation("c", john);
        await().until(() -> johnMatcher.has(m -> m.getSignal().equals("created")).check());
        joinConversation("c", stan);
        await().until(() -> johnMatcher.has(m -> m.getSignal().equals("newJoined")).check());
        johnMatcher.reset();
        stanMatcher.reset();

        // when
        textMessage.execute(create()
                .from(john)
                .to(stan)
                .signal(Signal.TEXT)
                .content("Hello!")
                .addCustom("type", "Greeting")
                .build());

        // then
        assertThat(johnMatcher.getMessages(), hasSize(0));
        assertThat(stanMatcher.getMessages(), hasSize(1));
        assertThat(stanMatcher.getMessage().getContent(), is("Hello!"));
        assertThat(stanMatcher.getMessage().getFrom(), is("Jan"));
        assertThat(stanMatcher.getMessage().getTo(), is("Stefan"));
        assertThat(stanMatcher.getMessage().getSignal(), is("text"));
        assertThat(stanMatcher.getMessage().getCustom().get("type"), is("Greeting"));
    }

    @Test
    public void shouldSendMessageToAllMemberOfConversationIfToIsEmpty() throws Exception {
        // given
        MessageMatcher johnMatcher = new MessageMatcher();
        MessageMatcher stanMatcher = new MessageMatcher();
        MessageMatcher markMatcher = new MessageMatcher();
        Member john = mockMember("Jan", johnMatcher);
        Member stan = mockMember("Stefan", stanMatcher);
        Member mark = mockMember("Marek", markMatcher);
        members.register(john);
        members.register(stan);
        members.register(mark);
        createConversation("c", john);
        await().until(() -> johnMatcher.has(m -> m.getSignal().equals("created")).check());
        joinConversation("c", stan);
        joinConversation("c", mark);
        await().until(() -> stanMatcher.has(m -> m.getSignal().equals("newJoined")).check());
        await().until(() -> markMatcher.has(m -> m.getSignal().equals("newJoined")).check());
        johnMatcher.reset();
        stanMatcher.reset();
        markMatcher.reset();

        // when
        textMessage.execute(create()
                .from(john)
                .signal(Signal.TEXT)
                .content("Hello!")
                .addCustom("type", "Greeting")
                .build());

        // then
        assertThat(johnMatcher.getMessages(), hasSize(0));
        assertThat(stanMatcher.getMessages(), hasSize(1));
        assertThat(markMatcher.getMessages(), hasSize(1));
        assertMessage(stanMatcher, 0, "Jan", "Stefan", "text", "Hello!");
        assertThat(stanMatcher.getMessage().getCustom().get("type"), is("Greeting"));
        assertMessage(markMatcher, 0, "Jan", "Marek", "text", "Hello!");
        assertThat(stanMatcher.getMessage().getCustom().get("type"), is("Greeting"));

    }

    @Test
    public void shouldSendMessageFromOneToAnotherBuInSameConversation() throws Exception {
        // given
        MessageMatcher johnMatcher = new MessageMatcher();
        MessageMatcher stanMatcher = new MessageMatcher();
        Member john = mockMember("Jan", johnMatcher);
        Member stan = mockMember("Stefan", stanMatcher);
        members.register(john);
        members.register(stan);
        createConversation("d", john);
        johnMatcher.reset();

        // when
        textMessage.execute(create()
                .from(john)
                .to(stan)
                .signal(Signal.TEXT)
                .content("Hello!")
                .addCustom("type", "Greeting")
                .build());

        // then
        assertThat(johnMatcher.getMessages(), hasSize(0));
        assertThat(stanMatcher.getMessages(), hasSize(0));
    }

}