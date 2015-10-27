package fr.ms.log4jdbc.proxy.handler;

import java.lang.reflect.Method;

import fr.ms.lang.reflect.TimeInvocation;
import fr.ms.log4jdbc.SqlOperation;
import fr.ms.log4jdbc.context.internal.ConnectionContext;

public class TraceTimeInvocationOperationFactory implements Log4JdbcOperationFactory {

    private final Log4JdbcOperationFactory factory;

    public TraceTimeInvocationOperationFactory(final Log4JdbcOperationFactory factory) {
	this.factory = factory;
    }

    public Log4JdbcOperation newLog4JdbcOperation(final ConnectionContext connectionContext, final TimeInvocation timeInvocation, final Object proxy,
	    final Method method, final Object[] args) {
	final Log4JdbcOperation newLog4JdbcOperation = factory.newLog4JdbcOperation(connectionContext, timeInvocation, proxy, method, args);

	final Log4JdbcOperation decorator = new TraceTimeInvocationOperation(newLog4JdbcOperation, timeInvocation);

	return decorator;
    }

    private final class TraceTimeInvocationOperation implements Log4JdbcOperation {

	private final Log4JdbcOperation operation;

	private final TimeInvocation timeInvocation;

	public TraceTimeInvocationOperation(final Log4JdbcOperation operation, final TimeInvocation timeInvocation) {
	    this.operation = operation;
	    this.timeInvocation = timeInvocation;
	}

	public SqlOperation getSqlOperation() {
	    return operation.getSqlOperation();
	}

	public Object getResultMethod() {
	    timeInvocation.setInvoke(operation.getResultMethod());
	    return timeInvocation;
	}
    }
}
