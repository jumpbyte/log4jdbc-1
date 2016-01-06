package fr.ms.log4jdbc;

import java.sql.Driver;
import java.util.Date;

import fr.ms.lang.reflect.TimeInvocation;
import fr.ms.log4jdbc.context.Transaction;
import fr.ms.log4jdbc.rdbms.RdbmsSpecifics;
import fr.ms.log4jdbc.sql.Query;

public class SqlOperationDefault implements SqlOperation {

    private final TimeInvocation timeInvocation;

    public SqlOperationDefault(final TimeInvocation timeInvocation) {
	this.timeInvocation = timeInvocation;
    }

    public Date getDate() {
	return timeInvocation.getStartDate();
    }

    public long getExecTime() {
	return timeInvocation.getExecTime();
    }

    public long getConnectionNumber() {
	return -1;
    }

    public long getOpenConnection() {
	return -1;
    }

    public Driver getDriver() {
	return null;
    }

    public RdbmsSpecifics getRdbms() {
	return null;
    }

    public String getUrl() {
	return null;
    }

    public Query getQuery() {
	return null;
    }

    public Transaction getTransaction() {
	return null;
    }
}
