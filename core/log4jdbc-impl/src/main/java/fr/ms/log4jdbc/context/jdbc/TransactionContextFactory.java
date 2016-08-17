package fr.ms.log4jdbc.context.jdbc;

public interface TransactionContextFactory {

	TransactionContextJDBC newTransactionContext();
}
