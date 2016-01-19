package fr.ms.log4jdbc.context;

import java.sql.Connection;
import java.sql.Driver;

import fr.ms.log4jdbc.context.jdbc.ConnectionContextJDBC;

public interface Log4JdbcContext {
    ConnectionContextJDBC newConnectionContext(Connection connection, Class clazz);

    ConnectionContextJDBC newConnectionContext(Connection connection, Driver driver, String url);
}
