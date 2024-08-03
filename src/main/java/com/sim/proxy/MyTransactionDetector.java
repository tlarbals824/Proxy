package com.sim.proxy;

import org.springframework.util.ReflectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicBoolean;

public class MyTransactionDetector {

    public boolean hasTransactional(Class<?> clazz){
        var hasTransactional = new AtomicBoolean(false);
        var classes = new ArrayList<Class<?>>();
        classes.add(clazz);
        Collections.addAll(classes, clazz.getInterfaces());
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
