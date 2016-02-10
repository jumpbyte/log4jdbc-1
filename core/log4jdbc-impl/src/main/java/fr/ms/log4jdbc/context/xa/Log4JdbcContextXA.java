package fr.ms.log4jdbc.context.xa;

import java.sql.Connection;
import java.sql.Driver;

import fr.ms.log4jdbc.context.Log4JdbcContext;
import fr.ms.log4jdbc.context.jdbc.ConnectionContextJDBC;

public class Log4JdbcContextXA implements Log4JdbcContext {

    private ConnectionContextXA connectionContext;

    private TransactionContextXA transactionContext;

    public ConnectionContextJDBC newConnectionContext(final Connection connection, final Class clazz) {
	connectionContext = new ConnectionContextXA(clazz);

	connectionContext.setTransactionContextXA(transactionContext);
	connectionContext.setTransactionEnabled(transactionContext != null);

	return connectionContext;
    }

    public ConnectionContextJDBC newConnectionContext(final Connection connection, final Driver driver, final String url) {
	connectionContext = new ConnectionContextXA(driver, url);

	connectionContext.setTransactionContextXA(transactionContext);
	connectionContext.setTransactionEnabled(transactionContext != null);

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
