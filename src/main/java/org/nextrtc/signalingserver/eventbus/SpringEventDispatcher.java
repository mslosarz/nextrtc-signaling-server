package org.nextrtc.signalingserver.eventbus;

import lombok.extern.slf4j.Slf4j;
import org.nextrtc.signalingserver.Names;
import org.nextrtc.signalingserver.api.NextRTCEvents;
import org.nextrtc.signalingserver.api.annotation.NextRTCEventListener;
import org.springframework.aop.framework.Advised;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Map;

import static org.springframework.core.annotation.AnnotationUtils.getValue;

@Slf4j
@Component(Names.EVENT_DISPATCHER)
@Scope("singleton")
@NextRTCEventListener
public class SpringEventDispatcher extends AbstractEventDispatcher {
    @Autowired
    private ApplicationContext context;

    protected Collection<Object> getNextRTCEventListeners() {
        Map<String, Object> beans = context.getBeansWithAnnotation(NextRTCEventListener.class);
        beans.remove(Names.EVENT_DISPATCHER);
        return beans.values();
    }

    @Override
    protected NextRTCEvents[] getSupportedEvents(Object listener) {
        try {
            if (AopUtils.isJdkDynamicProxy(listener)) {
                listener = ((Advised) listener).getTargetSource().getTarget();
            }
        } catch (Exception e) {
            return new NextRTCEvents[0];
        }
        return (NextRTCEvents[]) getValue(listener.getClass().getAnnotation(NextRTCEventListener.class));
    }
}
