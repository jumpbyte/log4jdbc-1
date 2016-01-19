package fr.ms.log4jdbc.context;

import fr.ms.lang.delegate.DefaultSyncLongFactory;
import fr.ms.lang.delegate.SyncLongFactory;
import fr.ms.lang.sync.impl.SyncLong;

public class TransactionContextDefault {

    private final static SyncLongFactory syncLongFactory = DefaultSyncLongFactory.getInstance();

    private final static SyncLong totalTransactionNumber = syncLongFactory.newLong();

    private final static SyncLong openTransaction = syncLongFactory.newLong();

    private long transactionNumber;

    protected String state = Transaction.STATE_NOT_EXECUTE;

    {
	transactionNumber = totalTransactionNumber.incrementAndGet();
	openTransaction.incrementAndGet();
    }

    public void close() {
	openTransaction.decrementAndGet();
    }

    public long getOpenTransaction() {
	return openTransaction.get();
    }

    public long getTransactionNumber() {
	return transactionNumber;
    }

    public String getTransactionState() {
	return state;
    }
}
