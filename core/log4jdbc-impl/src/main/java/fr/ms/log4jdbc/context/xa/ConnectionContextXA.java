package fr.ms.log4jdbc.context.xa;

import java.sql.Driver;

import fr.ms.log4jdbc.context.jdbc.ConnectionContextJDBC;
import fr.ms.log4jdbc.context.jdbc.TransactionContextJDBC;
import fr.ms.log4jdbc.sql.QueryImpl;

public class ConnectionContextXA extends ConnectionContextJDBC {

    private TransactionContextXA transactionContext;

    public ConnectionContextXA(final Class clazz) {
	super(clazz);
    }

    public ConnectionContextXA(final Driver driver, final String url) {
	super(driver, url);
    }

    public void setTransactionContext(final TransactionContextXA transactionContext) {
	this.transactionContext = transactionContext;

	setTransactionEnabled(transactionContext != null);
    }

    @Override
    public void setTransactionEnabled(final boolean transactionEnabled) {
	if (!transactionEnabled) {
	    transactionContext = null;
	}
	super.setTransactionEnabled(transactionEnabled);
    }

    @Override
    public QueryImpl addQuery(final QueryImpl query) {
	if (transactionContext == null) {
	    return super.addQuery(query);
	}

	if (transactionEnabled) {
	    transactionContext.addQuery(query);
	}

	return query;

    }

    @Override
    public TransactionContextJDBC getTransactionContext() {
	if (transactionContext == null) {
	    return super.getTransactionContext();
	}
	return transactionContext;
    }

    @Override
    public void commit() {
	if (transactionContext == null) {
	    super.commit();
	} else {
	    transactionContext.commit();
	    transactionContext.close();
	    transactionContext = null;
	}

    }

    @Override
    public void rollback(final Object savePoint) {
	if (transactionContext == null) {
	    super.rollback(savePoint);
	} else {
	    transactionContext.rollback(null);
	    transactionContext.close();
	    transactionContext = null;
	}
    }
}
