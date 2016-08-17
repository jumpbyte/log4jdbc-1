package fr.ms.log4jdbc.context.jdbc;

public class TransactionContextJDBCFactory implements TransactionContextFactory {

	public TransactionContextJDBC newTransactionContext() {
		return new TransactionContextJDBC();
	}
}
