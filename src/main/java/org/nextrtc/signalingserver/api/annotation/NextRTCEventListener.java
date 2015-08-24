package org.nextrtc.signalingserver.api.annotation;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.nextrtc.signalingserver.api.NextRTCEvents;

@Retention(RUNTIME)
@Target({ TYPE })
public @interface NextRTCEventListener {

	NextRTCEvents[] value() default {};
}
