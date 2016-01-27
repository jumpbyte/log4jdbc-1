package fr.ms.log4jdbc.context.xa;

import java.sql.Driver;

import fr.ms.log4jdbc.context.jdbc.ConnectionContextJDBC;
import fr.ms.log4jdbc.context.jdbc.TransactionContextJDBC;
import fr.ms.log4jdbc.sql.QueryImpl;

public class ConnectionContextXA extends ConnectionContextJDBC {

    private boolean transactionActive = false;

    private TransactionContextXA transactionContextXA;

    public ConnectionContextXA(final Class clazz) {
	super(clazz);
    }

    public ConnectionContextXA(final Driver driver, final String url) {
	super(driver, url);
    }

    @Override
    public void setTransactionEnabled(final boolean transactionEnabled) {
	if (!transactionEnabled) {
	    transactionContextXA = null;
	    transactionActive = false;
	}
	super.setTransactionEnabled(transactionEnabled);
    }

    @Override
    public QueryImpl addQuery(final QueryImpl query) {
	if (transactionContextXA == null) {
	    return super.addQuery(query);
	}

	if (transactionEnabled) {
	    transactionActive = true;
	    transactionContextXA.addQuery(query);
	}

	return query;
    }

    public void setTransactionContextXA(final TransactionContextXA transactionContextXA) {
	this.transactionContextXA = transactionContextXA;
	setTransactionEnabled(this.transactionContextXA != null);
    }

    @Override
    public TransactionContextJDBC getTransactionContext() {
	if (transactionContextXA == null) {
	    return super.getTransactionContext();
	}
	if (transactionActive) {
	    return transactionContextXA;
	}

	return null;
    }

    @Override
    public void commit() {
	if (transactionContextXA == null) {
	    super.commit();
	} else {
	    transactionContextXA.commit();
	}

    }

    @Override
    public void rollback(final Object savePoint) {
	if (transactionContextXA == null) {
	    super.rollback(savePoint);
	} else {
	    transactionContextXA.rollback(null);
	}
    }

    @Override
    public void resetTransaction() {
	transactionContextXA.close();
	setTransactionEnabled(false);
    }
}
