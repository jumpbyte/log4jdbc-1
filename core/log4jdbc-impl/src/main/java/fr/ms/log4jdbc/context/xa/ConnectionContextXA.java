package fr.ms.log4jdbc.context.xa;

import java.sql.Driver;

import fr.ms.lang.sync.impl.SyncLong;
import fr.ms.log4jdbc.context.ConnectionContext;
import fr.ms.log4jdbc.context.Transaction;
import fr.ms.log4jdbc.rdbms.RdbmsSpecifics;
import fr.ms.log4jdbc.sql.QueryImpl;

public class ConnectionContextXA implements ConnectionContext {

    private final ConnectionContext connectionContext;

    private ConnectionContextXA(final ConnectionContext connectionContext) {
	this.connectionContext = connectionContext;
    }

    public void commit() {
	connectionContext.commit();
    }

    public void resetTransaction() {
	connectionContext.resetTransaction();
    }

    public void setSavePoint(final Object savePoint) {
	connectionContext.setSavePoint(savePoint);
    }

    public void rollback(final Object savePoint) {
	connectionContext.rollback(savePoint);
    }

    public void resetContext() {
	connectionContext.resetContext();
    }

    public QueryImpl addQuery(final QueryImpl query) {
	return connectionContext.addQuery(query);
    }

    public RdbmsSpecifics getRdbmsSpecifics() {
	return connectionContext.getRdbmsSpecifics();
    }

    public SyncLong getOpenConnection() {
	return connectionContext.getOpenConnection();
    }

    public long getConnectionNumber() {
	return connectionContext.getConnectionNumber();
    }

    public Driver getDriver() {
	return connectionContext.getDriver();
    }

    public String getUrl() {
	return connectionContext.getUrl();
    }

    public Transaction cloneTransaction(final Transaction transaction) throws CloneNotSupportedException {
	return connectionContext.cloneTransaction(transaction);
    }

    public Transaction getTransaction() {
	return connectionContext.getTransaction();
    }

    public void setEnabledTransaction(final boolean enabled) {
	connectionContext.setEnabledTransaction(enabled);
    }

    public void executeBatch(final int[] updateCounts) {
	connectionContext.executeBatch(updateCounts);
    }
}
