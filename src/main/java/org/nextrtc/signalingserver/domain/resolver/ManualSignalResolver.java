package org.nextrtc.signalingserver.domain.resolver;

import org.nextrtc.signalingserver.cases.SignalHandler;

import java.util.Map;

public class ManualSignalResolver extends AbstractSignalResolver {

    public ManualSignalResolver(Map<String, SignalHandler> handlers) {
        super(handlers);
        initByDefault();
    }
}
