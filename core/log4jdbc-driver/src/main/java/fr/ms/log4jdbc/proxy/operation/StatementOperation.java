package fr.ms.log4jdbc.proxy.operation;

import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import fr.ms.lang.reflect.TimeInvocation;
import fr.ms.log4jdbc.SqlOperation;
import fr.ms.log4jdbc.SqlOperationImpl;
import fr.ms.log4jdbc.context.internal.ConnectionContext;
import fr.ms.log4jdbc.proxy.Log4JdbcProxy;
import fr.ms.log4jdbc.proxy.handler.Log4JdbcOperation;
import fr.ms.log4jdbc.sql.Query;
import fr.ms.log4jdbc.sql.QueryFactory;
import fr.ms.log4jdbc.sql.QueryImpl;

public class StatementOperation implements Log4JdbcOperation {

    private final Statement statement;
    protected final QueryFactory queryFactory;

    protected final ConnectionContext connectionContext;
    protected final TimeInvocation timeInvocation;
    private final Object proxy;
    protected final Method method;
    protected final Object[] args;

    protected final SqlOperationImpl sqlOperation;

    protected QueryImpl query;

    public StatementOperation(final Statement statement, final QueryFactory queryFactory, final ConnectionContext connectionContext,
	    final TimeInvocation timeInvocation, final Object proxy, final Method method, final Object[] args) {
	this.statement = statement;
	this.queryFactory = queryFactory;

	this.connectionContext = connectionContext;
	this.timeInvocation = timeInvocation;
	this.proxy = proxy;
	this.method = method;
	this.args = args;

	sqlOperation = new SqlOperationImpl(timeInvocation, connectionContext);
    }

    public SqlOperation newSqlOperation() {

	final String nameMethod = method.getName();

	final boolean addBatchMethod = nameMethod.equals("addBatch") && args != null && args.length >= 1;
	if (addBatchMethod) {
	    final String sql = (String) args[0];

	    query = queryFactory.newQuery(connectionContext, sql);
	    query.setMethodQuery(Query.METHOD_BATCH);
	    query.setTimeInvocation(timeInvocation);

	    connectionContext.addQuery(query, true);

	    query.execute();
	    sqlOperation.setQuery(query);

	    return sqlOperation;
	}

	final boolean executeBatchMethod = nameMethod.equals("executeBatch") && args == null;
	if (executeBatchMethod) {
	    final Object invoke = timeInvocation.getInvoke();
	    int[] updateCounts = null;

	    final Class returnType = method.getReturnType();
	    if (invoke != null) {
		if (int[].class.equals(returnType)) {
		    updateCounts = (int[]) invoke;
		}
	    }

	    connectionContext.getBatchContext().executeBatch(updateCounts);
	    connectionContext.resetBatch();
	    return sqlOperation;
	}

	final boolean executeMethod = nameMethod.startsWith("execute") && args != null && args.length >= 1;
	if (executeMethod) {
	    final String sql = (String) args[0];

	    query = queryFactory.newQuery(connectionContext, sql);
	    query.setMethodQuery(Query.METHOD_EXECUTE);
	    query.setTimeInvocation(timeInvocation);
	    final Integer updateCount = getUpdateCount(timeInvocation, method);
	    query.setUpdateCount(updateCount);

	    connectionContext.addQuery(query, false);

	    query.execute();
	    sqlOperation.setQuery(query);

	    // execute retourne true boolean - GetResultSet
	    final Class returnType = method.getReturnType();
	    if (Boolean.class.equals(returnType) || Boolean.TYPE.equals(returnType)) {
		final Boolean invokeBoolean = (Boolean) timeInvocation.getInvoke();
		if (invokeBoolean.booleanValue()) {
		    query.initResultSetCollector(connectionContext);
		    this.query = query;
		}
	    }

	    return sqlOperation;
	}

	return sqlOperation;
    }

    public Object wrapInvoke() {

	final Object invoke = timeInvocation.getInvoke();

	if (invoke != null) {
	    if (invoke instanceof ResultSet) {

		final ResultSet resultSet = (ResultSet) invoke;

		if (query == null) {
		    final QueryImpl wrapperQuery = queryFactory.newQuery(connectionContext, null);
		    wrapperQuery.execute();
		    wrapperQuery.initResultSetCollector(connectionContext, resultSet);

		    query = wrapperQuery;
		}

		return Log4JdbcProxy.proxyResultSet(resultSet, connectionContext, query);
	    }
	}
	return invoke;
    }

    protected Integer getUpdateCount(final TimeInvocation timeInvocation, final Method method) {
	Integer updateCount = null;
	final Object invoke = timeInvocation.getInvoke();
	final Class returnType = method.getReturnType();
	if (Integer.class.equals(returnType) || Integer.TYPE.equals(returnType)) {
	    updateCount = (Integer) invoke;
	} else if (Boolean.class.equals(returnType) || Boolean.TYPE.equals(returnType)) {
	    final Boolean invokeBoolean = (Boolean) invoke;
	    if (timeInvocation.getTargetException() == null && !invokeBoolean.booleanValue()) {
		try {
		    updateCount = new Integer(statement.getUpdateCount());
		} catch (final SQLException e) {
		    return null;
		}
	    }
	}
	return updateCount;
    }
}
