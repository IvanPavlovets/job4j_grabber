package ru.job4j.db;

import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Connection, which rollback all commits.
 * It is used for integration test.
 */
public class ConnectionRollback {
    /**
     * Создает connection с autocommit=false mode и вызывом rollback, когда соединение закрыто.
     * Все тестовые методы будут выполняться с виртуальным обьектом Connection.
     * @param connection connection.
     * @return Connection object.
     * @throws SQLException possible exception.
     */
    public static Connection create(Connection cn) throws SQLException {
        cn.setAutoCommit(false);
        return (Connection) Proxy.newProxyInstance(
                ConnectionRollback.class.getClassLoader(),
                new Class[] {Connection.class},
                (proxy, method, args) -> {
                    Object rsl = null;
                    if ("close".equals(method.getName())) {
                        cn.rollback();
                        cn.close();
                    } else {
                        rsl = method.invoke(cn, args);
                    }
                    return rsl;
                }
        );
    }
}
