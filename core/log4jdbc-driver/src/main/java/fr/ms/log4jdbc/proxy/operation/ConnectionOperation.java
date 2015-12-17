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

	final Object invoke = timeInvocation.getInvoke();

	final String nameMethod = method.getName();

	boolean commitMethod = nameMethod.equals("commit");

	final boolean setAutoCommitMethod = nameMethod.equals("setAutoCommit");
	if (setAutoCommitMethod) {
	    final boolean autoCommitNew = ((Boolean) args[0]).booleanValue();

	    if (autoCommitNew && !connectionOperationFactory.isAutoCommit()) {
		commitMethod = true;
	    }

	    connectionOperationFactory.setAutoCommit(autoCommitNew);
	    connectionContext.getTransactionContext().setEnabled(!autoCommitNew);
	}

	if (commitMethod) {
	    connectionContext.commit();
	    connectionContext.resetTransaction();
	}

	final boolean rollbackMethod = nameMethod.equals("rollback");
	if (rollbackMethod) {
	    Object savePoint = null;
	    if (args != null && args[0] != null) {
		savePoint = args[0];
	    }
	    connectionContext.rollback(savePoint);
	    if (savePoint == null) {
		connectionContext.resetTransaction();
	    }
	}

	final boolean setSavepointMethod = nameMethod.equals("setSavepoint");
	if (setSavepointMethod) {
	    connectionContext.setSavePoint(invoke);
	}

	final boolean closeMethod = nameMethod.equals("close");
	if (closeMethod) {
	    connectionContext.resetContext();
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
}
