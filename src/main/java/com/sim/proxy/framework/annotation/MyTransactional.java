package com.sim.proxy.framework.annotation;

import org.springframework.aot.hint.annotation.Reflective;

import java.lang.annotation.*;

@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface MyTransactional {
}
