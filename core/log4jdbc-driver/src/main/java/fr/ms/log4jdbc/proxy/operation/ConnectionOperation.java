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
	    connectionOperationFactory.executeAutoCommit(args);
	} else if (nameMethod.equals("commit")) {
	    connectionOperationFactory.executeCommit();
	} else if (nameMethod.equals("rollback")) {
	    connectionOperationFactory.executeRollback(args);
	} else if (nameMethod.equals("setSavepoint")) {
	    connectionOperationFactory.executeSavePoint(timeInvocation.getInvoke());
	} else if (nameMethod.equals("close")) {
	    connectionOperationFactory.executeClose();
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
