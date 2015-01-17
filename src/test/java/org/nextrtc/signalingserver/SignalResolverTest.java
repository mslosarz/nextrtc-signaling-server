package org.nextrtc.signalingserver;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.nextrtc.signalingserver.SignalResolverTest.A;
import org.nextrtc.signalingserver.domain.InternalMessage;
import org.nextrtc.signalingserver.domain.SignalResolver;
import org.nextrtc.signalingserver.domain.signal.Signal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@ContextConfiguration(classes = { TestConfig.class, A.class })
@RunWith(SpringJUnit4ClassRunner.class)
public class SignalResolverTest {

	@Component
	public static class A implements Signal {

		@Override
		public boolean is(String string) {
			return "existing".equalsIgnoreCase(string);
		}

		@Override
		public void executeMessage(InternalMessage internal) {
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
