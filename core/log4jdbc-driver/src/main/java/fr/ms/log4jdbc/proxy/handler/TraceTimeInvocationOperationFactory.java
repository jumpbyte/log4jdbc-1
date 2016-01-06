package fr.ms.log4jdbc.proxy.handler;

import java.lang.reflect.Method;

import fr.ms.lang.reflect.ProxyOperation;
import fr.ms.lang.reflect.ProxyOperationFactory;
import fr.ms.lang.reflect.TimeInvocation;

public class TraceTimeInvocationOperationFactory implements ProxyOperationFactory {

    private final ProxyOperationFactory factory;

    public TraceTimeInvocationOperationFactory(final ProxyOperationFactory factory) {
	this.factory = factory;
    }

    public ProxyOperation newOperation(final TimeInvocation timeInvocation, final Object proxy, final Method method, final Object[] args) {
	final ProxyOperation newLog4JdbcOperation = factory.newOperation(timeInvocation, proxy, method, args);

	final ProxyOperation decorator = new TraceTimeInvocationOperation(newLog4JdbcOperation, timeInvocation);

	return decorator;
    }

    private final class TraceTimeInvocationOperation implements ProxyOperation {

	private final ProxyOperation operation;

	private final TimeInvocation timeInvocation;

	public TraceTimeInvocationOperation(final ProxyOperation operation, final TimeInvocation timeInvocation) {
	    this.operation = operation;
	    this.timeInvocation = timeInvocation;
	}

	public Object getOperation() {
	    return operation.getOperation();
	}

	public Object getInvoke() {
	    timeInvocation.setInvoke(operation.getInvoke());
	    return timeInvocation;
	}
    }
}
