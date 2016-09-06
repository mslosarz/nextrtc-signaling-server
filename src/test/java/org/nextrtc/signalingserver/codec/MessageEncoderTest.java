package org.nextrtc.signalingserver.codec;

import org.junit.Test;
import org.nextrtc.signalingserver.domain.Message;

import javax.websocket.EncodeException;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.nextrtc.signalingserver.domain.Message.create;

public class MessageEncoderTest {

    private MessageEncoder encoder = new MessageEncoder();

    @Test
    public void shouldEncodeObject() throws EncodeException {
        // given
        Message message = create()//
                .from("A")//
                .to("B")//
                .content("con")//
                .signal("sig")//
                .build();

        // when
        String result = encoder.encode(message);

        // then
        assertNotNull(result);
        assertThat(result, containsString(replaceQuotes("'content':'con'")));
        assertThat(result, containsString(replaceQuotes("'from':'A'")));
        assertThat(result, containsString(replaceQuotes("'to':'B'")));
        assertThat(result, containsString(replaceQuotes("'signal':'sig'")));
    }

    @Test
    public void shouldEncodeObjectWithNullValues() throws EncodeException {
        // given
        Message message = create()//
                .from(null)//
                .to(null)//
                .build();

        // when
        String result = encoder.encode(message);

        // then
        assertNotNull(result);
        assertThat(result, not(containsString(replaceQuotes("'from'"))));
        assertThat(result, not(containsString(replaceQuotes("'to'"))));
    }

    private String replaceQuotes(String string) {
        return string.replace("'", "\"");
    }
}
