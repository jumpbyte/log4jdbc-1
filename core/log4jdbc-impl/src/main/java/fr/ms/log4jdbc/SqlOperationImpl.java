/*
 * This file is part of Log4Jdbc.
 *
 * Log4Jdbc is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Log4Jdbc is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Log4Jdbc.  If not, see <http://www.gnu.org/licenses/>.
 *
 */
package fr.ms.log4jdbc;

import java.sql.Driver;
import java.util.Date;

import fr.ms.lang.reflect.TimeInvocation;
import fr.ms.log4jdbc.context.Batch;
import fr.ms.log4jdbc.context.BatchImpl;
import fr.ms.log4jdbc.context.SqlOperationContext;
import fr.ms.log4jdbc.context.Transaction;
import fr.ms.log4jdbc.context.TransactionImpl;
import fr.ms.log4jdbc.context.internal.BatchContext;
import fr.ms.log4jdbc.context.internal.ConnectionContext;
import fr.ms.log4jdbc.context.internal.TransactionContext;
import fr.ms.log4jdbc.rdbms.RdbmsSpecifics;
import fr.ms.log4jdbc.sql.Query;
import fr.ms.log4jdbc.sql.QueryImpl;

/**
 *
 * @see <a href="http://marcosemiao4j.wordpress.com">Marco4J</a>
 *
 *
 * @author Marco Semiao
 *
 */
public class SqlOperationImpl implements SqlOperation, Cloneable {

    private final TimeInvocation timeInvocation;

    private final ConnectionContext connectionContext;

    private final BatchContext batchContext;
    private final TransactionContext transactionContext;

    private final long openConnection;

    private QueryImpl query;

    private Batch batch;

    private Transaction transaction;

    public SqlOperationImpl(final SqlOperationContext mic) {
	this(mic.getInvokeTime(), mic.getConnectionContext());
    }

    public SqlOperationImpl(final TimeInvocation timeInvocation, final ConnectionContext connectionContext) {
	this.timeInvocation = timeInvocation;
	this.connectionContext = connectionContext;
	this.openConnection = connectionContext.getOpenConnection().get();

	batchContext = connectionContext.getBatchContext();
	transactionContext = connectionContext.getTransactionContext();

	batch = new BatchImpl(batchContext);
	transaction = new TransactionImpl(transactionContext);
    }

    public Object clone() throws CloneNotSupportedException {
	if (query != null) {
	    query = (QueryImpl) query.clone();
	}

	batch = new BatchImpl((BatchContext) batchContext.clone());
	transaction = new TransactionImpl((TransactionContext) transactionContext.clone());

	return this;
    }

    public Date getDate() {
	return timeInvocation.getStartDate();
    }

    public long getExecTime() {
	return timeInvocation.getExecTime();
    }

    public long getConnectionNumber() {
	return connectionContext.getConnectionNumber();
    }

    public long getOpenConnection() {
	return openConnection;
    }

    public Driver getDriver() {
	return connectionContext.getDriver();
    }

    public RdbmsSpecifics getRdbms() {
	return connectionContext.getRdbmsSpecifics();
    }

    public String getUrl() {
	return connectionContext.getUrl();
    }

    public Query getQuery() {
	return query;
    }

    public void setQuery(final QueryImpl query) {
	this.query = query;
    }

    public boolean isAutoCommit() {
	return connectionContext.isAutoCommit();
    }

    public Transaction getTransaction() {
	if (isAutoCommit() || transaction == null || transaction.getTransactionState() == null) {
	    return null;
	}
	return transaction;
    }

    public Batch getBatch() {
	if (isAutoCommit() || batch == null || batch.getBatchState() == null) {
	    return null;
	}
	return batch;
    }

    public String toString() {
	final StringBuffer buffer = new StringBuffer();
	buffer.append("SqlOperationImpl [getConnectionNumber()=");
	buffer.append(getConnectionNumber());
	buffer.append(", getOpenConnection()=");
	buffer.append(getOpenConnection());
	buffer.append(", getUrl()=");
	buffer.append(getUrl());
	buffer.append(", getDriver()=");
	buffer.append(getDriver());
	buffer.append(", getRdbms()=");
	buffer.append(getRdbms());
	buffer.append(", getDate()=");
	buffer.append(getDate());
	buffer.append(", getExecTime()=");
	buffer.append(getExecTime());
	buffer.append(", getQuery()=");
	buffer.append(getQuery());
	buffer.append(", isAutoCommit()=");
	buffer.append(isAutoCommit());
	buffer.append(", getTransaction()=");
	buffer.append(getTransaction());
	buffer.append(", getBatch()=");
	buffer.append(getBatch());
	buffer.append("]");
	return buffer.toString();
    }
}
