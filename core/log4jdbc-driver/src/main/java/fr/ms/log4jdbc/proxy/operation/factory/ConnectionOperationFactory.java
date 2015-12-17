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

    public void executeAutoCommit(final Object[] args) {
	final boolean autoCommit = ((Boolean) args[0]).booleanValue();
	executeAutoCommit(autoCommit);
    }

    private void executeAutoCommit(final boolean autoCommit) {
	boolean commit = false;

	if (autoCommit && !this.autoCommit) {
	    commit = true;
	}

	this.autoCommit = autoCommit;
	connectionContext.getTransactionContext().setEnabled(!this.autoCommit);

	if (commit) {
	    executeCommit();
	}
    }

    public void executeCommit() {
	connectionContext.commit();
	connectionContext.resetTransaction();
    }

    public void executeSavePoint(final Object savePoint) {
	connectionContext.setSavePoint(savePoint);
    }

    public void executeRollback(final Object[] args) {
	Object savePoint = null;
	if (args != null && args[0] != null) {
	    savePoint = args[0];
	}
	executeRollback(savePoint);
    }

    private void executeRollback(final Object savePoint) {
	connectionContext.rollback(savePoint);
	if (savePoint == null) {
	    connectionContext.resetTransaction();
	}
    }

    public void executeClose() {
	connectionContext.resetContext();
    }
}
