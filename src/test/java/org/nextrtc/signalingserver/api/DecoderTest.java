package org.nextrtc.signalingserver.api;

import org.junit.Test;
import org.nextrtc.signalingserver.domain.Message;

import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.isEmptyOrNullString;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

public class DecoderTest {

    private NextRTCServer.MessageDecoder decoder = new NextRTCServer.MessageDecoder();

    @Test
    public void shouldParseBasicObject() {
        // given
        String validJson = "{'from' : 'Alice',"//
                + "'to' : 'Bob',"//
                + "'signal' : 'join',"//
                + "'content' : 'something'}";

        // when
        Message result = decoder.decode(validJson);

        // then
        assertNotNull(result);
        assertThat(result.getFrom(), is("Alice"));
        assertThat(result.getTo(), is("Bob"));
        assertThat(result.getSignal(), is("join"));
        assertThat(result.getContent(), is("something"));
    }

    @Test
    public void shouldParseAlmostEmptyObject() {
        // given
        String validJson = "{'signal' : 'join',"//
                + "'content' : 'something'}";

        // when
        Message result = decoder.decode(validJson);

        // then
        assertNotNull(result);
        assertThat(result.getFrom(), is(EMPTY));
        assertThat(result.getTo(), is(EMPTY));
        assertThat(result.getSignal(), is("join"));
        assertThat(result.getContent(), is("something"));
    }

    @Test
    public void shouldRecognizeAndDisposeXSSAttack() {
        // given
        String validJson = "{'signal' : 'join',"//
                + "'content':'<script>alert(1);</script>'}";

        // when
        Message result = decoder.decode(validJson);

        // then
        assertNotNull(result);
        assertThat(result.getContent(), containsString("&lt;script&gt;alert"));
    }

    @Test
    public void shouldParseRequestWithDoubleQuotes() {
        // given
        String validJson = "{'from' : 'Alice',"//
                + "'to' : 'Bob',"//
                + "'signal' : 'join',"//
                + "'content' : 'something',"//
                + "'parameters' : {'param1' : 'value1'}}".replace("'", "\"");

        // when
        Message result = decoder.decode(validJson);

        // then
        assertNotNull(result);
        assertThat(result.getFrom(), is("Alice"));
        assertThat(result.getTo(), is("Bob"));
        assertThat(result.getSignal(), is("join"));
        assertThat(result.getContent(), is("something"));
    }

    @Test
    public void shouldNormalRequest() {
        // given
        String validJson = "{'from' : 'Alice',"//
                + "'to' : null,"//
                + "'signal' : 'join',"//
                + "'content' : 'aaa'}".replace("'", "\"");

        // when
        Message result = decoder.decode(validJson);

        // then
        assertNotNull(result);
        assertThat(result.getFrom(), is("Alice"));
        assertThat(result.getTo(), isEmptyOrNullString());
        assertThat(result.getSignal(), is("join"));
        assertThat(result.getContent(), is("aaa"));
    }

}
