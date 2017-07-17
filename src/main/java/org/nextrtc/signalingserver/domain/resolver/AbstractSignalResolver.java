package org.nextrtc.signalingserver.domain.resolver;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.nextrtc.signalingserver.cases.SignalHandler;
import org.nextrtc.signalingserver.domain.Signal;
import org.nextrtc.signalingserver.domain.SignalResolver;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static java.util.Optional.ofNullable;

public abstract class AbstractSignalResolver implements SignalResolver {

    protected Map<String, SignalHandler> handlers;
    private Map<Signal, SignalHandler> customHandlers = new HashMap<>();

    public AbstractSignalResolver(Map<String, SignalHandler> handlers) {
        this.handlers = handlers;
    }

    @Override
    public Pair<Signal, SignalHandler> resolve(String string) {
        Signal signal = Signal.fromString(string);
        if (customHandlers.containsKey(signal)) {
            return new ImmutablePair<>(signal, customHandlers.get(signal));
        }
        return new ImmutablePair<>(Signal.EMPTY, handlers.get(Signal.EMPTY.handlerName()));
    }

    @Override
    public Optional<Pair<Signal, SignalHandler>> addCustomHandler(Signal signal, SignalHandler handler) {
        SignalHandler oldValue = customHandlers.put(signal, handler);
        if (oldValue == null) {
            return Optional.empty();
        }
        return Optional.of(new ImmutablePair<>(signal, handler));
    }

    protected void initByDefault() {
        for (Signal signal : Signal.values()) {
            addCustomHandler(signal, getHandler(signal));
        }
    }

    private SignalHandler getHandler(Signal signal) {
        return ofNullable(handlers.get(signal.handlerName())).orElse(handlers.get(Signal.EMPTY.handlerName()));
    }
}
