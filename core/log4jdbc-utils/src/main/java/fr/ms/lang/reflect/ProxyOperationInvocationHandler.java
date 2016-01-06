package fr.ms.lang.reflect;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class ProxyOperationInvocationHandler implements InvocationHandler {

    private final TimeInvocationHandler invocationHandler;

    private final ProxyOperationFactory factory;

    public ProxyOperationInvocationHandler(final Object implementation, final ProxyOperationFactory factory) {
	this.invocationHandler = new TimeInvocationHandler(implementation);
	this.factory = factory;
    }

    public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable {
	final TimeInvocation timeInvocation = (TimeInvocation) invocationHandler.invoke(proxy, method, args);

	final Throwable targetException = timeInvocation.getTargetException();

	final ProxyOperation operationContext = factory.newOperation(timeInvocation, proxy, method, args);

	final boolean buildOperation = preProcess();
	if (buildOperation) {
	    postProcess(operationContext, timeInvocation, proxy, method, args);
	}

	if (targetException != null) {
	    throw targetException;
	}

	final Object wrapInvoke = operationContext.getInvoke();

	return wrapInvoke;
    }

    public boolean preProcess() {
	return false;
    }

    public void postProcess(final ProxyOperation operationContext, final TimeInvocation timeInvocation, final Object proxy, final Method method,
	    final Object[] args) {
	// NOOP
    }
}
