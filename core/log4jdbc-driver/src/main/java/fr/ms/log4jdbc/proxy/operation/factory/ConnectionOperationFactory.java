package fr.ms.log4jdbc.proxy.operation.factory;

import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.SQLException;

import fr.ms.lang.reflect.TimeInvocation;
import fr.ms.log4jdbc.context.jdbc.ConnectionJDBCContext;
import fr.ms.log4jdbc.proxy.handler.Log4JdbcOperation;
import fr.ms.log4jdbc.proxy.handler.Log4JdbcOperationFactory;
import fr.ms.log4jdbc.proxy.operation.ConnectionOperation;

public class ConnectionOperationFactory implements Log4JdbcOperationFactory {

    private boolean autoCommit = true;

    private final ConnectionJDBCContext connectionContext;

    public ConnectionOperationFactory(final ConnectionJDBCContext connectionContext, final Connection connection) {
	this.connectionContext = connectionContext;

	try {
	    autoCommit = connection.getAutoCommit();
	} catch (final SQLException e) {

	}
    }

    public Log4JdbcOperation newLog4JdbcOperation(final TimeInvocation timeInvocation, final Object proxy, final Method method, final Object[] args) {

	final Log4JdbcOperation operation = new ConnectionOperation(this, connectionContext, timeInvocation, proxy, method, args);

	return operation;
    }

    public boolean isAutoCommit() {
	return autoCommit;
    }

    public void setAutoCommit(final boolean autoCommit) {
	this.autoCommit = autoCommit;
    }

}
