package fr.ms.log4jdbc.context.xa;

import java.sql.Connection;
import java.sql.Driver;

import fr.ms.log4jdbc.context.Log4JdbcContext;

public class Log4JdbcContextXA implements Log4JdbcContext {

    private ConnectionContextXA connectionContext;

    private TransactionContextXA transactionContext;

    public ConnectionContextXA newConnectionContext(final Connection connection, final Class clazz) {
	connectionContext = new ConnectionContextXA(clazz);

	connectionContext.setTransactionContext(transactionContext);

	return connectionContext;
    }

    public ConnectionContextXA newConnectionContext(final Connection connection, final Driver driver, final String url) {
	connectionContext = new ConnectionContextXA(driver, url);

	connectionContext.setTransactionContext(transactionContext);

	return connectionContext;
    }

    public ConnectionContextXA getConnectionContext() {
	return connectionContext;
    }

    public TransactionContextXA getTransactionContext() {
	return transactionContext;
    }

    public void setTransactionContext(final TransactionContextXA transactionContext) {
	this.transactionContext = transactionContext;
    }
}
