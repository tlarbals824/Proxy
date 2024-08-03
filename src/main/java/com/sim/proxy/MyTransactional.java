package com.sim.proxy;

import org.springframework.aot.hint.annotation.Reflective;

import java.lang.annotation.*;

@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
@Reflective
public @interface MyTransactional {
}
