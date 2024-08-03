package com.sim.proxy;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.SmartInstantiationAwareBeanPostProcessor;

import java.lang.reflect.Method;

public class MyTransactionalBeanPostProcessor implements SmartInstantiationAwareBeanPostProcessor {
    private final MyTransactionDetector transactionDetector;

    public MyTransactionalBeanPostProcessor(MyTransactionDetector transactionDetector) {
        this.transactionDetector = transactionDetector;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (transactionDetector.hasTransactional(bean.getClass())) {
            var proxy = getProxy(bean, bean.getClass());
            return proxy.getProxy(getClass().getClassLoader());
        }
        return bean;
    }

    @Override
    public Class<?> determineBeanType(Class<?> beanClass, String beanName) throws BeansException {
        if (transactionDetector.hasTransactional(beanClass)) {
            return getProxy(null, beanClass).getProxyClass(getClass().getClassLoader());
        }
        return SmartInstantiationAwareBeanPostProcessor.super.determineBeanType(beanClass, beanName);
    }

    private ProxyFactory getProxy(Object bean, Class<?> clzzName) {
        var pf = new ProxyFactory();

        if (bean != null) {
            pf.setTarget(bean);
        }

        pf.setInterfaces(clzzName.getInterfaces());
        pf.setTargetClass(clzzName);

        pf.addAdvice((MethodInterceptor) invocation -> {
            Method method = invocation.getMethod();
            Object[] arguments = invocation.getArguments();
            System.out.println("calling " + method.getName() + " with arguments [" + arguments + "]");
            try {
                if (method.getAnnotation(MyTransactional.class) != null) {
                    System.out.println("starting transaction for " + method.getName());
                }
                return method.invoke(bean, arguments);
            } finally {
                if (method.getAnnotation(MyTransactional.class) != null) {
                    System.out.println("finishing transaction for " + method.getName());
                }
            }
        });
        return pf;
    }
}
