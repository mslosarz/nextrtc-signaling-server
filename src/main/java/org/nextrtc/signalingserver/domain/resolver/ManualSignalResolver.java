package org.nextrtc.signalingserver.domain.resolver;

import org.nextrtc.signalingserver.cases.SignalHandler;

import javax.inject.Inject;
import java.util.Map;

public class ManualSignalResolver extends AbstractSignalResolver {

    @Inject
    public ManualSignalResolver(Map<String, SignalHandler> handlers) {
        super(handlers);
        initByDefault();
    }
}
