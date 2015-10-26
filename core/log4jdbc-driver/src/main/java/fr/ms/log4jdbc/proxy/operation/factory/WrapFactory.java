package fr.ms.log4jdbc.proxy.operation.factory;

import java.lang.reflect.Method;

import fr.ms.lang.reflect.TimeInvocation;
import fr.ms.log4jdbc.SqlOperation;
import fr.ms.log4jdbc.context.internal.ConnectionContext;
import fr.ms.log4jdbc.proxy.handler.Log4JdbcOperation;
import fr.ms.log4jdbc.proxy.handler.Log4JdbcOperationFactory;

public class WrapFactory implements Log4JdbcOperationFactory {

    private final Log4JdbcOperationFactory wrap;

    public WrapFactory(final Log4JdbcOperationFactory wrap) {
	this.wrap = wrap;
    }

    public Log4JdbcOperation newLog4JdbcOperation(final ConnectionContext connectionContext, final TimeInvocation timeInvocation, final Object proxy,
	    final Method method, final Object[] args) {
	final Log4JdbcOperation newLog4JdbcOperation = wrap.newLog4JdbcOperation(connectionContext, timeInvocation, proxy, method, args);

	final WrapLog4JdbcOperation wrap = new WrapLog4JdbcOperation(newLog4JdbcOperation, timeInvocation);

	return wrap;
    }

    private final class WrapLog4JdbcOperation implements Log4JdbcOperation {

	private final Log4JdbcOperation wrap;

	private final TimeInvocation timeInvocation;

	public WrapLog4JdbcOperation(final Log4JdbcOperation wrap, final TimeInvocation timeInvocation) {
	    this.wrap = wrap;
	    this.timeInvocation = timeInvocation;
	}

	public SqlOperation getSqlOperation() {
	    return wrap.getSqlOperation();
	}

	public Object getResultMethod() {
	    timeInvocation.setInvoke(wrap.getResultMethod());
	    return timeInvocation;
	}
    }
}
