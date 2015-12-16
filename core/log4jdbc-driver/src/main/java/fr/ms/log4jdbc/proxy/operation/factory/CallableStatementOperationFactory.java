package fr.ms.log4jdbc.proxy.operation.factory;

import java.sql.CallableStatement;

import fr.ms.log4jdbc.context.internal.ConnectionContext;
import fr.ms.log4jdbc.sql.internal.QueryFactory;
import fr.ms.log4jdbc.sql.internal.QueryNamedFactory;

public class CallableStatementOperationFactory extends PreparedStatementOperationFactory {

    public CallableStatementOperationFactory(final ConnectionContext connectionContext, final CallableStatement statement, final String sql) {
	super(connectionContext, statement, sql);
    }

    public QueryFactory getQueryFactory() {
	return QueryNamedFactory.getInstance();
    }
}
