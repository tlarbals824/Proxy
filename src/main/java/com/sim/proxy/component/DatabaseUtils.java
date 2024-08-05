package com.sim.proxy.component;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseUtils {
    private static final String URL = "jdbc:mysql://localhost:3306/test";
    private static final String USER = "root";
    private static final String PASSWORD = "root";

    public static Connection getConnection() throws SQLException {
        System.out.println("Getting connection");
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    public static void startTransaction(Connection connection) throws SQLException {
        if (connection != null) {
            connection.setAutoCommit(false);
        }
        System.out.println("Transaction started");
    }

    public static void endTransaction(Connection connection, boolean commit) throws SQLException {
        if (connection != null) {
            try {
                if (commit) {
                    connection.commit();
                } else {
                    connection.rollback();
                }
            } finally {
                connection.setAutoCommit(true);
                connection.close();
            }
        }
        System.out.println("Transaction ended");
    }
}
