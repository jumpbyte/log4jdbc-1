package fr.ms.log4jdbc.proxy.operation;

import java.lang.reflect.Method;
import java.sql.PreparedStatement;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import fr.ms.lang.reflect.TimeInvocation;
import fr.ms.log4jdbc.SqlOperation;
import fr.ms.log4jdbc.context.internal.ConnectionContext;
import fr.ms.log4jdbc.proxy.operation.factory.PreparedStatementOperationFactory;
import fr.ms.log4jdbc.sql.Query;
import fr.ms.log4jdbc.sql.QueryFactory;
import fr.ms.log4jdbc.sql.QueryImpl;

public class PreparedStatementOperation extends StatementOperation {

    private PreparedStatementOperationFactory factory;

    public PreparedStatementOperation(final PreparedStatementOperationFactory factory, final PreparedStatement statement, final QueryFactory queryFactory,
	    final ConnectionContext connectionContext, final TimeInvocation timeInvocation, final Object proxy, final Method method, final Object[] args) {
	super(statement, queryFactory, connectionContext, timeInvocation, proxy, method, args);
    }

    public SqlOperation newSqlOperation() {

	final String nameMethod = method.getName();

	final boolean addBatchMethod = nameMethod.equals("addBatch") && args == null;
	if (addBatchMethod) {
	    query.setMethodQuery(Query.METHOD_BATCH);
	    query.setTimeInvocation(timeInvocation);

	    connectionContext.addQuery(query, true);

	    query.execute();
	    sqlOperation.setQuery(query);

	    // Creation de la prochaine requete
	    factory.newQuery = createWrapperQuery(query);

	    return sqlOperation;
	}

	final boolean setNullMethod = nameMethod.equals("setNull") && args != null && args.length >= 1;
	if (setNullMethod) {
	    if (factory.newQuery != null) {
		this.query = factory.newQuery;
		factory.newQuery = null;
	    }
	    final Object param = args[0];
	    final Object value = null;
	    query.putParams(param, value);
	    return sqlOperation;
	}

	final boolean setMethod = nameMethod.startsWith("set") && args != null && args.length >= 2;
	if (setMethod) {
	    if (factory.newQuery != null) {
		this.query = factory.newQuery;
		factory.newQuery = null;
	    }
	    final Object param = args[0];
	    final Object value = args[1];
	    query.putParams(param, value);
	    return sqlOperation;
	}

	final boolean executeMethod = nameMethod.startsWith("execute") && !nameMethod.equals("executeBatch") && args == null;
	if (executeMethod) {

	    query.setMethodQuery(Query.METHOD_EXECUTE);
	    query.setTimeInvocation(timeInvocation);
	    final Integer updateCount = getUpdateCount(timeInvocation, method);
	    query.setUpdateCount(updateCount);

	    connectionContext.addQuery(query, false);

	    query.execute();
	    sqlOperation.setQuery(query);

	    // Creation de la prochaine requete
	    factory.newQuery = createWrapperQuery(query);

	    return sqlOperation;
	}

	return super.newSqlOperation();
    }

    private QueryImpl createWrapperQuery(final QueryImpl query) {
	final QueryImpl newQuery = queryFactory.newQuery(connectionContext, query.getJDBCQuery());

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
