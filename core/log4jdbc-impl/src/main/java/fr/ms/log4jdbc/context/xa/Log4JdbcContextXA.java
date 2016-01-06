package fr.ms.log4jdbc.context.xa;

import java.sql.Driver;

import fr.ms.log4jdbc.context.ConnectionContext;
import fr.ms.log4jdbc.context.Log4JdbcContext;
import fr.ms.log4jdbc.context.jdbc.ConnectionContextJDBC;

public class Log4JdbcContextXA implements Log4JdbcContext {

    private ConnectionContext connectionContext;

    private final XAResourceContextXA xaResourceContext = new XAResourceContextXA();

    public ConnectionContext newConnectionContext(final Class clazz) {
	connectionContext = new ConnectionContextJDBC(clazz);
	return connectionContext;
    }

    public ConnectionContext newConnectionContext(final Driver driver, final String url) {
	connectionContext = new ConnectionContextJDBC(driver, url);
	return connectionContext;
    }

    public ConnectionContext getConnectionContext() {
	return connectionContext;
    }

    public XAResourceContextXA getxaResourceContext() {
	return xaResourceContext;
    }
}
