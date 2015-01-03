package org.nextrtc.signalingserver.codec;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.nextrtc.signalingserver.domain.Message.create;

import javax.websocket.EncodeException;

import org.junit.Test;
import org.nextrtc.signalingserver.domain.Message;

public class MessageEncoderTest {

	private MessageEncoder encoder = new MessageEncoder();

	@Test
	public void shouldEncodeObject() throws EncodeException {
		// given
		Message message = create()//
				.withFrom("A")//
				.withTo("B")//
				.withContent("con")//
				.withSignal("sig")//
				.withParameter("param1", "noga")//
				.build();

		// when
		String result = encoder.encode(message);

		// then
		assertNotNull(result);
		assertThat(result, containsString(replaceQuotes("'content':'con'")));
		assertThat(result, containsString(replaceQuotes("'from':'A'")));
		assertThat(result, containsString(replaceQuotes("'to':'B'")));
		assertThat(result, containsString(replaceQuotes("'signal':'sig'")));
		assertThat(result, containsString(replaceQuotes("'param1':'noga'")));
	}

	@Test
	public void shouldEncodeObjectWithNullValues() throws EncodeException {
		// given
		Message message = create()//
				.withFrom(null)//
				.withTo(null)//
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
