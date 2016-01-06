package fr.ms.log4jdbc.proxy.jdbc.operation;

import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import fr.ms.lang.reflect.ProxyOperation;
import fr.ms.lang.reflect.TimeInvocation;
import fr.ms.log4jdbc.SqlOperation;
import fr.ms.log4jdbc.SqlOperationContext;
import fr.ms.log4jdbc.context.ConnectionContext;
import fr.ms.log4jdbc.proxy.Log4JdbcProxy;
import fr.ms.log4jdbc.proxy.jdbc.operation.factory.StatementOperationFactory;
import fr.ms.log4jdbc.resultset.ResultSetCollectorImpl;
import fr.ms.log4jdbc.sql.Query;
import fr.ms.log4jdbc.sql.QueryImpl;
import fr.ms.log4jdbc.sql.internal.QueryFactory;

public class StatementOperation implements ProxyOperation {

    protected final ConnectionContext connectionContext;
    protected final TimeInvocation timeInvocation;
    protected final Method method;
    protected final Object[] args;

    protected SqlOperationContext sqlOperation;

    private final QueryFactory queryFactory;

    protected StatementOperationFactory context;

    protected Statement statement;

    protected QueryImpl query;

    private Object invokeWrapper;

    public StatementOperation(final QueryFactory queryFactory, final StatementOperationFactory context, final Statement statement,
	    final ConnectionContext connectionContext, final TimeInvocation timeInvocation, final Method method, final Object[] args) {
	this.connectionContext = connectionContext;
	this.timeInvocation = timeInvocation;
	this.method = method;
	this.args = args;

	this.sqlOperation = new SqlOperationContext(timeInvocation, connectionContext);

	this.queryFactory = queryFactory;
	this.statement = statement;
	this.context = context;
    }

    public SqlOperation getOperation() {
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

	// Create ResultSetCollector and Proxy ResultSet
	invokeWrapper = wrapInvoke();

	return sqlOperation.valid();
    }

    private void addBatch(final String sql) {
	query = queryFactory.newQuery(connectionContext, sql);
	query.setTimeInvocation(timeInvocation);
	query.setMethodQuery(Query.METHOD_BATCH);
	query.setState(Query.STATE_NOT_EXECUTE);

	connectionContext.addQuery(query);

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
	query = queryFactory.newQuery(connectionContext, sql);
	query.setTimeInvocation(timeInvocation);
	query.setMethodQuery(Query.METHOD_EXECUTE);
	if (connectionContext.getTransaction().isEnabled()) {
	    query.setState(Query.STATE_EXECUTE);
	} else {
	    query.setState(Query.STATE_COMMIT);
	}

	final Integer updateCount = getUpdateCount(method);
	query.setUpdateCount(updateCount);

	// execute retourne true boolean - GetResultSet
	final Class returnType = method.getReturnType();
	if (Boolean.class.equals(returnType) || Boolean.TYPE.equals(returnType)) {
	    final Boolean invokeBoolean = (Boolean) timeInvocation.getInvoke();
	    if (invokeBoolean.booleanValue()) {
		query.createResultSetCollector(connectionContext);
	    }
	}

	connectionContext.addQuery(query);

	context.setQuery(query);
	sqlOperation.setQuery(query);
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
		    updateCount = new Integer(statement.getUpdateCount());
		} catch (final SQLException e) {
		    return null;
		}
	    }
	}
	return updateCount;
    }

    public Object wrapInvoke() {
	Object invokeWrapper = timeInvocation.getInvoke();

	if (invokeWrapper != null) {
	    if (invokeWrapper instanceof ResultSet) {

		final ResultSet resultSet = (ResultSet) invokeWrapper;

		QueryImpl queryCurrent = query;
		if (queryCurrent == null) {
		    queryCurrent = context.getQuery();
		}
		if (queryCurrent == null) {
		    queryCurrent = queryFactory.newQuery(connectionContext, null);
		}

		final ResultSetCollectorImpl resultSetCollector = queryCurrent.createResultSetCollector(connectionContext);

		resultSetCollector.setRs(resultSet);

		invokeWrapper = Log4JdbcProxy.proxyResultSet(resultSet, connectionContext, queryCurrent);
	    }
	}

	return invokeWrapper;
    }

    public Object getInvoke() {
	return invokeWrapper;
    }
}
