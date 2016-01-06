package fr.ms.log4jdbc.proxy.xa.operation.factory;

import java.lang.reflect.Method;

import fr.ms.lang.reflect.ProxyOperation;
import fr.ms.lang.reflect.ProxyOperationFactory;
import fr.ms.lang.reflect.TimeInvocation;
import fr.ms.log4jdbc.context.ConnectionContext;
import fr.ms.log4jdbc.context.xa.Log4JdbcContextXA;
import fr.ms.log4jdbc.context.xa.XAResourceContextXA;
import fr.ms.log4jdbc.proxy.xa.operation.XAResourceOperation;

public class XAResourceOperationFactory implements ProxyOperationFactory {

    private final Log4JdbcContextXA log4JdbcContext;

    public XAResourceOperationFactory(final Log4JdbcContextXA log4JdbcContext) {
	this.log4JdbcContext = log4JdbcContext;
    }

    public ProxyOperation newOperation(final TimeInvocation timeInvocation, final Object proxy, final Method method, final Object[] args) {
	final ConnectionContext connectionContext = log4JdbcContext.getConnectionContext();
	final XAResourceContextXA xaResourceContext = log4JdbcContext.getxaResourceContext();

	final ProxyOperation operation = new XAResourceOperation(connectionContext, xaResourceContext, timeInvocation, proxy, method, args);

	return operation;
    }
}
