package fr.ms.log4jdbc.proxy.operation;

import java.lang.reflect.Method;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.Statement;

import fr.ms.lang.reflect.TimeInvocation;
import fr.ms.log4jdbc.SqlOperation;
import fr.ms.log4jdbc.SqlOperationImpl;
import fr.ms.log4jdbc.context.internal.ConnectionContext;
import fr.ms.log4jdbc.proxy.Log4JdbcProxy;
import fr.ms.log4jdbc.proxy.handler.Log4JdbcOperation;

public class ConnectionOperation implements Log4JdbcOperation {

    private final ConnectionContext connectionContext;
    private final TimeInvocation timeInvocation;
    private final Object proxy;
    private final Method method;
    private final Object[] args;

    private final SqlOperationImpl sqlOperation;

    public ConnectionOperation(final ConnectionContext connectionContext, final TimeInvocation timeInvocation, final Object proxy, final Method method,
	    final Object[] args) {
	this.connectionContext = connectionContext;
	this.timeInvocation = timeInvocation;
	this.proxy = proxy;
	this.method = method;
	this.args = args;

	sqlOperation = new SqlOperationImpl(timeInvocation, connectionContext);
    }

    public SqlOperation newSqlOperation() {

	final Object invoke = timeInvocation.getInvoke();

	final String nameMethod = method.getName();

	boolean commitMethod = nameMethod.equals("commit");

	final boolean setAutoCommitMethod = nameMethod.equals("setAutoCommit");
	if (setAutoCommitMethod) {
	    final Boolean autoCommit = (Boolean) args[0];
	    final boolean etatActuel = connectionContext.isAutoCommit();
	    connectionContext.setAutoCommit(autoCommit.booleanValue());

	    if (etatActuel == false && connectionContext.isAutoCommit()) {
		commitMethod = true;
	    }
	}

	if (commitMethod) {
	    connectionContext.getTransactionContext().commit();
	    connectionContext.resetTransaction();
	}

	final boolean rollbackMethod = nameMethod.equals("rollback");
	if (rollbackMethod) {
	    connectionContext.getTransactionContext().rollback(invoke);
	    if (invoke == null) {
		connectionContext.resetTransaction();
	    }
	}

	final boolean setSavepointMethod = nameMethod.equals("setSavepoint");
	if (setSavepointMethod) {
	    connectionContext.getTransactionContext().setSavePoint(invoke);
	}

	final boolean closeMethod = nameMethod.equals("close");
	if (closeMethod) {
	    connectionContext.getOpenConnection().decrementAndGet();
	}

	return sqlOperation;
    }

    public Object wrapInvoke() {
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
