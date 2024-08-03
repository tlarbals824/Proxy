package com.sim.proxy;

import org.springframework.util.ReflectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicBoolean;

public class MyTransactionDetector {

    public boolean hasTransactional(Object o) {
        var hasTransactional = new AtomicBoolean(false);
        var classes = new ArrayList<Class<?>>();
        classes.add(o.getClass());
        Collections.addAll(classes, o.getClass().getInterfaces());
        classes.forEach(it -> {
            ReflectionUtils.doWithMethods(it, method -> {
                if (method.getAnnotation(MyTransactional.class) != null) {
                    hasTransactional.set(true);
                }
            });
        });

        return hasTransactional.get();
    }
}
