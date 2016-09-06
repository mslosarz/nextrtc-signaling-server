package org.nextrtc.signalingserver.domain;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.nextrtc.signalingserver.cases.SignalHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class SignalResolver {

    @Autowired
    private Map<String, SignalHandler> handlers;

    public Pair<Signal, SignalHandler> resolve(String string) {
        for (Signal signal : Signal.values()) {
            if (signal.is(string)) {
                return new ImmutablePair<>(signal, handlers.get(signal.handlerName()));
            }
        }
        return new ImmutablePair<>(Signal.EMPTY, handlers.get(Signal.EMPTY.handlerName()));
    }

}
