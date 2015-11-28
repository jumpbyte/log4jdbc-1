package fr.ms.log4jdbc.proxy.operation.factory;

import java.lang.reflect.Method;
import java.sql.Statement;

import fr.ms.lang.reflect.TimeInvocation;
import fr.ms.log4jdbc.context.internal.ConnectionContext;
import fr.ms.log4jdbc.proxy.handler.Log4JdbcOperation;
import fr.ms.log4jdbc.proxy.handler.Log4JdbcOperationFactory;
import fr.ms.log4jdbc.proxy.operation.StatementOperation;
import fr.ms.log4jdbc.sql.QueryImpl;
import fr.ms.log4jdbc.sql.internal.QueryFactory;
import fr.ms.log4jdbc.sql.internal.QuerySQLFactory;

public class StatementOperationFactory implements Log4JdbcOperationFactory {

    private final Statement statement;

    protected QueryImpl query;

    public StatementOperationFactory(final Statement statement) {
	this.statement = statement;
    }

    public Log4JdbcOperation newLog4JdbcOperation(
	    final ConnectionContext connectionContext,
	    final TimeInvocation timeInvocation, final Object proxy,
	    final Method method, final Object[] args) {

	final Log4JdbcOperation operation = new StatementOperation(this,
		connectionContext, timeInvocation, proxy, method, args);

	return operation;
    }

    public QueryImpl getQuery() {
	return query;
    }

    public void setQuery(final QueryImpl query) {
	this.query = query;
    }

    public Statement getStatement() {
	return statement;
    }

    public QueryFactory getQueryFactory() {
	return QuerySQLFactory.getInstance();
    }
}
