package fr.ms.log4jdbc.context;

import java.sql.Driver;

import fr.ms.lang.sync.impl.SyncLong;
import fr.ms.log4jdbc.rdbms.RdbmsSpecifics;
import fr.ms.log4jdbc.sql.QueryImpl;

public interface ConnectionContext {
    void commit();

    void resetTransaction();

    void setSavePoint(Object savePoint);

    void rollback(final Object savePoint);

    void resetContext();

    QueryImpl addQuery(QueryImpl query);

    RdbmsSpecifics getRdbmsSpecifics();

    SyncLong getOpenConnection();

    long getConnectionNumber();

    Driver getDriver();

    String getUrl();

    Transaction cloneTransaction(Transaction transaction) throws CloneNotSupportedException;

    Transaction getTransaction();

    void setEnabledTransaction(boolean enabled);

    void executeBatch(int[] updateCounts);
}
