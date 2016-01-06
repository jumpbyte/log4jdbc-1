package fr.ms.log4jdbc.proxy.jdbc.operation.factory;

import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.SQLException;

import fr.ms.lang.reflect.ProxyOperation;
import fr.ms.lang.reflect.ProxyOperationFactory;
import fr.ms.lang.reflect.TimeInvocation;
import fr.ms.log4jdbc.context.ConnectionContext;
import fr.ms.log4jdbc.proxy.jdbc.operation.ConnectionOperation;

public class ConnectionOperationFactory implements ProxyOperationFactory {

    private boolean autoCommit = true;

    private final ConnectionContext connectionContext;

    public ConnectionOperationFactory(final ConnectionContext connectionContext, final Connection connection) {
	this.connectionContext = connectionContext;

	try {
	    autoCommit = connection.getAutoCommit();
	} catch (final SQLException e) {

	}
    }

    public ProxyOperation newOperation(final TimeInvocation timeInvocation, final Object proxy, final Method method, final Object[] args) {
	final ProxyOperation operation = new ConnectionOperation(this, connectionContext, timeInvocation, method, args);

	return operation;
    }

    public boolean isAutoCommit() {
	return autoCommit;
    }

    public void setAutoCommit(final boolean autoCommit) {
	this.autoCommit = autoCommit;
    }

    public boolean executeAutoCommit(final boolean autoCommit) {
	boolean commit = false;

	if (autoCommit && !this.autoCommit) {
	    commit = true;
	}

	this.autoCommit = autoCommit;

	return commit;
    }
}
