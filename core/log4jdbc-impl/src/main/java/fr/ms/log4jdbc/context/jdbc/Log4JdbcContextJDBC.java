package fr.ms.log4jdbc.context.jdbc;

import java.sql.Connection;
import java.sql.Driver;
import java.util.Map;
import java.util.WeakHashMap;

import fr.ms.log4jdbc.context.Log4JdbcContext;
import fr.ms.util.CollectionsUtil;

public class Log4JdbcContextJDBC implements Log4JdbcContext {

    private final static Map context = CollectionsUtil.synchronizedMap(new WeakHashMap());

    public ConnectionContextJDBC newConnectionContext(final Connection connection, final Class clazz) {
	ConnectionContextJDBC connectionContextJDBC = (ConnectionContextJDBC) context.get(connection);

	if (connectionContextJDBC == null) {
	    connectionContextJDBC = new ConnectionContextJDBC(clazz);
	    context.put(connection, connectionContextJDBC);
	}

	return connectionContextJDBC;
    }

    public ConnectionContextJDBC newConnectionContext(final Connection connection, final Driver driver, final String url) {
	ConnectionContextJDBC connectionContextJDBC = (ConnectionContextJDBC) context.get(connection);

	if (connectionContextJDBC == null) {
	    connectionContextJDBC = new ConnectionContextJDBC(driver, url);
	    context.put(connection, connectionContextJDBC);
	}

	return connectionContextJDBC;
    }
}
