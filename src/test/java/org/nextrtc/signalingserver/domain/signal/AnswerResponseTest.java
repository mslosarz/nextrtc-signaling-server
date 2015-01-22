package org.nextrtc.signalingserver.domain.signal;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.nextrtc.signalingserver.BaseTest;
import org.nextrtc.signalingserver.repository.Members;
import org.springframework.beans.factory.annotation.Autowired;

public class AnswerResponseTest extends BaseTest {
	@Autowired
	private OfferResponse offerResponse;

	@Autowired
	private Members members;

	@Rule
	public ExpectedException expect = ExpectedException.none();

	@Test
	public void should() throws Exception {
		// given

		// when

		// then
	}

}
