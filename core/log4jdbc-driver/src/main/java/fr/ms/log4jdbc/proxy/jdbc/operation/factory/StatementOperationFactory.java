package fr.ms.log4jdbc.proxy.jdbc.operation.factory;

import java.lang.reflect.Method;
import java.sql.Statement;

import fr.ms.lang.reflect.ProxyOperation;
import fr.ms.lang.reflect.ProxyOperationFactory;
import fr.ms.lang.reflect.TimeInvocation;
import fr.ms.log4jdbc.context.jdbc.ConnectionContextJDBC;
import fr.ms.log4jdbc.proxy.jdbc.operation.StatementOperation;
import fr.ms.log4jdbc.sql.QueryImpl;
import fr.ms.log4jdbc.sql.internal.QueryFactory;

public class StatementOperationFactory implements ProxyOperationFactory {

    protected final QueryFactory queryFactory;

    protected final ConnectionContextJDBC connectionContext;

    protected final Statement statement;

    protected QueryImpl query;

    public StatementOperationFactory(final ConnectionContextJDBC connectionContext, final Statement statement, final QueryFactory queryFactory) {
	this.connectionContext = connectionContext;
	this.statement = statement;
	this.queryFactory = queryFactory;
    }

    public ProxyOperation newOperation(final TimeInvocation timeInvocation, final Object proxy, final Method method, final Object[] args) {
	final ProxyOperation operation = new StatementOperation(queryFactory, this, statement, connectionContext, timeInvocation, method, args);

	return operation;
    }

    public void setQuery(final QueryImpl query) {
	this.query = query;
    }

    public QueryImpl getQuery() {
	return query;
    }
}
