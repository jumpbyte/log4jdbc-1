package fr.ms.log4jdbc.proxy.handler;

import java.lang.reflect.Method;

import fr.ms.lang.reflect.TimeInvocation;
import fr.ms.log4jdbc.context.internal.ConnectionContext;

public interface Log4JdbcOperationFactory {

    Log4JdbcOperation newLog4JdbcOperation(ConnectionContext connectionContext,
	    TimeInvocation timeInvocation, Object proxy, Method method,
	    Object[] args);
}
