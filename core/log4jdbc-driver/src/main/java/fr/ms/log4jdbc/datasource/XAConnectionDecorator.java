package fr.ms.log4jdbc.datasource;

import javax.transaction.xa.XAResource;

import fr.ms.lang.reflect.ImplementationDecorator;
import fr.ms.lang.reflect.ImplementationDecorator.ImplementationProxy;
import fr.ms.log4jdbc.context.xa.Log4JdbcContextXA;
import fr.ms.log4jdbc.proxy.Log4JdbcProxyXA;

public class XAConnectionDecorator extends ConnectionDecorator {

    private final Log4JdbcContextXA log4JdbcContext;

    protected XAConnectionDecorator(final Log4JdbcContextXA log4JdbcContext, final Object sourceImpl) {
	super(log4JdbcContext, sourceImpl);
	this.log4JdbcContext = log4JdbcContext;
    }

    public static Object proxyConnection(final Log4JdbcContextXA log4JdbcContext, final Object impl, final Object sourceImpl) {
	final ImplementationProxy ip = new XAConnectionDecorator(log4JdbcContext, sourceImpl);

	return proxyConnection(ip, impl, sourceImpl);
    }

    @Override
    public Object createProxy(final ImplementationDecorator origine, Object invoke) {
	invoke = super.createProxy(origine, invoke);
	if (invoke instanceof XAResource) {

	    final XAResource xaResource = (XAResource) invoke;

	    final XAResource wrapObject = Log4JdbcProxyXA.proxyXAResource(xaResource, log4JdbcContext);

	    return wrapObject;
	}
	return invoke;
    }
}
