package fr.ms.log4jdbc.proxy.operation.factory;

import java.sql.CallableStatement;

import fr.ms.log4jdbc.context.internal.ConnectionContext;
import fr.ms.log4jdbc.sql.QueryFactory;
import fr.ms.log4jdbc.sql.QueryNamedFactory;

public class CallableStatementFactory extends
	PreparedStatementOperationFactory {

    public CallableStatementFactory(final CallableStatement statement,
	    final ConnectionContext connectionContext, final String sql) {
	super(statement, connectionContext, sql);
    }

    public QueryFactory getQueryFactory() {
	return QueryNamedFactory.getInstance();
    }
}
