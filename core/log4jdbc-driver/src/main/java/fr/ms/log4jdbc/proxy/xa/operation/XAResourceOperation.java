package fr.ms.log4jdbc.proxy.xa.operation;

import java.lang.reflect.Method;

import fr.ms.lang.reflect.ProxyOperation;
import fr.ms.lang.reflect.TimeInvocation;
import fr.ms.log4jdbc.SqlOperation;
import fr.ms.log4jdbc.SqlOperationContext;
import fr.ms.log4jdbc.context.ConnectionContext;
import fr.ms.log4jdbc.context.xa.XAResourceContextXA;

public class XAResourceOperation implements ProxyOperation {

    private final ConnectionContext connectionContext;
    private final XAResourceContextXA xaResourceContext;

    private final TimeInvocation timeInvocation;
    private final Object proxy;
    private final Method method;
    private final Object[] args;

    private final SqlOperationContext sqlOperation;

    public XAResourceOperation(final ConnectionContext connectionContext, final XAResourceContextXA xaResourceContext, final TimeInvocation timeInvocation,
	    final Object proxy, final Method method, final Object[] args) {
	this.connectionContext = connectionContext;
	this.xaResourceContext = xaResourceContext;

	this.timeInvocation = timeInvocation;
	this.proxy = proxy;
	this.method = method;
	this.args = args;

	this.sqlOperation = new SqlOperationContext(timeInvocation, connectionContext);
    }

    public SqlOperation getOperation() {
	final String declaringClass = method.getDeclaringClass().getName();
	final String name = method.getName();

	System.out.println(declaringClass + "." + name);

	return sqlOperation.valid();
    }

    public Object getInvoke() {
	final Object invoke = timeInvocation.getInvoke();
	return invoke;
    }
}
