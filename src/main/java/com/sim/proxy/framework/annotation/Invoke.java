package com.sim.proxy.framework.annotation;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Invoke {
    // target annotation
    Class<? extends Annotation> value();
}
