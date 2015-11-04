package fr.ms.log4jdbc.proxy.handler;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import fr.ms.lang.reflect.TimeInvocation;
import fr.ms.lang.reflect.TimeInvocationHandler;
import fr.ms.log4jdbc.SqlOperation;
import fr.ms.log4jdbc.SqlOperationLogger;
import fr.ms.log4jdbc.context.internal.ConnectionContext;

public class Log4JdbcInvocationHandler implements InvocationHandler {

    private final TimeInvocationHandler invocationHandler;

    private final ConnectionContext connectionContext;

    private final SqlOperationLogger[] logs;

    private final Log4JdbcOperationFactory factory;

    public Log4JdbcInvocationHandler(final Object implementation, final ConnectionContext connectionContext, final SqlOperationLogger[] logs,
	    final Log4JdbcOperationFactory factory) {
	this.invocationHandler = new TimeInvocationHandler(implementation);
	this.connectionContext = connectionContext;
	this.logs = logs;
	this.factory = factory;
    }

    public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable {
	final TimeInvocation timeInvocation = (TimeInvocation) invocationHandler.invoke(proxy, method, args);

	final Object invoke = timeInvocation.getInvoke();
	final Throwable targetException = timeInvocation.getTargetException();

	final Log4JdbcOperation operationContext = factory.newLog4JdbcOperation(connectionContext, timeInvocation, proxy, method, args);

	if (logs != null && logs.length != 0) {

	    boolean buildSqlOperation = false;
	    for (int i = 0; i < logs.length; i++) {
		final SqlOperationLogger log = logs[i];

		if (log != null && log.isEnabled()) {
		    if (!buildSqlOperation) {
			operationContext.buildOperation();
			buildSqlOperation = true;
		    }
		    try {
			final SqlOperation sqlOperation = operationContext.getSqlOperation(log);
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
	}

	if (targetException != null) {
	    throw targetException;
	}

	final Object wrapInvoke = operationContext.getResultMethod();

	return wrapInvoke;
    }
}
