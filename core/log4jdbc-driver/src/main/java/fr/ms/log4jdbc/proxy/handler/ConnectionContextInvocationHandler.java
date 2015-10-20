package fr.ms.log4jdbc.proxy.handler;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import fr.ms.lang.reflect.TimeInvocation;
import fr.ms.lang.reflect.TimeInvocationHandler;
import fr.ms.log4jdbc.SqlOperation;
import fr.ms.log4jdbc.SqlOperationLogger;
import fr.ms.log4jdbc.context.internal.ConnectionContext;

public class ConnectionContextInvocationHandler implements InvocationHandler {

    private final TimeInvocationHandler invocationHandler;

    private final ConnectionContext connectionContext;

    private final SqlOperationLogger[] logs;

    private OperationContextFactory factory;

    public ConnectionContextInvocationHandler(final Object implementation, final ConnectionContext connectionContext,
	    final SqlOperationLogger[] logs) {
	this.invocationHandler = new TimeInvocationHandler(implementation);
	this.connectionContext = connectionContext;
	this.logs = logs;
    }

    public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable {
	final TimeInvocation timeInvocation = (TimeInvocation) invocationHandler.invoke(proxy, method, args);

	final Object invoke = timeInvocation.getInvoke();
	final Throwable targetException = timeInvocation.getTargetException();

	final OperationContext operationContext = factory.newOperationContext(connectionContext, timeInvocation, proxy,
		method, args);

	if (logs != null && logs.length != 0) {
	    final SqlOperation sqlOperation = operationContext.newSqlOperation();

	    for (int i = 0; i < logs.length; i++) {
		final SqlOperationLogger log = logs[i];

		try {
		    if (targetException == null) {
			log.buildLog(sqlOperation, method, args, invoke);
		    } else {
			log.buildLog(sqlOperation, method, args, targetException);
		    }
		} catch (final Throwable t) {
		    t.printStackTrace();
		}
	    }
	}

	return operationContext.wrapInvoke();
    }
}
