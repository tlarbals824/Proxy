package com.sim.proxy;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.concurrent.atomic.AtomicBoolean;

@SpringBootApplication
public class ProxyApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProxyApplication.class, args);
    }

    @Bean
    public ApplicationRunner applicationRunner() {
        return new ApplicationRunner() {
            @Override
            public void run(ApplicationArguments args) throws Exception {
                var target = new DefaultCustomerService();

                var pf = new ProxyFactory();
                pf.setInterfaces(target.getClass().getInterfaces());
                pf.setTarget(target);
                pf.addAdvice(new MethodInterceptor() {
                    @Override
                    public Object invoke(MethodInvocation invocation) throws Throwable {
                        Method method = invocation.getMethod();
                        Object[] arguments = invocation.getArguments();
                        System.out.println("calling " + method.getName() + " with arguments [" + arguments + "]");
                        try {
                            if (method.getAnnotation(MyTransactional.class) != null) {
                                System.out.println("starting transaction for " + method.getName());
                            }
                            return method.invoke(target, arguments);
                        } finally {
                            if (method.getAnnotation(MyTransactional.class) != null) {
                                System.out.println("finishing transaction for " + method.getName());
                            }
                        }
                    }
                });

                var proxyInstance = (CustomerService) pf.getProxy(getClass().getClassLoader());

                proxyInstance.create();
            }
        };
    }
}
