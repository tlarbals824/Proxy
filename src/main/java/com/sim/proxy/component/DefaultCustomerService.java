package com.sim.proxy.component;

import com.sim.proxy.framework.annotation.MyTransactional;
import com.sim.proxy.framework.annotation.Component;
import com.sim.proxy.framework.annotation.Time;


@Component
public class DefaultCustomerService implements CustomerService {

    @Time
    @MyTransactional
    public void create() {
        System.out.println("create()");
    }
}
