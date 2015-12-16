package fr.ms.log4jdbc.proxy.operation.factory;

import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.SQLException;

import fr.ms.lang.reflect.TimeInvocation;
import fr.ms.log4jdbc.context.internal.ConnectionContext;
import fr.ms.log4jdbc.proxy.handler.Log4JdbcOperation;
import fr.ms.log4jdbc.proxy.handler.Log4JdbcOperationFactory;
import fr.ms.log4jdbc.proxy.operation.ConnectionOperation;

public class ConnectionOperationFactory implements Log4JdbcOperationFactory {

    private final ConnectionContext connectionContext;

    public ConnectionOperationFactory(final ConnectionContext connectionContext, final Connection connection) {
	this.connectionContext = connectionContext;
	try {
	    final boolean autoCommit = connection.getAutoCommit();
	    connectionContext.setAutoCommit(autoCommit);
	} catch (final SQLException e) {

	}
    }

    public Log4JdbcOperation newLog4JdbcOperation(final TimeInvocation timeInvocation, final Object proxy, final Method method, final Object[] args) {

	final Log4JdbcOperation operation = new ConnectionOperation(connectionContext, timeInvocation, proxy, method, args);

	return operation;
    }
}
