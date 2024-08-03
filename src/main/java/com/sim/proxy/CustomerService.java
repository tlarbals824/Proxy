package com.sim.proxy;

public interface CustomerService {

    @MyTransactional
    void create();
}
