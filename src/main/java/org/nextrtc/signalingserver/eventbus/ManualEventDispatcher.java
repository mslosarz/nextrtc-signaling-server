package org.nextrtc.signalingserver.eventbus;

import org.nextrtc.signalingserver.api.NextRTCEventBus;
import org.nextrtc.signalingserver.api.NextRTCEvents;
import org.nextrtc.signalingserver.api.NextRTCHandler;
import org.nextrtc.signalingserver.api.annotation.NextRTCEventListener;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@NextRTCEventListener
public class ManualEventDispatcher extends AbstractEventDispatcher {

    private final List<Object> events = new ArrayList<>();

    private final NextRTCEventBus eventBus;

    public ManualEventDispatcher(NextRTCEventBus eventBus) {
        this.eventBus = eventBus;
    }

    protected Collection<Object> getNextRTCEventListeners() {
        return events;
    }

    @Override
    protected NextRTCEvents[] getSupportedEvents(Object listener) {
        return (NextRTCEvents[]) getValue(listener.getClass().getAnnotation(NextRTCEventListener.class));

    }

    public static Object getValue(Annotation annotation) {
        if (annotation != null) {
            try {
                Method method = annotation.annotationType().getDeclaredMethod("value", new Class[0]);
                method.setAccessible(true);
                return method.invoke(annotation, new Object[0]);
            } catch (Exception var3) {
                return null;
            }
        } else {
            return null;
        }
    }

    public void addListener(NextRTCHandler handler) {
        if (handler != null) {
            eventBus.register(handler);
            events.add(handler);
        }
    }
}
