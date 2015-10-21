package fr.ms.log4jdbc.proxy.operation.factory;

import java.lang.reflect.Method;
import java.sql.PreparedStatement;

import fr.ms.lang.reflect.TimeInvocation;
import fr.ms.log4jdbc.context.internal.ConnectionContext;
import fr.ms.log4jdbc.proxy.handler.Log4JdbcOperation;
import fr.ms.log4jdbc.proxy.handler.Log4JdbcOperationFactory;
import fr.ms.log4jdbc.proxy.operation.PreparedStatementOperation;
import fr.ms.log4jdbc.sql.QueryFactory;
import fr.ms.log4jdbc.sql.QueryImpl;
import fr.ms.log4jdbc.sql.QuerySQLFactory;

public class PreparedStatementOperationFactory implements Log4JdbcOperationFactory {

    private final PreparedStatement statement;

    public QueryImpl newQuery;

    public PreparedStatementOperationFactory(final PreparedStatement statement, final String sql) {
	this.statement = statement;
    }

    public Log4JdbcOperation newLog4JdbcOperation(final ConnectionContext connectionContext, final TimeInvocation timeInvocation, final Object proxy,
	    final Method method, final Object[] args) {

	final QueryFactory queryFactory = getQueryFactory();

	final Log4JdbcOperation operation = new PreparedStatementOperation(this, statement, queryFactory, connectionContext, timeInvocation, proxy, method,
		args);

	return operation;
    }

    public QueryFactory getQueryFactory() {
	return QuerySQLFactory.getInstance();
    }
}
