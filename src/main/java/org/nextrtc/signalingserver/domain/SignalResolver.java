package org.nextrtc.signalingserver.domain;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.nextrtc.signalingserver.cases.SignalHandler;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static java.util.Optional.ofNullable;

@Component
@Scope("singleton")
public class SignalResolver implements InitializingBean {

    @Autowired
    private Map<String, SignalHandler> handlers;

    private Map<Signal, SignalHandler> customHandlers = new HashMap<>();

    public Pair<Signal, SignalHandler> resolve(String string) {
        Signal signal = Signal.fromString(string);
        if (customHandlers.containsKey(signal)) {
            return new ImmutablePair<>(signal, customHandlers.get(signal));
        }
        return new ImmutablePair<>(Signal.EMPTY, handlers.get(Signal.EMPTY.handlerName()));
    }

    public Optional<Pair<Signal, SignalHandler>> addCustomHandler(Signal signal, SignalHandler handler) {
        SignalHandler oldValue = customHandlers.put(signal, handler);
        if (oldValue == null) {
            return Optional.empty();
        }
        return Optional.of(new ImmutablePair<>(signal, handler));
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        for (Signal signal : Signal.values()) {
            addCustomHandler(signal, getHandler(signal));
        }
    }

    private SignalHandler getHandler(Signal signal) {
        return ofNullable(handlers.get(signal.handlerName())).orElse(handlers.get(Signal.EMPTY.handlerName()));
    }
}
