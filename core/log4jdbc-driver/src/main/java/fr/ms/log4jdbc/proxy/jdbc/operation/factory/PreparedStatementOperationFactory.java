package fr.ms.log4jdbc.proxy.jdbc.operation.factory;

import java.lang.reflect.Method;
import java.sql.PreparedStatement;

import fr.ms.lang.reflect.ProxyOperation;
import fr.ms.lang.reflect.TimeInvocation;
import fr.ms.log4jdbc.context.jdbc.ConnectionContextJDBC;
import fr.ms.log4jdbc.proxy.jdbc.operation.PreparedStatementOperation;
import fr.ms.log4jdbc.sql.internal.QueryFactory;

public class PreparedStatementOperationFactory extends StatementOperationFactory {

    private final String sql;

    public PreparedStatementOperationFactory(final ConnectionContextJDBC connectionContext, final PreparedStatement statement, final QueryFactory queryFactory,
	    final String sql) {
	super(connectionContext, statement, queryFactory);
	this.sql = sql;
	query = queryFactory.newQuery(connectionContext, sql);
    }

    public ProxyOperation newOperation(final TimeInvocation timeInvocation, final Object proxy, final Method method, final Object[] args) {
	final ProxyOperation operation = new PreparedStatementOperation(queryFactory, this, statement, query, connectionContext, timeInvocation, method, args);

	return operation;
    }

    public void newQuery() {
	query = queryFactory.newQuery(connectionContext, sql, query.getJDBCParameters());
    }
}
