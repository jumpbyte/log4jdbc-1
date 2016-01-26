package fr.ms.log4jdbc.proxy.jdbc.operation;

import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import fr.ms.lang.reflect.TimeInvocation;
import fr.ms.log4jdbc.SqlOperation;
import fr.ms.log4jdbc.SqlOperationContext;
import fr.ms.log4jdbc.context.jdbc.ConnectionContextJDBC;
import fr.ms.log4jdbc.context.jdbc.TransactionContextJDBC;
import fr.ms.log4jdbc.proxy.Log4JdbcProxy;
import fr.ms.log4jdbc.proxy.handler.Log4JdbcOperation;
import fr.ms.log4jdbc.proxy.jdbc.operation.factory.StatementOperationFactory;
import fr.ms.log4jdbc.resultset.ResultSetCollectorImpl;
import fr.ms.log4jdbc.sql.Query;
import fr.ms.log4jdbc.sql.QueryImpl;
import fr.ms.log4jdbc.sql.internal.QueryFactory;

public class StatementOperation implements Log4JdbcOperation {

    protected final ConnectionContextJDBC connectionContext;
    protected final TimeInvocation timeInvocation;
    protected final Method method;
    protected final Object[] args;

    private final QueryFactory queryFactory;

    protected StatementOperationFactory context;

    protected Statement statement;

    protected QueryImpl query;

    private Object invokeWrapper;

    public StatementOperation(final QueryFactory queryFactory, final StatementOperationFactory context, final Statement statement,
	    final ConnectionContextJDBC connectionContext, final TimeInvocation timeInvocation, final Method method, final Object[] args) {
	this.connectionContext = connectionContext;
	this.timeInvocation = timeInvocation;
	this.method = method;
	this.args = args;

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
	wrapInvoke();

	final SqlOperationContext sqlOperationContext = new SqlOperationContext(timeInvocation, connectionContext, query);
	return sqlOperationContext;
    }

    public void postOperation() {
	// NO-OP
    }

    private void addBatch(final String sql) {
	query = queryFactory.newQuery(connectionContext, sql);
	query.setTimeInvocation(timeInvocation);
	query.setMethodQuery(Query.METHOD_BATCH);
	query.setState(Query.STATE_NOT_EXECUTE);

	connectionContext.addQuery(query);
    }

    private void executeBatch(final Object invoke) {
	int[] updateCounts = null;

	final Class returnType = method.getReturnType();
	if (invoke != null) {
	    if (int[].class.equals(returnType)) {
		updateCounts = (int[]) invoke;
	    }
	}

	final TransactionContextJDBC transactionContext = connectionContext.getTransactionContext();
	if (transactionContext != null) {
	    transactionContext.executeBatch(updateCounts);
	}
    }

    private void execute(final String sql) {
	query = queryFactory.newQuery(connectionContext, sql);
	query.setTimeInvocation(timeInvocation);
	query.setMethodQuery(Query.METHOD_EXECUTE);
	if (connectionContext.isTransactionEnabled()) {
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

    public void wrapInvoke() {
	invokeWrapper = timeInvocation.getInvoke();

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
    }

    public Object getInvoke() {
	return invokeWrapper;
    }
}
