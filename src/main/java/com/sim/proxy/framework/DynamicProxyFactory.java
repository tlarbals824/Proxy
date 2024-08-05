package com.sim.proxy.framework;

import com.sim.proxy.ProxyApp;
import com.sim.proxy.framework.annotation.Component;
import com.sim.proxy.framework.annotation.Invoke;
import com.sim.proxy.framework.handler.CustomInvocationHandler;
import org.reflections.Reflections;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.*;

public class DynamicProxyFactory {
    private List<MyCustomProxy> beanRegistry;
    private Map<Class<? extends Annotation>, Class<? extends InvocationHandler>> invocationHandlerMap = new HashMap<>();

    public DynamicProxyFactory(Package packageToScan) {
        Reflections reflections = new Reflections(packageToScan.getName());
        Set<Class<?>> componentClasses = reflections.getTypesAnnotatedWith(Component.class);
        Set<Class<?>> invokeClasses = reflections.getTypesAnnotatedWith(Invoke.class);

        configInvokeHandler(invokeClasses);

        List<?> beans = instantiateBeans(componentClasses);
        beanRegistry = beans.stream()
                .map(bean -> {
                    try {
                        return createProxy(bean);
                    } catch (NoSuchMethodException | InvocationTargetException | InstantiationException |
                             IllegalAccessException e) {
                        throw new RuntimeException("Could not create proxy for bean " + bean.getClass().getName(), e);
                    }
                })
                .toList();
    }

    private List<?> instantiateBeans(Set<Class<?>> annotatedClasses) {
        return annotatedClasses.stream()
                .map(this::instantiateClass)
                .toList();
    }

    private MyCustomProxy createProxy(Object bean) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        Object proxy = bean;
        List<Class<?>> interfaces = new ArrayList<>(List.of(bean.getClass().getInterfaces()));
        for (var entry : invocationHandlerMap.entrySet()) {
            for (var method : bean.getClass().getDeclaredMethods()) {
                if (method.isAnnotationPresent(entry.getKey())) {
                    InvocationHandler handler = entry.getValue().getConstructor(Object.class).newInstance(proxy);
                    proxy = Proxy.newProxyInstance(
                            ProxyApp.class.getClassLoader(),
                            bean.getClass().getInterfaces(),
                            handler);
                    interfaces.add(entry.getValue());
                    break;
                }
            }
        }
        return new MyCustomProxy(interfaces, proxy);
    }

    private Object instantiateClass(Class<?> componentClass) {
        try {
            return componentClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException("Could not instantiate class " + componentClass.getName());
        }
    }

    private void configInvokeHandler(Set<Class<?>> componentClasses) {
        componentClasses.stream()
                .filter(clazz -> clazz.isAnnotationPresent(Invoke.class) && CustomInvocationHandler.class.isAssignableFrom(clazz))
                .forEach(clazz -> {
                    Invoke invoke = clazz.getAnnotation(Invoke.class);
                    invocationHandlerMap.put(invoke.value(), (Class<? extends InvocationHandler>) clazz);
                });
    }

    public <T> T getBean(Class<T> clazz) {
        Object proxy = beanRegistry.stream()
                .filter(bean -> bean.hasInterface(clazz))
                .findFirst()
                .map(MyCustomProxy::getProxy)
                .orElseThrow(() -> new RuntimeException("Bean not found for class " + clazz.getName()));

        return clazz.cast(proxy);
    }

}
