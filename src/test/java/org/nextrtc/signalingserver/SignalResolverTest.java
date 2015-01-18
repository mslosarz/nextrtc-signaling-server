package org.nextrtc.signalingserver;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.nextrtc.signalingserver.SignalResolverTest.A;
import org.nextrtc.signalingserver.domain.InternalMessage;
import org.nextrtc.signalingserver.domain.SignalResolver;
import org.nextrtc.signalingserver.domain.signal.Signal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.test.context.ContextConfiguration;

@ContextConfiguration(classes = { A.class })
public class SignalResolverTest extends BaseTest {

	@Component
	public static class A implements Signal {

		private String existing = "existing";

		@Override
		public boolean is(String string) {
			return existing.equalsIgnoreCase(string);
		}

		@Override
		public void executeMessage(InternalMessage internal) {
		}

		@Override
		public String name() {
			return existing;
		}
	}

	@Autowired
	private SignalResolver signals;

	@Test
	public void shouldCheckResolvingSignalBasedOnString() throws Exception {
		// given

		// when
		Signal existing = signals.resolve("existing");

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
