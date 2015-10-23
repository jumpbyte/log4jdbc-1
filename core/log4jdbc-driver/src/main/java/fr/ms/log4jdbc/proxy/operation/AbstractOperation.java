package fr.ms.log4jdbc.proxy.operation;

import java.lang.reflect.Method;

import fr.ms.lang.reflect.TimeInvocation;
import fr.ms.log4jdbc.SqlOperation;
import fr.ms.log4jdbc.SqlOperationImpl;
import fr.ms.log4jdbc.context.internal.ConnectionContext;
import fr.ms.log4jdbc.proxy.handler.Log4JdbcOperation;

public abstract class AbstractOperation implements Log4JdbcOperation {

    protected final ConnectionContext connectionContext;
    protected final TimeInvocation timeInvocation;
    protected final Object proxy;
    protected final Method method;
    protected final Object[] args;

    protected final SqlOperationImpl sqlOperation;

    public AbstractOperation(final ConnectionContext connectionContext, final TimeInvocation timeInvocation, final Object proxy, final Method method,
	    final Object[] args) {
	this.connectionContext = connectionContext;
	this.timeInvocation = timeInvocation;
	this.proxy = proxy;
	this.method = method;
	this.args = args;

	this.sqlOperation = new SqlOperationImpl(timeInvocation, connectionContext);
    }

    public SqlOperation getSqlOperation() {
	return sqlOperation;
    }

    public Object getInvoke() {
	return timeInvocation.getInvoke();
    }
}
