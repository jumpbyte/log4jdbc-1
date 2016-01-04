package fr.ms.log4jdbc.proxy.operation;

import java.lang.reflect.Method;

import fr.ms.lang.reflect.TimeInvocation;
import fr.ms.log4jdbc.context.ConnectionContext;
import fr.ms.log4jdbc.proxy.operation.factory.PreparedStatementOperationFactory;
import fr.ms.log4jdbc.sql.QueryImpl;

public class PreparedStatementOperation extends StatementOperation {

    private final PreparedStatementOperationFactory context;

    public PreparedStatementOperation(final PreparedStatementOperationFactory context, final ConnectionContext connectionContext,
	    final TimeInvocation timeInvocation, final Object proxy, final Method method, final Object[] args) {
	super(context, connectionContext, timeInvocation, proxy, method, args);
	this.context = context;
    }

    private void addBatch() {
	final QueryImpl query = context.addBatch(timeInvocation);

	sqlOperation.setQuery(query);
    }

    private void setNull(final Object[] args) {
	final Object param = args[0];
	context.set(param, null);
    }

    private void set(final Object[] args) {
	final Object param = args[0];
	final Object value = args[1];
	context.set(param, value);
    }

    private void execute() {
	final Integer updateCount = getUpdateCount(method);

	final QueryImpl query = context.execute(timeInvocation, updateCount);

	sqlOperation.setQuery(query);
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
}
