package fr.ms.log4jdbc.proxy.xa.operation;

import java.lang.reflect.Method;

import javax.transaction.xa.Xid;

import fr.ms.lang.reflect.ProxyOperation;
import fr.ms.lang.reflect.TimeInvocation;
import fr.ms.log4jdbc.SqlOperation;
import fr.ms.log4jdbc.SqlOperationContext;
import fr.ms.log4jdbc.SqlOperationDefault;
import fr.ms.log4jdbc.context.ConnectionContext;
import fr.ms.log4jdbc.context.xa.Log4JdbcContextXA;
import fr.ms.log4jdbc.context.xa.XAResourceContextXA;

public class XAResourceOperation implements ProxyOperation {

    private final ConnectionContext connectionContext;
    private final XAResourceContextXA xaResourceContext;

    private final TimeInvocation timeInvocation;
    private final Object proxy;
    private final Method method;
    private final Object[] args;

    private SqlOperationDefault sqlOperationWithOutConnectionContext;
    private SqlOperationContext sqlOperationWithConnectionContext;

    public XAResourceOperation(final Log4JdbcContextXA log4JdbcContext, final TimeInvocation timeInvocation, final Object proxy, final Method method,
	    final Object[] args) {
	connectionContext = log4JdbcContext.getConnectionContext();
	xaResourceContext = log4JdbcContext.getxaResourceContext();

	this.timeInvocation = timeInvocation;
	this.proxy = proxy;
	this.method = method;
	this.args = args;

	if (connectionContext == null) {
	    this.sqlOperationWithOutConnectionContext = new SqlOperationDefault(timeInvocation);
	} else {
	    this.sqlOperationWithConnectionContext = new SqlOperationContext(timeInvocation, connectionContext);
	}
    }

    public SqlOperation getOperation() {
	final String nameMethod = method.getName();
	final Object invoke = timeInvocation.getInvoke();
	final Object exception = timeInvocation.getTargetException();

	if (exception == null) {
	    if (nameMethod.equals("start")) {
		final Xid xid = ((Xid) args[0]);
		final int flags = ((Integer) args[1]).intValue();
		xaResourceContext.start(xid, flags);
		if (connectionContext != null) {
		    connectionContext.resetTransaction();
		    connectionContext.setEnabledTransaction(true);
		}
	    } else if (nameMethod.equals("end")) {
		final Xid xid = ((Xid) args[0]);
		final int flags = ((Integer) args[1]).intValue();
		xaResourceContext.end(xid, flags);
	    } else if (nameMethod.equals("prepare")) {
		final Xid xid = ((Xid) args[0]);
		final int response = ((Integer) invoke).intValue();
		xaResourceContext.prepare(xid, response);
	    } else if (nameMethod.equals("rollback")) {
		final Xid xid = ((Xid) args[0]);
		xaResourceContext.rollback(xid);
		if (connectionContext != null) {
		    connectionContext.rollback(null);
		    connectionContext.resetTransaction();
		}
	    } else if (nameMethod.equals("commit")) {
		final Xid xid = ((Xid) args[0]);
		final boolean onePhase = ((Boolean) args[1]).booleanValue();
		final boolean commit = xaResourceContext.commit(xid, onePhase);
		if (commit && connectionContext != null) {
		    connectionContext.commit();
		    connectionContext.resetTransaction();
		}
	    }
	}

	if (connectionContext == null) {
	    return sqlOperationWithOutConnectionContext;
	} else {
	    return sqlOperationWithConnectionContext.valid();
	}
    }

    public Object getInvoke() {
	final Object invoke = timeInvocation.getInvoke();
	return invoke;
    }
}
