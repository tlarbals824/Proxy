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
    public DefaultCustomerService defaultCustomerService() {
        return new DefaultCustomerService();
    }

    @Bean
    public MyTransactionDetector myTransactionDetector() {
        return new MyTransactionDetector();
    }

    @Bean
    public MyTransactionalBeanPostProcessor myTransactionalBeanPostProcessor(
            MyTransactionDetector myTransactionDetector
    ) {
        return new MyTransactionalBeanPostProcessor(myTransactionDetector);
    }


    @Bean
    public ApplicationRunner applicationRunner(CustomerService customerService) {
        return new ApplicationRunner() {
            @Override
            public void run(ApplicationArguments args) throws Exception {
                customerService.create();
            }
        };
    }
}
