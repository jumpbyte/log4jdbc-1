package fr.ms.log4jdbc.context.jdbc;

import java.sql.Connection;
import java.sql.Driver;

import fr.ms.log4jdbc.context.ConnectionContext;
import fr.ms.log4jdbc.context.Log4JdbcContext;

public class Log4JdbcContextJDBC implements Log4JdbcContext {

    public ConnectionContext newConnectionContext(final Connection connection, final Class clazz) {
	return new ConnectionContextJDBC("JDBC", clazz);
    }

    public ConnectionContext newConnectionContext(final Connection connection, final Driver driver, final String url) {
	return new ConnectionContextJDBC("JDBC", driver, url);
    }
}
