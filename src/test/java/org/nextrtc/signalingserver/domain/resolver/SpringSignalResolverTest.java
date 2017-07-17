package org.nextrtc.signalingserver.domain.resolver;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.Test;
import org.nextrtc.signalingserver.BaseTest;
import org.nextrtc.signalingserver.cases.SignalHandler;
import org.nextrtc.signalingserver.domain.Signal;
import org.nextrtc.signalingserver.domain.Signals;
import org.springframework.beans.factory.annotation.Autowired;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

public class SpringSignalResolverTest extends BaseTest {

    @Autowired
    private SpringSignalResolver signals;

    @Test
    public void shouldCheckResolvingSignalBasedOnString() throws Exception {
        // given

        // when
        Pair<Signal, SignalHandler> existing = signals.resolve(Signals.FINALIZE);

        // then
        assertNotNull(existing);
        assertThat(existing.getKey(), is(Signal.FINALIZE));
    }

    @Test
    public void shouldReturnDefaultImplementationOnNotExistingSignal() throws Exception {
        // given

        // when
        Pair<Signal, SignalHandler> notExisting = signals.resolve("not existing");

        // then
        assertNotNull(notExisting);
        assertThat(notExisting.getKey(), is(Signal.EMPTY));
    }
}
