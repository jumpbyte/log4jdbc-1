package fr.ms.log4jdbc.proxy.operation;

import java.lang.reflect.Method;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.Statement;

import fr.ms.lang.reflect.TimeInvocation;
import fr.ms.log4jdbc.context.jdbc.ConnectionJDBCContext;
import fr.ms.log4jdbc.proxy.Log4JdbcProxy;
import fr.ms.log4jdbc.proxy.operation.factory.ConnectionOperationFactory;

public class ConnectionOperation extends AbstractOperation {

    private final ConnectionOperationFactory connectionOperationFactory;

    public ConnectionOperation(final ConnectionOperationFactory connectionOperationFactory, final ConnectionJDBCContext connectionContext,
	    final TimeInvocation timeInvocation, final Object proxy, final Method method, final Object[] args) {
	super(connectionContext, timeInvocation, proxy, method, args);
	this.connectionOperationFactory = connectionOperationFactory;
    }

    public void buildSqlOperation() {
	final String nameMethod = method.getName();

	if (nameMethod.equals("setAutoCommit")) {
	    setAutoCommit(args);
	} else if (nameMethod.equals("commit")) {
	    commit();
	} else if (nameMethod.equals("rollback")) {
	    rollback(args);
	} else if (nameMethod.equals("setSavepoint")) {
	    setSavepoint(timeInvocation.getInvoke());
	} else if (nameMethod.equals("close")) {
	    close();
	}
    }

    public Object buildResultMethod() {
	final Object invoke = timeInvocation.getInvoke();
	if (invoke != null) {
	    if (invoke instanceof CallableStatement) {
		final CallableStatement callableStatement = (CallableStatement) invoke;
		final String sql = (String) args[0];
		return Log4JdbcProxy.proxyCallableStatement(callableStatement, connectionContext, sql);
	    } else if (invoke instanceof PreparedStatement) {
		final PreparedStatement preparedStatement = (PreparedStatement) invoke;
		final String sql = (String) args[0];
		return Log4JdbcProxy.proxyPreparedStatement(preparedStatement, connectionContext, sql);
	    } else if (invoke instanceof Statement) {
		final Statement statement = (Statement) invoke;
		return Log4JdbcProxy.proxyStatement(statement, connectionContext);
	    }
	}
	return invoke;
    }

    private void setAutoCommit(final Object[] args) {
	final boolean autoCommit = ((Boolean) args[0]).booleanValue();
	setAutoCommit(autoCommit);
    }

    private void setAutoCommit(final boolean autoCommit) {

	final boolean commit = connectionOperationFactory.executeAutoCommit(autoCommit);
	connectionContext.getTransactionContext().setEnabled(!autoCommit);

	if (commit) {
	    commit();
	}
    }

    private void commit() {
	connectionContext.commit();
	connectionContext.resetTransaction();
    }

    private void setSavepoint(final Object savePoint) {
	connectionContext.setSavePoint(savePoint);
    }

    private void rollback(final Object[] args) {
	Object savePoint = null;
	if (args != null && args[0] != null) {
	    savePoint = args[0];
	}
	rollback(savePoint);
    }

    private void rollback(final Object savePoint) {
	connectionContext.rollback(savePoint);
	if (savePoint == null) {
	    connectionContext.resetTransaction();
	}
    }

    private void close() {
	connectionContext.resetContext();
    }
}
