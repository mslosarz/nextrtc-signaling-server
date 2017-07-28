package org.nextrtc.signalingserver.domain.resolver;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.nextrtc.signalingserver.cases.SignalHandler;
import org.nextrtc.signalingserver.domain.Signal;
import org.nextrtc.signalingserver.domain.SignalResolver;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static java.util.Optional.ofNullable;

public abstract class AbstractSignalResolver implements SignalResolver {

    private final Map<Signal, SignalHandler> customHandlers = new ConcurrentHashMap<>();

    public AbstractSignalResolver(Map<String, SignalHandler> handlers) {
        customHandlers.put(Signal.EMPTY, (msg) -> {
        });
        Map<Signal, SignalHandler> collect = handlers.entrySet().stream().collect(Collectors.toMap(k -> Signal.byHandlerName(k.getKey()), Map.Entry::getValue));
        customHandlers.putAll(collect);
    }

    @Override
    public Pair<Signal, SignalHandler> resolve(String string) {
        Signal signal = Signal.fromString(string);
        if (customHandlers.containsKey(signal)) {
            return new ImmutablePair<>(signal, customHandlers.get(signal));
        }
        return new ImmutablePair<>(Signal.EMPTY, customHandlers.get(Signal.EMPTY));
    }

    @Override
    public Optional<Pair<Signal, SignalHandler>> addCustomSignal(Signal signal, SignalHandler handler) {
        SignalHandler oldValue = customHandlers.put(signal, handler);
        if (oldValue == null) {
            return Optional.empty();
        }
        return Optional.of(new ImmutablePair<>(signal, handler));
    }

    protected void initByDefault() {
        for (Signal signal : Signal.values()) {
            SignalHandler handler = getHandler(signal);
            addCustomSignal(signal, handler);
        }
    }

    private SignalHandler getHandler(Signal signal) {
        return ofNullable(customHandlers.get(signal)).orElse(customHandlers.get(Signal.EMPTY));
    }
}
