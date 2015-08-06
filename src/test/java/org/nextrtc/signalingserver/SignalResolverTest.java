package org.nextrtc.signalingserver;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.nextrtc.signalingserver.domain.Signal;
import org.nextrtc.signalingserver.domain.SignalResolver;
import org.springframework.beans.factory.annotation.Autowired;

public class SignalResolverTest extends BaseTest {

	@Autowired
	private SignalResolver signals;

	@Test
	public void shouldCheckResolvingSignalBasedOnString() throws Exception {
		// given

		// when
		Signal existing = signals.resolve("finalize");

		// then
		assertNotNull(existing);
	}

	@Test
	public void shouldReturnDefaultImplementationOnNotExistingSignal() throws Exception {
		// given

		// when
		Signal notExisting = signals.resolve("not existing");

		// then
		assertNotNull(notExisting);
		assertThat(notExisting, is(Signal.EMPTY));
	}
}
