package org.nextrtc.signalingserver.domain;

import org.apache.commons.lang3.tuple.Pair;
import org.nextrtc.signalingserver.cases.SignalHandler;

import java.util.Optional;

public interface SignalResolver {
    Pair<Signal, SignalHandler> resolve(String string);

    Optional<Pair<Signal, SignalHandler>> addCustomSignal(Signal signal, SignalHandler handler);
}
