package fr.ms.log4jdbc.proxy.jdbc.operation;

import java.lang.reflect.Method;
import java.sql.Statement;

import fr.ms.lang.reflect.TimeInvocation;
import fr.ms.log4jdbc.SqlOperation;
import fr.ms.log4jdbc.context.jdbc.ConnectionContextJDBC;
import fr.ms.log4jdbc.proxy.jdbc.operation.factory.PreparedStatementOperationFactory;
import fr.ms.log4jdbc.sql.Query;
import fr.ms.log4jdbc.sql.QueryImpl;
import fr.ms.log4jdbc.sql.internal.QueryFactory;

public class PreparedStatementOperation extends StatementOperation {

    private final PreparedStatementOperationFactory context;

    public PreparedStatementOperation(final QueryFactory queryFactory, final PreparedStatementOperationFactory context, final Statement statement,
	    final QueryImpl query, final ConnectionContextJDBC connectionContext, final TimeInvocation timeInvocation, final Method method, final Object[] args) {
	super(queryFactory, context, statement, connectionContext, timeInvocation, method, args);
	this.context = context;
	this.query = query;
    }

    public SqlOperation getOperation() {
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

	return super.getOperation();
    }

    private void addBatch() {
	query.setTimeInvocation(timeInvocation);
	query.setMethodQuery(Query.METHOD_BATCH);
	query.setState(Query.STATE_NOT_EXECUTE);

	connectionContext.addQuery(query);

	context.newQuery();
    }

    private void setNull(final Object[] args) {
	final Object param = args[0];
	query.putParams(param, null);
    }

    private void set(final Object[] args) {
	final Object param = args[0];
	final Object value = args[1];
	query.putParams(param, value);
    }

    private void execute() {
	query.setTimeInvocation(timeInvocation);
	query.setMethodQuery(Query.METHOD_EXECUTE);
	if (connectionContext.isTransactionEnabled()) {
	    query.setState(Query.STATE_EXECUTE);
	} else {
	    query.setState(Query.STATE_COMMIT);
	}

	final Integer updateCount = getUpdateCount(method);
	query.setUpdateCount(updateCount);

	connectionContext.addQuery(query);

	context.newQuery();
    }
}
