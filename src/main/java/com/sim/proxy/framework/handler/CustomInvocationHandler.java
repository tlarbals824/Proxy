package com.sim.proxy.framework.handler;

import com.sim.proxy.framework.ProxyExtractUtils;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public abstract class CustomInvocationHandler implements InvocationHandler {
    protected Object target;
    protected Class<?> targetClass;

    public CustomInvocationHandler(Object target) {
        this.target = target;
        this.targetClass = target.getClass();
    }

    public Object getTarget(){
        return target;
    }

    protected Method getOverriddenMethod(Method method) throws NoSuchMethodException {
        return ProxyExtractUtils.getOriginalObject(target)
                .getClass()
                .getDeclaredMethod(method.getName(), method.getParameterTypes());
    }

}
