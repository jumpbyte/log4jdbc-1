package fr.ms.log4jdbc.proxy.operation;

import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.sql.SQLException;

import fr.ms.lang.reflect.TimeInvocation;
import fr.ms.log4jdbc.context.ConnectionContext;
import fr.ms.log4jdbc.proxy.Log4JdbcProxy;
import fr.ms.log4jdbc.proxy.operation.factory.StatementOperationFactory;
import fr.ms.log4jdbc.sql.QueryImpl;

public class StatementOperation extends AbstractOperation {

    protected StatementOperationFactory context;

    public StatementOperation(final StatementOperationFactory context, final ConnectionContext connectionContext, final TimeInvocation timeInvocation,
	    final Object proxy, final Method method, final Object[] args) {
	super(connectionContext, timeInvocation, proxy, method, args);
	this.context = context;
    }

    public void buildSqlOperation() {
	final String nameMethod = method.getName();

	if (nameMethod.equals("addBatch") && args != null && args.length >= 1) {
	    final String sql = (String) args[0];
	    addBatch(sql);
	} else if (nameMethod.equals("executeBatch") && args == null) {
	    executeBatch(timeInvocation.getInvoke());
	} else if (nameMethod.startsWith("execute") && args != null && args.length >= 1) {
	    final String sql = (String) args[0];
	    execute(sql);
	}
    }

    private void addBatch(final String sql) {
	final QueryImpl query = context.addBatch(sql, timeInvocation);

	sqlOperation.setQuery(query);
    }

    private void executeBatch(final Object invoke) {
	int[] updateCounts = null;

	final Class returnType = method.getReturnType();
	if (invoke != null) {
	    if (int[].class.equals(returnType)) {
		updateCounts = (int[]) invoke;
	    }
	}

	connectionContext.executeBatch(updateCounts);
    }

    private void execute(final String sql) {
	final Integer updateCount = getUpdateCount(method);
	final QueryImpl query = context.execute(sql, timeInvocation, updateCount);

	sqlOperation.setQuery(query);

	// execute retourne true boolean - GetResultSet
	final Class returnType = method.getReturnType();
	if (Boolean.class.equals(returnType) || Boolean.TYPE.equals(returnType)) {
	    final Boolean invokeBoolean = (Boolean) timeInvocation.getInvoke();
	    if (invokeBoolean != null && invokeBoolean.booleanValue()) {
		query.initResultSetCollector(connectionContext);
		context.setQuery(query);
	    }
	}
    }

    protected Integer getUpdateCount(final Method method) {
	Integer updateCount = null;
	final Object invoke = timeInvocation.getInvoke();
	final Class returnType = method.getReturnType();
	if (Integer.class.equals(returnType) || Integer.TYPE.equals(returnType)) {
	    updateCount = (Integer) invoke;
	} else if (Boolean.class.equals(returnType) || Boolean.TYPE.equals(returnType)) {
	    final Boolean invokeBoolean = (Boolean) invoke;
	    if (timeInvocation.getTargetException() == null && !invokeBoolean.booleanValue()) {
		try {
		    updateCount = new Integer(context.getStatement().getUpdateCount());
		} catch (final SQLException e) {
		    return null;
		}
	    }
	}
	return updateCount;
    }

    public Object buildResultMethod() {
	final Object invoke = timeInvocation.getInvoke();

	if (invoke != null) {
	    if (invoke instanceof ResultSet) {

		final ResultSet resultSet = (ResultSet) invoke;

		QueryImpl query = context.getQuery();
		if (query == null) {
		    query = context.execute(null, null, null);
		}

		query.initResultSetCollector(connectionContext, resultSet);

		return Log4JdbcProxy.proxyResultSet(resultSet, connectionContext, query);
	    }
	}
	return invoke;
    }
}
