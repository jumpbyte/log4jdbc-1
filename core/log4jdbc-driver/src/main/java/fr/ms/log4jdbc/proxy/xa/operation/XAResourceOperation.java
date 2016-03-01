package fr.ms.log4jdbc.proxy.xa.operation;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.WeakHashMap;

import javax.transaction.xa.XAResource;
import javax.transaction.xa.Xid;

import fr.ms.lang.reflect.TimeInvocation;
import fr.ms.log4jdbc.SqlOperation;
import fr.ms.log4jdbc.SqlOperationContext;
import fr.ms.log4jdbc.SqlOperationDefault;
import fr.ms.log4jdbc.context.xa.ConnectionContextXA;
import fr.ms.log4jdbc.context.xa.Log4JdbcContextXA;
import fr.ms.log4jdbc.context.xa.TransactionContextXA;
import fr.ms.log4jdbc.proxy.handler.Log4JdbcOperation;
import fr.ms.util.CollectionsUtil;

public class XAResourceOperation implements Log4JdbcOperation {

    private final static Map transactions = CollectionsUtil.synchronizedMap(new WeakHashMap());

    private final Log4JdbcContextXA log4JdbcContext;
    private final ConnectionContextXA connectionContext;

    private final TimeInvocation timeInvocation;
    private final Method method;
    private final Object[] args;

    private boolean resetTransaction;

    public XAResourceOperation(final Log4JdbcContextXA log4JdbcContext, final TimeInvocation timeInvocation, final Object proxy, final Method method,
	    final Object[] args) {
	this.log4JdbcContext = log4JdbcContext;
	connectionContext = log4JdbcContext.getConnectionContext();

	this.timeInvocation = timeInvocation;
	this.method = method;
	this.args = args;
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
	    return new SqlOperationDefault(timeInvocation);
	} else {
	    return new SqlOperationContext(timeInvocation, connectionContext);
	}
    }

    public void postOperation() {
	if (resetTransaction) {
	    connectionContext.resetTransaction();
	    resetTransaction = false;
	}
    }

    public void start(final Object[] args) {
	final Xid xid = ((Xid) args[0]);
	final int flags = ((Integer) args[1]).intValue();

	TransactionContextXA transactionContextXA = (TransactionContextXA) transactions.get(xid);

	if (transactionContextXA == null) {
	    transactionContextXA = new TransactionContextXA();
	    transactions.put(xid, transactionContextXA);
	}

	transactionContextXA.setFlags(flags);
	connectionContext.setTransactionContextXA(transactionContextXA);
	log4JdbcContext.setTransactionContext(transactionContextXA);
    }

    public void end(final Object[] args) {
	final Xid xid = ((Xid) args[0]);
	final int flags = ((Integer) args[1]).intValue();

	final TransactionContextXA transactionContextXA = (TransactionContextXA) transactions.get(xid);

	transactionContextXA.setFlags(flags);
	connectionContext.setTransactionContextXA(transactionContextXA);
	log4JdbcContext.setTransactionContext(transactionContextXA);
    }

    public void prepare(final Object[] args, final Object invoke) {
	final Xid xid = ((Xid) args[0]);
	final int flags = ((Integer) invoke).intValue();

	final TransactionContextXA transactionContextXA = (TransactionContextXA) transactions.get(xid);

	transactionContextXA.setFlags(flags);
	connectionContext.setTransactionContextXA(transactionContextXA);
	log4JdbcContext.setTransactionContext(transactionContextXA);
    }

    public void rollback(final Object[] args) {
	final Xid xid = ((Xid) args[0]);

	final TransactionContextXA transactionContextXA = (TransactionContextXA) transactions.get(xid);

	transactionContextXA.setFlags(XAResource.TMFAIL);
	connectionContext.setTransactionContextXA(transactionContextXA);
	log4JdbcContext.setTransactionContext(transactionContextXA);

	transactions.remove(xid);

	connectionContext.rollback(null);
	resetTransaction = true;
    }

    public void commit(final Object[] args) {
	final Xid xid = ((Xid) args[0]);
	final boolean onePhase = ((Boolean) args[1]).booleanValue();

	final TransactionContextXA transactionContextXA = (TransactionContextXA) transactions.get(xid);

	connectionContext.setTransactionContextXA(transactionContextXA);
	log4JdbcContext.setTransactionContext(transactionContextXA);

	transactions.remove(xid);

	connectionContext.commit();
	resetTransaction = true;
    }

    public Object getInvoke() {
	final Object invoke = timeInvocation.getInvoke();
	return invoke;
    }
}
