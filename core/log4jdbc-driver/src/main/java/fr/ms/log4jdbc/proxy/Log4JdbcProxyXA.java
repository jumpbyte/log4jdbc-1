package fr.ms.log4jdbc.proxy;

import java.lang.reflect.InvocationHandler;

import javax.transaction.xa.XAResource;

import fr.ms.lang.reflect.ProxyOperationFactory;
import fr.ms.lang.reflect.ProxyUtils;
import fr.ms.log4jdbc.SqlOperationLogger;
import fr.ms.log4jdbc.context.xa.Log4JdbcContextXA;
import fr.ms.log4jdbc.proxy.xa.operation.factory.XAResourceOperationFactory;
import fr.ms.log4jdbc.utils.ServicesJDBC;

public class Log4JdbcProxyXA {
    public static XAResource proxyXAResource(final XAResource xaResource, final Log4JdbcContextXA log4JdbcContext) {
	final SqlOperationLogger[] logs = ServicesJDBC.getMessageLogger(SqlOperationLogger.XA_RESOURCE);

	final ProxyOperationFactory factory = new XAResourceOperationFactory(log4JdbcContext);

	final InvocationHandler handler = Log4JdbcProxy.createHandler(xaResource, logs, factory);

	final XAResource instance = (XAResource) ProxyUtils.newProxyInstance(xaResource, handler);

	return instance;
    }
}
