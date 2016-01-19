package fr.ms.log4jdbc.proxy.jdbc.operation;

import java.lang.reflect.Method;

import fr.ms.lang.reflect.ProxyOperation;
import fr.ms.lang.reflect.TimeInvocation;
import fr.ms.log4jdbc.SqlOperation;
import fr.ms.log4jdbc.SqlOperationContext;
import fr.ms.log4jdbc.context.jdbc.ConnectionContextJDBC;
import fr.ms.log4jdbc.proxy.jdbc.operation.factory.ResultSetOperationFactory;
import fr.ms.log4jdbc.sql.QueryImpl;

public class ResultSetOperation implements ProxyOperation {

    private final TimeInvocation timeInvocation;
    private final Method method;
    private final Object[] args;

    private final SqlOperationContext sqlOperation;

    private final ResultSetOperationFactory context;

    public ResultSetOperation(final ResultSetOperationFactory context, final ConnectionContextJDBC connectionContext, final TimeInvocation timeInvocation,
	    final Method method, final Object[] args) {
	this.timeInvocation = timeInvocation;
	this.method = method;
	this.args = args;

	this.sqlOperation = new SqlOperationContext(timeInvocation, connectionContext);

	this.context = context;
    }

    public SqlOperation getOperation() {

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

	return sqlOperation.valid();
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

    public Object getInvoke() {
	final Object invoke = timeInvocation.getInvoke();
	return invoke;
    }
}
