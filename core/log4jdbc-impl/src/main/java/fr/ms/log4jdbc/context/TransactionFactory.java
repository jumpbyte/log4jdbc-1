package fr.ms.log4jdbc.context;

public interface TransactionFactory {

    TransactionContext newTransactionContext();
}
