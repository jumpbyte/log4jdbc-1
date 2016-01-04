package fr.ms.log4jdbc.proxy.operation.factory;

import java.lang.reflect.Method;
import java.sql.PreparedStatement;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import fr.ms.lang.reflect.TimeInvocation;
import fr.ms.log4jdbc.context.ConnectionContext;
import fr.ms.log4jdbc.proxy.handler.Log4JdbcOperation;
import fr.ms.log4jdbc.proxy.operation.PreparedStatementOperation;
import fr.ms.log4jdbc.sql.Query;
import fr.ms.log4jdbc.sql.QueryImpl;

public class PreparedStatementOperationFactory extends StatementOperationFactory {

    public PreparedStatementOperationFactory(final ConnectionContext connectionContext, final PreparedStatement statement, final String sql) {
	super(connectionContext, statement);
	query = getQueryFactory().newQuery(connectionContext, sql);
    }

    public Log4JdbcOperation newLog4JdbcOperation(final TimeInvocation timeInvocation, final Object proxy, final Method method, final Object[] args) {

	final Log4JdbcOperation operation = new PreparedStatementOperation(this, connectionContext, timeInvocation, proxy, method, args);

	return operation;
    }

    public QueryImpl addBatch(final TimeInvocation timeInvocation) {
	query.setTimeInvocation(timeInvocation);
	query.setMethodQuery(Query.METHOD_BATCH);

	connectionContext.addQuery(query);

	final QueryImpl queryCurrent = query;

	query = createWrapperQuery(queryCurrent);

	return queryCurrent;
    }

    public QueryImpl execute(final TimeInvocation timeInvocation, final Integer updateCount) {
	query.setTimeInvocation(timeInvocation);
	query.setMethodQuery(Query.METHOD_EXECUTE);
	query.setUpdateCount(updateCount);

	connectionContext.addQuery(query);

	final QueryImpl queryCurrent = query;

	query = createWrapperQuery(queryCurrent);

	return queryCurrent;
    }

    public void set(final Object param, final Object value) {
	query.putParams(param, value);
    }

    private QueryImpl createWrapperQuery(final QueryImpl query) {
	final QueryImpl newQuery = getQueryFactory().newQuery(connectionContext, query.getJDBCQuery());

	final Map jdbcParameters = query.getJDBCParameters();

	final Iterator entries = jdbcParameters.entrySet().iterator();
	while (entries.hasNext()) {
	    final Entry thisEntry = (Entry) entries.next();
	    final Object key = thisEntry.getKey();
	    final Object value = thisEntry.getValue();

	    newQuery.putParams(key, value);
	}

	return newQuery;
    }
}
