package fr.ms.log4jdbc.context;

import fr.ms.lang.delegate.DefaultSyncLongFactory;
import fr.ms.lang.delegate.SyncLongFactory;
import fr.ms.lang.sync.impl.SyncLong;

public class TransactionContextDefault {

    private final static SyncLongFactory syncLongFactory = DefaultSyncLongFactory.getInstance();

    private final static SyncLong totalTransactionNumber = syncLongFactory.newLong();

    private final static SyncLong openTransactionCurrent = syncLongFactory.newLong();

    private long transactionNumber;

    private long openTransaction;

    protected String state = Transaction.STATE_NOT_EXECUTE;

    {
	transactionNumber = totalTransactionNumber.incrementAndGet();
	openTransaction = openTransactionCurrent.incrementAndGet();
    }

    public void close() {
	openTransactionCurrent.decrementAndGet();
    }

    public long getOpenTransaction() {
	return openTransaction;
    }

    public long getTransactionNumber() {
	return transactionNumber;
    }

    public String getTransactionState() {
	return state;
    }
}
