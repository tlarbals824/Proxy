package com.sim.proxy;

public class DefaultCustomerService {

    @MyTransactional
    public void create() {
        System.out.println("create()");
    }
}
