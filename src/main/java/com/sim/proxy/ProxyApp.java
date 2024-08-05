package com.sim.proxy;

import com.sim.proxy.component.CustomerService;
import com.sim.proxy.component.DefaultCustomerService;
import com.sim.proxy.component.TransactionCustomerServiceProxy;
import com.sim.proxy.framework.DynamicProxyFactory;

public class ProxyApp {

    public static void main(String[] args) {
        DynamicProxyFactory dynamicProxyFactory = new DynamicProxyFactory(ProxyApp.class.getPackage());

        CustomerService customerService = dynamicProxyFactory.getBean(CustomerService.class);
        customerService.create();
    }
}
