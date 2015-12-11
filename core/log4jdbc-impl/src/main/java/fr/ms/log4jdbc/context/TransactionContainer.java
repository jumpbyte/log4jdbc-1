package fr.ms.log4jdbc.context;

import fr.ms.lang.delegate.DefaultStringMakerFactory;
import fr.ms.lang.delegate.DefaultSyncLongFactory;
import fr.ms.lang.delegate.StringMakerFactory;
import fr.ms.lang.delegate.SyncLongFactory;
import fr.ms.lang.stringmaker.impl.StringMaker;
import fr.ms.lang.sync.impl.SyncLong;
import fr.ms.log4jdbc.sql.Query;
import fr.ms.log4jdbc.sql.QueryImpl;

public class TransactionContainer implements TransactionContext {

    private TransactionContext transaction;

    private final static SyncLongFactory syncLongFactory = DefaultSyncLongFactory.getInstance();

    private final static SyncLong totalTransactionNumber = syncLongFactory.newLong();

    private final static SyncLong openTransaction = syncLongFactory.newLong();

    private boolean transactionInit = false;

    private long transactionNumber;

    public TransactionContainer(final TransactionContext transaction) {
	this.transaction = transaction;
    }

    private void initTransaction() {
	if (!transactionInit) {
	    transactionNumber = totalTransactionNumber.incrementAndGet();
	    openTransaction.incrementAndGet();
	    transactionInit = true;
	}
    }

    public void addQuery(final QueryImpl query, final boolean batch) {
	transaction.addQuery(query, batch);
	initTransaction();
    }

    public long getTransactionNumber() {
	return transactionNumber;
    }

    public long getOpenTransaction() {
	return openTransaction.get();
    }

    public void reset() {
	if (transactionInit) {
	    openTransaction.decrementAndGet();
	}
	transaction.reset();
    }

    public void executeBatch(final int[] updateCounts) {
	transaction.executeBatch(updateCounts);
    }

    public boolean isEnabled() {
	return transaction.isEnabled();
    }

    public void setEnabled(final boolean enabled) {
	transaction.setEnabled(enabled);
    }

    public void commit() {
	transaction.commit();
    }

    public void setSavePoint(final Object savePoint) {
	transaction.setSavePoint(savePoint);
    }

    public void rollback(final Object savePoint) {
	transaction.rollback(savePoint);
    }

    public Object clone() throws CloneNotSupportedException {
	this.transaction = (TransactionContext) transaction.clone();
	return this;
    }

    public String getTransactionType() {
	return transaction.getTransactionType();
    }

    public String getTransactionState() {
	return transaction.getTransactionState();
    }

    public Query[] getQueriesTransaction() {
	return transaction.getQueriesTransaction();
    }

    public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + ((getTransactionState() == null) ? 0 : getTransactionState().hashCode());
	result = prime * result + (int) (getTransactionNumber() ^ (getTransactionNumber() >>> 32));
	return result;
    }

    public boolean equals(final Object obj) {
	if (this == obj) {
	    return true;
	}
	if (obj == null) {
	    return false;
	}
	if (getClass() != obj.getClass()) {
	    return false;
	}
	final TransactionContainer other = (TransactionContainer) obj;
	if (getQueriesTransaction() == null) {
	    if (other.getQueriesTransaction() != null) {
		return false;
	    }
	} else if (getQueriesTransaction().length != other.getQueriesTransaction().length) {
	    return false;
	}
	if (getTransactionState() == null) {
	    if (other.getTransactionState() != null) {
		return false;
	    }
	} else if (!getTransactionState().equals(other.getTransactionState())) {
	    return false;
	}
	if (getTransactionNumber() != other.getTransactionNumber()) {
	    return false;
	}
	return true;
    }

    public String toString() {
	final String nl = System.getProperty("line.separator");

	final StringMakerFactory stringFactory = DefaultStringMakerFactory.getInstance();
	final StringMaker sb = stringFactory.newString();

	sb.append(getTransactionNumber() + ". " + getOpenTransaction());
	sb.append(nl);
	sb.append("	Type  : " + getTransactionType());
	sb.append(nl);
	sb.append("	State  : " + getTransactionState());
	sb.append(nl);

	if (getQueriesTransaction() != null && getQueriesTransaction().length > 0) {
	    sb.append("*********************");
	    sb.append(nl);
	    sb.append(getQueriesTransaction().length + " queries");

	    for (int i = 0; i < getQueriesTransaction().length; i++) {
		sb.append(nl);
		sb.append(getQueriesTransaction()[i]);
	    }
	}

	return sb.toString();
    }
}
