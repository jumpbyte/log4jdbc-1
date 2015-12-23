package fr.ms.log4jdbc.proxy.operation;

import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import fr.ms.lang.reflect.TimeInvocation;
import fr.ms.log4jdbc.context.jdbc.ConnectionJDBCContext;
import fr.ms.log4jdbc.proxy.operation.factory.PreparedStatementOperationFactory;
import fr.ms.log4jdbc.sql.Query;
import fr.ms.log4jdbc.sql.QueryImpl;

public class PreparedStatementOperation extends StatementOperation {

    public PreparedStatementOperation(final PreparedStatementOperationFactory context, final ConnectionJDBCContext connectionContext,
	    final TimeInvocation timeInvocation, final Object proxy, final Method method, final Object[] args) {
	super(context, connectionContext, timeInvocation, proxy, method, args);
    }

    private void addBatch() {
	query.setMethodQuery(Query.METHOD_BATCH);
	query.setTimeInvocation(timeInvocation);

	connectionContext.addQuery(query, true);

	sqlOperation.setQuery(query);

	// Creation de la prochaine requete
	final QueryImpl newQuery = createWrapperQuery(query);
	context.setQuery(newQuery);
    }

    private void setNull(final Object[] args) {
	final Object param = args[0];
	set(param, null);
    }

    private void set(final Object[] args) {
	final Object param = args[0];
	final Object value = args[1];
	set(param, value);
    }

    private void set(final Object param, final Object value) {
	query.putParams(param, value);
    }

    private void execute() {
	query.setMethodQuery(Query.METHOD_EXECUTE);
	query.setTimeInvocation(timeInvocation);
	final Integer updateCount = getUpdateCount(method);
	query.setUpdateCount(updateCount);

	connectionContext.addQuery(query, false);

	sqlOperation.setQuery(query);

	// Creation de la prochaine requete
	final QueryImpl newQuery = createWrapperQuery(query);
	context.setQuery(newQuery);
    }

    public void buildSqlOperation() {
	final String nameMethod = method.getName();

	if (nameMethod.equals("addBatch") && args == null) {
	    addBatch();
	} else if (nameMethod.equals("setNull") && args != null && args.length >= 1) {
	    setNull(args);
	} else if (nameMethod.startsWith("set") && args != null && args.length >= 2) {
	    set(args);
	} else if (nameMethod.startsWith("execute") && !nameMethod.equals("executeBatch") && args == null) {
	    execute();
	}

	super.buildSqlOperation();
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
