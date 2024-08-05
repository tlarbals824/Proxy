package com.sim.proxy.component;

import java.sql.Connection;
import java.sql.SQLException;

public class TransactionCustomerServiceProxy implements CustomerService {
    private final DefaultCustomerService customerService;
    private final ThreadLocal<Connection> connection = new ThreadLocal<>();

    public TransactionCustomerServiceProxy(DefaultCustomerService customerService) {
        this.customerService = customerService;
    }

    @Override
    public void create() {
        try {
            if (connection.get() == null) {
                connection.set(DatabaseUtils.getConnection());
            }
            DatabaseUtils.startTransaction(connection.get());
            customerService.create();
            DatabaseUtils.endTransaction(connection.get(), true);
            connection.remove();
        } catch (SQLException e) {
            System.out.println("Transaction failed: "+e);
            try {
                DatabaseUtils.endTransaction(connection.get(), false);
                connection.remove();
            } catch (SQLException ex) {
                System.out.println("Rollback failed");
            }
        }
    }
}
