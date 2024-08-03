package com.sim.proxy;

public class DefaultCustomerService implements CustomerService {
    @Override
    public void create() {
        System.out.println("create()");
    }
}
