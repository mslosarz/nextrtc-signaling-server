package org.nextrtc.signalingserver.cases;

import org.junit.Test;
import org.nextrtc.signalingserver.BaseTest;
import org.nextrtc.signalingserver.MessageMatcher;
import org.nextrtc.signalingserver.domain.Member;
import org.nextrtc.signalingserver.repository.Members;
import org.springframework.beans.factory.annotation.Autowired;

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

        // when
        textMessage.execute(create()
                .from(john)
                .to(stan)
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

}