package fr.ms.log4jdbc.proxy.operation.factory;

import java.sql.CallableStatement;

import fr.ms.log4jdbc.sql.QueryFactory;
import fr.ms.log4jdbc.sql.QueryNamedFactory;

public class CallableStatementOperationFactory extends PreparedStatementOperationFactory {

    public CallableStatementOperationFactory(final CallableStatement statement, final String sql) {
	super(statement, sql);
    }

    public QueryFactory getQueryFactory() {
	return QueryNamedFactory.getInstance();
    }
}
