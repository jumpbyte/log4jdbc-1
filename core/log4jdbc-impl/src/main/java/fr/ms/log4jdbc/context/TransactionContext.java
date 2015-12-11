package fr.ms.log4jdbc.context;

import fr.ms.log4jdbc.sql.QueryImpl;

public interface TransactionContext extends Transaction, Cloneable {

    boolean isEnabled();

    void setEnabled(boolean enabled);

    void addQuery(QueryImpl query, boolean batch);

    void executeBatch(int[] updateCounts);

    void commit();

    void setSavePoint(Object savePoint);

    void rollback(Object savePoint);

    void reset();

    Object clone() throws CloneNotSupportedException;
}
