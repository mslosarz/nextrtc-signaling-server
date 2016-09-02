package org.nextrtc.signalingserver.api.annotation;

import org.nextrtc.signalingserver.api.NextRTCEvents;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Retention(RUNTIME)
@Target({TYPE})
public @interface NextRTCEventListener {

    NextRTCEvents[] value() default {};
}
