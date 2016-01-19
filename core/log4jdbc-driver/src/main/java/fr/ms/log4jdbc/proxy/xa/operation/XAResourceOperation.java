package fr.ms.log4jdbc.proxy.xa.operation;

import java.lang.reflect.Method;
import java.util.WeakHashMap;

import javax.transaction.xa.XAResource;
import javax.transaction.xa.Xid;

import fr.ms.lang.reflect.ProxyOperation;
import fr.ms.lang.reflect.TimeInvocation;
import fr.ms.log4jdbc.SqlOperation;
import fr.ms.log4jdbc.SqlOperationContext;
import fr.ms.log4jdbc.SqlOperationDefault;
import fr.ms.log4jdbc.context.xa.ConnectionContextXA;
import fr.ms.log4jdbc.context.xa.Log4JdbcContextXA;
import fr.ms.log4jdbc.context.xa.TransactionContextXA;

public class XAResourceOperation implements ProxyOperation {

    public final static WeakHashMap<Xid, TransactionContextXA> transactions = new WeakHashMap<Xid, TransactionContextXA>();

    Log4JdbcContextXA log4JdbcContext;
    private final ConnectionContextXA connectionContext;

    private final TimeInvocation timeInvocation;
    private final Object proxy;
    private final Method method;
    private final Object[] args;

    private SqlOperationDefault sqlOperationWithOutConnectionContext;
    private SqlOperationContext sqlOperationWithConnectionContext;

    public XAResourceOperation(final Log4JdbcContextXA log4JdbcContext, final TimeInvocation timeInvocation, final Object proxy, final Method method,
	    final Object[] args) {
	this.log4JdbcContext = log4JdbcContext;
	connectionContext = log4JdbcContext.getConnectionContext();

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
		start(args);
	    } else if (nameMethod.equals("end")) {
		end(args);
	    } else if (nameMethod.equals("prepare")) {
		prepare(args, invoke);
	    } else if (nameMethod.equals("rollback")) {
		rollback(args);
	    } else if (nameMethod.equals("commit")) {
		commit(args);
	    }
	}

	if (connectionContext == null) {
	    return sqlOperationWithOutConnectionContext;
	} else {
	    return sqlOperationWithConnectionContext.valid();
	}
    }

    public void start(final Object[] args) {
	final Xid xid = ((Xid) args[0]);
	final int flags = ((Integer) args[1]).intValue();

	TransactionContextXA transactionContextXA = transactions.get(xid);

	if (transactionContextXA == null) {
	    transactionContextXA = new TransactionContextXA();
	    transactions.put(xid, transactionContextXA);
	}

	transactionContextXA.setFlags(flags);
	log4JdbcContext.setTransactionContext(transactionContextXA);
	connectionContext.setTransactionContext(transactionContextXA);
    }

    public void end(final Object[] args) {
	final Xid xid = ((Xid) args[0]);
	final int flags = ((Integer) args[1]).intValue();

	final TransactionContextXA transactionContextXA = transactions.get(xid);

	transactionContextXA.setFlags(flags);
	log4JdbcContext.setTransactionContext(transactionContextXA);
	connectionContext.setTransactionContext(transactionContextXA);
    }

    public void prepare(final Object[] args, final Object invoke) {
	final Xid xid = ((Xid) args[0]);
	final int flags = ((Integer) invoke).intValue();

	final TransactionContextXA transactionContextXA = transactions.get(xid);

	transactionContextXA.setFlags(flags);
	log4JdbcContext.setTransactionContext(transactionContextXA);
	connectionContext.setTransactionContext(transactionContextXA);

    }

    public void rollback(final Object[] args) {
	final Xid xid = ((Xid) args[0]);

	final TransactionContextXA transactionContextXA = transactions.get(xid);

	transactionContextXA.setFlags(XAResource.TMFAIL);
	log4JdbcContext.setTransactionContext(transactionContextXA);
	connectionContext.setTransactionContext(transactionContextXA);

	transactions.remove(xid);

	connectionContext.rollback(null);
    }

    public void commit(final Object[] args) {
	final Xid xid = ((Xid) args[0]);
	// final boolean onePhase = ((Boolean) args[1]).booleanValue();

	final TransactionContextXA transactionContextXA = transactions.get(xid);

	log4JdbcContext.setTransactionContext(transactionContextXA);
	connectionContext.setTransactionContext(transactionContextXA);

	transactions.remove(xid);

	connectionContext.commit();
    }

    public Object getInvoke() {
	final Object invoke = timeInvocation.getInvoke();
	return invoke;
    }
}
