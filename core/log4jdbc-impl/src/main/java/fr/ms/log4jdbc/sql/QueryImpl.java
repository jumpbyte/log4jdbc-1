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
package fr.ms.log4jdbc.sql;

import java.sql.ResultSet;
import java.util.Date;
import java.util.Map;

import fr.ms.lang.delegate.DefaultStringMakerFactory;
import fr.ms.lang.delegate.DefaultSyncLongFactory;
import fr.ms.lang.delegate.StringMakerFactory;
import fr.ms.lang.delegate.SyncLongFactory;
import fr.ms.lang.reflect.TimeInvocation;
import fr.ms.lang.stringmaker.impl.StringMaker;
import fr.ms.lang.sync.impl.SyncLong;
import fr.ms.log4jdbc.context.Transaction;
import fr.ms.log4jdbc.context.jdbc.ConnectionJDBCContext;
import fr.ms.log4jdbc.context.jdbc.TransactionJDBCContext;
import fr.ms.log4jdbc.resultset.ResultSetCollector;
import fr.ms.log4jdbc.resultset.ResultSetCollectorImpl;
import fr.ms.log4jdbc.sql.internal.QuerySQL;

/**
 *
 * @see <a href="http://marcosemiao4j.wordpress.com">Marco4J</a>
 *
 *
 * @author Marco Semiao
 *
 */
public class QueryImpl implements Query, Cloneable {

    private final static SyncLongFactory syncLongFactory = DefaultSyncLongFactory.getInstance();

    private final static SyncLong nbQueryTotal = syncLongFactory.newLong();

    private long queryNumber;
    private TimeInvocation timeInvocation;

    private String methodQuery;

    private final QuerySQL query;

    private Integer updateCount;

    private ResultSetCollectorImpl resultSetCollector;

    private String state = Query.STATE_COMMIT;

    private Transaction transaction;

    private Object savePoint;

    public QueryImpl(final QuerySQL query) {
	this.query = query;
    }

    public Date getDate() {
	if (timeInvocation == null) {
	    return null;
	}
	return timeInvocation.getStartDate();
    }

    public long getExecTime() {
	if (timeInvocation == null) {
	    return -1;
	}
	return timeInvocation.getExecTime();
    }

    public long getQueryNumber() {
	return queryNumber;
    }

    public String getMethodQuery() {
	return methodQuery;
    }

    public String getJDBCQuery() {
	if (query == null) {
	    return null;
	}
	return query.getJDBCQuery();
    }

    public Map getJDBCParameters() {
	if (query == null) {
	    return null;
	}
	return query.getJDBCParameters();
    }

    public String getTypeQuery() {
	if (query == null) {
	    return null;
	}
	return query.getTypeQuery();
    }

    public String getSQLQuery() {
	if (query == null) {
	    return null;
	}
	return query.getSQLQuery();
    }

    public Integer getUpdateCount() {
	return updateCount;
    }

    public ResultSetCollector getResultSetCollector() {
	if (resultSetCollector == null || resultSetCollector.isMetaDataError()) {
	    return null;
	}
	return resultSetCollector;
    }

    public String getState() {
	return state;
    }

    public Transaction getTransaction() {
	return transaction;
    }

    public Object putParams(final Object key, final Object value) {
	if (query == null) {
	    return null;
	}
	return query.putParams(key, value);
    }

    public void setTimeInvocation(final TimeInvocation timeInvocation) {
	this.timeInvocation = timeInvocation;
    }

    public void setUpdateCount(final Integer updateCount) {
	if (updateCount != null && updateCount.intValue() >= 0) {
	    this.updateCount = updateCount;
	}
    }

    public void initResultSetCollector(final ConnectionJDBCContext connectionContext) {
	if (this.resultSetCollector == null) {
	    this.resultSetCollector = new ResultSetCollectorImpl(connectionContext);
	}
    }

    public void initResultSetCollector(final ConnectionJDBCContext connectionContext, final ResultSet rs) {
	initResultSetCollector(connectionContext);
	this.resultSetCollector.setRs(rs);
    }

    public void setMethodQuery(final String methodQuery) {
	this.methodQuery = methodQuery;
    }

    public void setState(final String state) {
	if (Query.STATE_NOT_EXECUTE.equals(this.state) && Query.STATE_EXECUTE.equals(state)) {
	    this.queryNumber = nbQueryTotal.incrementAndGet();
	}
	this.state = state;
    }

    public void setTransactionContext(final TransactionJDBCContext transactionContext) {
	this.transaction = transactionContext;
    }

    public void setSavePoint(final Object savePoint) {
	this.savePoint = savePoint;
    }

    public Object getSavePoint() {
	return savePoint;
    }

    public Object clone() throws CloneNotSupportedException {
	return super.clone();
    }

    public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + (int) (queryNumber ^ (queryNumber >>> 32));
	result = prime * result + ((state == null) ? 0 : state.hashCode());
	return result;
    }

    public boolean equals(final Object obj) {
	if (this == obj) {
	    return true;
	}
	if (obj == null) {
	    return false;
	}
	if (getClass() != obj.getClass()) {
	    return false;
	}
	final QueryImpl other = (QueryImpl) obj;
	if (queryNumber != other.queryNumber) {
	    return false;
	}
	if (state == null) {
	    if (other.state != null) {
		return false;
	    }
	} else if (!state.equals(other.state)) {
	    return false;
	}
	return true;
    }

    public String toString() {
	final String nl = System.getProperty("line.separator");

	final StringMakerFactory stringFactory = DefaultStringMakerFactory.getInstance();
	final StringMaker sb = stringFactory.newString();

	sb.append(getQueryNumber() + ".");
	sb.append(nl);
	sb.append("	Method : " + getMethodQuery());
	sb.append(nl);
	sb.append("	State  : " + getState());
	sb.append(nl);
	sb.append("	Query  : " + getSQLQuery());

	return sb.toString();
    }
}
