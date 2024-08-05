package com.sim.proxy.framework;

import com.sim.proxy.framework.handler.CustomInvocationHandler;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

public class ProxyExtractUtils {

    public static Object getOriginalObject(Object proxy) {
        Object originalTarget = proxy;
        while(Proxy.isProxyClass(originalTarget.getClass())) {
            InvocationHandler invocationHandler = Proxy.getInvocationHandler(originalTarget);
            if(invocationHandler instanceof CustomInvocationHandler customInvocationHandler) {
                originalTarget = customInvocationHandler.getTarget();
            }else{
                break;
            }
        }
        return originalTarget;
    }
}
