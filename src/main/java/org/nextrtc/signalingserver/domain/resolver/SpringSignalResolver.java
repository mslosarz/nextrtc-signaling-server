package org.nextrtc.signalingserver.domain.resolver;

import org.nextrtc.signalingserver.cases.SignalHandler;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@Scope("singleton")
public class SpringSignalResolver extends AbstractSignalResolver implements InitializingBean {

    @Autowired
    public SpringSignalResolver(Map<String, SignalHandler> handlers) {
        super(handlers);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        initByDefault();
    }

}
