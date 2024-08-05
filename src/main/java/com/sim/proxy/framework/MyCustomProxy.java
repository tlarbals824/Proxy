package com.sim.proxy.framework;

import javassist.util.proxy.ProxyFactory;

import java.util.List;

public class MyCustomProxy {
    private List<Class<?>> interfaces;
    private Object proxy;

    public MyCustomProxy(List<Class<?>> interfaces, Object proxy) {
        this.interfaces = interfaces;
        this.proxy = proxy;
    }

    public Object getProxy() {
        return this.proxy;
    }

    public void printInterfaces() {
        for (Class<?> i : interfaces) {
            System.out.println(i.getName());
        }
    }

    public boolean hasInterface(Class<?> expectedInterface) {
        return interfaces.contains(expectedInterface);
    }
}
