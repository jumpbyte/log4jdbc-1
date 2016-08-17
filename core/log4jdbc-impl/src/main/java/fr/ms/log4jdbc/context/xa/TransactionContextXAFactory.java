package fr.ms.log4jdbc.context.xa;

import fr.ms.log4jdbc.context.jdbc.TransactionContextFactory;
import fr.ms.log4jdbc.context.jdbc.TransactionContextJDBC;

public class TransactionContextXAFactory implements TransactionContextFactory {

	public TransactionContextJDBC newTransactionContext() {
		return new TransactionContextXA();
	}
}
