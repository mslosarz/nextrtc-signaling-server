package org.nextrtc.signalingserver.codec;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.junit.runner.RunWith;

import com.googlecode.zohhak.api.TestWith;
import com.googlecode.zohhak.api.runners.ZohhakRunner;

@RunWith(ZohhakRunner.class)
public class MessageDecoderValidatorTest {

	private MessageDecoder decoder = new MessageDecoder();

	@TestWith(value = { //
			"true;		{'from':'A','to':'B','signal':'C', content: null}",//
			"true;		{'from':null,'to':null,'signal':null, content: null}",//
			"true;		{\"signal\" : join, \"content\" : fish , 'member' : null}",//
			"false;		{'from': A}"//
	}, separator = ";")
	public void testValidation(boolean result, String json) {
		// given json

		// when
		boolean actual = decoder.willDecode(json);

		// then
		assertThat(actual, is(result));
	}
}
