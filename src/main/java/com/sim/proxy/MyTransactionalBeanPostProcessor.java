package com.sim.proxy;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

import java.lang.reflect.Method;

public class MyTransactionalBeanPostProcessor implements BeanPostProcessor {
    private final MyTransactionDetector transactionDetector;

    public MyTransactionalBeanPostProcessor(MyTransactionDetector transactionDetector) {
        this.transactionDetector = transactionDetector;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (transactionDetector.hasTransactional(bean)) {
            var pf = new ProxyFactory();
            pf.setInterfaces(bean.getClass().getInterfaces());
            pf.setTarget(bean);
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

            return pf.getProxy(getClass().getClassLoader());
        }
        return BeanPostProcessor.super.postProcessAfterInitialization(bean, beanName);
    }
}
