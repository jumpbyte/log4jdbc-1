package fr.ms.log4jdbc.context;

import java.sql.Connection;
import java.sql.Driver;

public interface Log4JdbcContext {
    ConnectionContext newConnectionContext(Connection connection, Class clazz);

    ConnectionContext newConnectionContext(Connection connection, Driver driver, String url);
}
