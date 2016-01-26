package fr.ms.log4jdbc.proxy.handler;

import java.lang.reflect.Method;

import fr.ms.lang.reflect.ProxyOperation;
import fr.ms.lang.reflect.ProxyOperationFactory;
import fr.ms.lang.reflect.TimeInvocation;
import fr.ms.log4jdbc.SqlOperation;

public class TraceTimeInvocationOperationFactory implements ProxyOperationFactory {

    private final ProxyOperationFactory factory;

    public TraceTimeInvocationOperationFactory(final ProxyOperationFactory factory) {
	this.factory = factory;
    }

    public ProxyOperation newOperation(final TimeInvocation timeInvocation, final Object proxy, final Method method, final Object[] args) {
	final ProxyOperation newLog4JdbcOperation = factory.newOperation(timeInvocation, proxy, method, args);

	final ProxyOperation decorator = new TraceTimeInvocationOperation((Log4JdbcOperation) newLog4JdbcOperation, timeInvocation);

	return decorator;
    }

    private final class TraceTimeInvocationOperation implements Log4JdbcOperation {

	private final Log4JdbcOperation operation;

	private final TimeInvocation timeInvocation;

	public TraceTimeInvocationOperation(final Log4JdbcOperation newLog4JdbcOperation, final TimeInvocation timeInvocation) {
	    this.operation = newLog4JdbcOperation;
	    this.timeInvocation = timeInvocation;
	}

	public SqlOperation getOperation() {
	    return operation.getOperation();
	}

	public void postOperation() {
	    operation.postOperation();
	}

	public Object getInvoke() {
	    timeInvocation.setInvoke(operation.getInvoke());
	    return timeInvocation;
	}
    }
}
