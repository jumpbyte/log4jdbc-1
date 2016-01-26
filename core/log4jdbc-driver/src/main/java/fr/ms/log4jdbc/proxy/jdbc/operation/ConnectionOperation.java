package fr.ms.log4jdbc.proxy.jdbc.operation;

import java.lang.reflect.Method;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.Statement;

import fr.ms.lang.reflect.TimeInvocation;
import fr.ms.log4jdbc.SqlOperation;
import fr.ms.log4jdbc.SqlOperationContext;
import fr.ms.log4jdbc.context.jdbc.ConnectionContextJDBC;
import fr.ms.log4jdbc.context.jdbc.TransactionContextJDBC;
import fr.ms.log4jdbc.proxy.Log4JdbcProxy;
import fr.ms.log4jdbc.proxy.handler.Log4JdbcOperation;
import fr.ms.log4jdbc.proxy.jdbc.operation.factory.ConnectionOperationFactory;

public class ConnectionOperation implements Log4JdbcOperation {

    private final ConnectionContextJDBC connectionContext;

    private final TimeInvocation timeInvocation;
    private final Method method;
    private final Object[] args;

    private final ConnectionOperationFactory connectionOperationFactory;

    private boolean resetTransaction;

    public ConnectionOperation(final ConnectionOperationFactory connectionOperationFactory, final ConnectionContextJDBC connectionContext,
	    final TimeInvocation timeInvocation, final Method method, final Object[] args) {
	this.connectionContext = connectionContext;
	this.timeInvocation = timeInvocation;
	this.method = method;
	this.args = args;

	this.connectionOperationFactory = connectionOperationFactory;
    }

    public SqlOperation getOperation() {
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

	final SqlOperationContext sqlOperationContext = new SqlOperationContext(timeInvocation, connectionContext);
	return sqlOperationContext;
    }

    public void postOperation() {
	if (resetTransaction) {
	    connectionContext.resetTransaction();
	    resetTransaction = false;
	}
    }

    private void setAutoCommit(final Object[] args) {
	final boolean autoCommit = ((Boolean) args[0]).booleanValue();
	final boolean commit = connectionOperationFactory.executeAutoCommit(autoCommit);

	connectionContext.setTransactionEnabled(!autoCommit);

	if (commit) {
	    commit();
	}
    }

    private void commit() {
	connectionContext.commit();
	resetTransaction = true;
    }

    private void setSavepoint(final Object savePoint) {
	final TransactionContextJDBC transactionContext = connectionContext.getTransactionContext();
	if (transactionContext != null) {
	    transactionContext.setSavePoint(savePoint);
	}
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
	resetTransaction = savePoint == null;
    }

    private void close() {
	connectionContext.close();
    }

    public Object getInvoke() {
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
}
