package fr.ms.log4jdbc.proxy.operation.factory;

import java.lang.reflect.Method;
import java.sql.PreparedStatement;

import fr.ms.lang.reflect.TimeInvocation;
import fr.ms.log4jdbc.context.internal.ConnectionContext;
import fr.ms.log4jdbc.proxy.handler.Log4JdbcOperation;
import fr.ms.log4jdbc.proxy.operation.PreparedStatementOperation;

public class PreparedStatementOperationFactory extends StatementOperationFactory {

    public PreparedStatementOperationFactory(final ConnectionContext connectionContext, final PreparedStatement statement, final String sql) {
	super(connectionContext, statement);
	query = getQueryFactory().newQuery(connectionContext, sql);
    }

    public Log4JdbcOperation newLog4JdbcOperation(final TimeInvocation timeInvocation, final Object proxy, final Method method, final Object[] args) {

	final Log4JdbcOperation operation = new PreparedStatementOperation(this, connectionContext, timeInvocation, proxy, method, args);

	return operation;
    }
}
