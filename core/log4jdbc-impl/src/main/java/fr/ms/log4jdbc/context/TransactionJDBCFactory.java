package fr.ms.log4jdbc.context;

public class TransactionJDBCFactory implements TransactionFactory {

    public TransactionContext newTransactionContext() {
	final TransactionJDBCContext transactionJDBC = new TransactionJDBCContext();

	final TransactionContainer container = new TransactionContainer(transactionJDBC);
	return container;
    }
}
