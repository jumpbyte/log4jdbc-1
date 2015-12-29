package fr.ms.log4jdbc.proxy.operation;

import java.lang.reflect.Method;

import fr.ms.lang.reflect.TimeInvocation;
import fr.ms.log4jdbc.context.jdbc.ConnectionJDBCContext;
import fr.ms.log4jdbc.proxy.operation.factory.ResultSetOperationFactory;
import fr.ms.log4jdbc.sql.QueryImpl;

public class ResultSetOperation extends AbstractOperation {

    private final ResultSetOperationFactory context;

    public ResultSetOperation(final ResultSetOperationFactory context, final ConnectionJDBCContext connectionContext, final TimeInvocation timeInvocation,
	    final Object proxy, final Method method, final Object[] args) {
	super(connectionContext, timeInvocation, proxy, method, args);
	this.context = context;
    }

    private void next(final boolean valid) {
	final QueryImpl query = context.next(valid);

	sqlOperation.setQuery(query);
    }

    private void previous(final boolean valid) {
	final QueryImpl query = context.previous(valid);

	sqlOperation.setQuery(query);
    }

    private void first(final boolean valid) {
	final QueryImpl query = context.first(valid);

	sqlOperation.setQuery(query);
    }

    private void last(final boolean valid) {
	final QueryImpl query = context.last(valid);

	sqlOperation.setQuery(query);
    }

    private void close() {
	final QueryImpl query = context.close();
	sqlOperation.setQuery(query);
    }

    private void get(final Object invoke) {
	final Class clazz = method.getParameterTypes()[0];

	context.addValueColumn(clazz, args, invoke);
    }

    public void buildSqlOperation() {

	final Object invoke = timeInvocation.getInvoke();
	boolean valid = timeInvocation.getTargetException() == null;
	final String nameMethod = method.getName();

	if (nameMethod.equals("next") && invoke != null) {
	    valid = valid && ((Boolean) invoke).booleanValue();
	    next(valid);
	} else if (nameMethod.equals("previous") && invoke != null) {
	    valid = valid && ((Boolean) invoke).booleanValue();
	    previous(valid);
	} else if (nameMethod.equals("first") && invoke != null) {
	    valid = valid && ((Boolean) invoke).booleanValue();
	    first(valid);
	} else if (nameMethod.equals("last") && invoke != null) {
	    valid = valid && ((Boolean) invoke).booleanValue();
	    last(valid);
	} else if (nameMethod.equals("beforeFirst")) {
	    context.beforeFirst();
	} else if (nameMethod.equals("afterLast")) {
	    context.afterLast();
	} else if (nameMethod.equals("wasNull") && context.lastCell != null && invoke != null && ((Boolean) invoke).booleanValue()) {
	    context.wasNull();
	} else if (nameMethod.startsWith("getMetaData") && invoke != null) {
	    context.getMetaData(invoke);
	} else if (nameMethod.startsWith("close")) {
	    close();
	} else if (nameMethod.startsWith("get") && valid && args != null && args.length > 0) {
	    get(invoke);
	}
    }
}
