package fr.ms.log4jdbc.proxy.handler;

import java.lang.reflect.Method;

import fr.ms.lang.reflect.TimeInvocation;

public interface Log4JdbcOperationFactory {

    Log4JdbcOperation newLog4JdbcOperation(TimeInvocation timeInvocation, Object proxy, Method method, Object[] args);
}
