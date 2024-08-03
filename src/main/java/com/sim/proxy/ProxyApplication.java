package com.sim.proxy;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

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
                var defaultCustomer = new DefaultCustomerService();


                var proxyInstance = (CustomerService) Proxy
                        .newProxyInstance(defaultCustomer.getClass().getClassLoader(),
                                defaultCustomer.getClass().getInterfaces(), (proxy, method, args1) -> {
                                    System.out.println("calling " + method.getName() + " with arguments [" + args1 + "]");
                                    try {
                                        if (method.getAnnotation(MyTransactional.class) != null) {
                                            System.out.println("starting transaction for " + method.getName());
                                        }
                                        return method.invoke(defaultCustomer, args1);
                                    } finally {
                                        if (method.getAnnotation(MyTransactional.class) != null) {
                                            System.out.println("finishing transaction for " + method.getName());
                                        }
                                    }
                                }); // jdk proxy

                /**
                 * calling create with arguments [null]
                 * create()
                 */
                proxyInstance.create();

            }
        };
    }
}
