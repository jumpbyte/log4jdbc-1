package fr.ms.log4jdbc.proxy.operation;

import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.sql.SQLException;

import fr.ms.lang.reflect.TimeInvocation;
import fr.ms.log4jdbc.context.internal.ConnectionContext;
import fr.ms.log4jdbc.proxy.Log4JdbcProxy;
import fr.ms.log4jdbc.proxy.operation.factory.StatementOperationFactory;
import fr.ms.log4jdbc.sql.Query;
import fr.ms.log4jdbc.sql.QueryImpl;
import fr.ms.log4jdbc.sql.internal.QueryFactory;

public class StatementOperation extends AbstractOperation {

    protected StatementOperationFactory context;

    protected QueryFactory queryFactory;

    protected QueryImpl query;

    public StatementOperation(final StatementOperationFactory context, final ConnectionContext connectionContext, final TimeInvocation timeInvocation,
	    final Object proxy, final Method method, final Object[] args) {
	super(connectionContext, timeInvocation, proxy, method, args);
	this.context = context;
	this.queryFactory = context.getQueryFactory();
    }

    public void init() {
	query = context.getQuery();
    }

    public void buildSqlOperation() {
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
	    return;
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

	    connectionContext.getTransactionContext().executeBatch(updateCounts);
	    return;
	}

	final boolean executeMethod = nameMethod.startsWith("execute") && args != null && args.length >= 1;
	if (executeMethod) {
	    final String sql = (String) args[0];

	    query = queryFactory.newQuery(connectionContext, sql);
	    query.setMethodQuery(Query.METHOD_EXECUTE);
	    query.setTimeInvocation(timeInvocation);
	    final Integer updateCount = getUpdateCount(method);
	    query.setUpdateCount(updateCount);

	    connectionContext.addQuery(query, false);

	    query.execute();
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
	return;
    }

    public Object buildResultMethod() {
	final Object invoke = timeInvocation.getInvoke();

	if (invoke != null) {
	    if (invoke instanceof ResultSet) {

		final ResultSet resultSet = (ResultSet) invoke;

		if (query == null) {
		    query = queryFactory.newQuery(connectionContext, null);
		    query.setMethodQuery(Query.STATE_EXECUTE);
		    query.setTimeInvocation(timeInvocation);

		    connectionContext.addQuery(query, true);

		    query.execute();

		}

		query.initResultSetCollector(connectionContext, resultSet);

		return Log4JdbcProxy.proxyResultSet(resultSet, connectionContext, query);
	    }
	}
	return invoke;
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
}
