package com.sim.proxy.framework.handler;

import com.sim.proxy.framework.ProxyExtractUtils;
import com.sim.proxy.framework.annotation.Invoke;
import com.sim.proxy.framework.annotation.MyTransactional;
import com.sim.proxy.framework.annotation.Time;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Optional;

@Invoke(value = Time.class)
public class TimeCheckInvocationHandler extends CustomInvocationHandler {

    public TimeCheckInvocationHandler(Object target) {
        super(target);
    }

    @Override
    public Object invoke(Object proxy, java.lang.reflect.Method method, Object[] args) throws Throwable {
        var timeOptional = getTimeMethod(getOverriddenMethod(method));
        if (timeOptional.isPresent()) {
            return handleTimeCheckMethod(method, args);
        }
        return invokeMethod(method, args);
    }

    private Optional<Time> getTimeMethod(Method method) {
        return Optional.ofNullable(method.getAnnotation(Time.class));
    }

    private Object handleTimeCheckMethod(Method method, Object[] args) {
        try {
            long startTime = System.currentTimeMillis();
            Object result = invokeMethod(method, args);
            long endTime = System.currentTimeMillis();
            System.out.println("Time taken to execute " + method.getName() + " is " + (endTime - startTime) + "ms");
            return result;
        } catch (RuntimeException e) {
            throw e;
        }
    }

    private Object invokeMethod(Method method, Object[] args) {
        try {
            return method.invoke(target, args);
        } catch (IllegalAccessException | java.lang.reflect.InvocationTargetException e) {
            throw new RuntimeException("Could not invoke method " + method.getName(), e);
        }
    }



}
