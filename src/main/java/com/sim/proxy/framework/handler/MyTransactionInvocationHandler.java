package com.sim.proxy.framework.handler;

import com.sim.proxy.component.DatabaseUtils;
import com.sim.proxy.framework.annotation.Invoke;
import com.sim.proxy.framework.annotation.MyTransactional;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;

@Invoke(value = MyTransactional.class)
public class MyTransactionInvocationHandler extends CustomInvocationHandler {

    private ThreadLocal<Connection> connection = new ThreadLocal<>();

    public MyTransactionInvocationHandler(Object target) {
        super(target);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        var transcationOptional = getTransactionalMethod(getOverriddenMethod(method));
        if (transcationOptional.isPresent()) {
            return handleTransactionalMethod(method, args);
        }
        return invokeMethod(method, args);
    }

    private Object handleTransactionalMethod(Method method, Object[] args) {
        try {
            if (connection.get() == null) {
                connection.set(DatabaseUtils.getConnection());
            }
            DatabaseUtils.startTransaction(connection.get());
            return invokeMethod(method, args);
        } catch (SQLException | RuntimeException e) {
            System.out.println("Rolling back transaction for " + method.getName());
            throw new RuntimeException("Could not invoke method " + method.getName(), e);
        } finally {
            try {
                DatabaseUtils.endTransaction(connection.get(), true);
                connection.remove();
            } catch (SQLException e) {
                throw new RuntimeException("Could not commit transaction for " + method.getName(), e);
            }
        }
    }

    private Object invokeMethod(Method method, Object[] args) {
        try {
            return method.invoke(target, args);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException("Could not invoke method " + method.getName(), e);
        }
    }

    private Optional<MyTransactional> getTransactionalMethod(Method method) {
        return Optional.ofNullable(method.getAnnotation(MyTransactional.class));
    }
}
