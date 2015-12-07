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
package fr.ms.log4jdbc.context;

import java.util.ArrayList;
import java.util.List;

import fr.ms.lang.delegate.DefaultStringMakerFactory;
import fr.ms.lang.delegate.DefaultSyncLongFactory;
import fr.ms.lang.delegate.StringMakerFactory;
import fr.ms.lang.delegate.SyncLongFactory;
import fr.ms.lang.ref.ReferenceFactory;
import fr.ms.lang.ref.ReferenceObject;
import fr.ms.lang.stringmaker.impl.StringMaker;
import fr.ms.lang.sync.impl.SyncLong;
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
public class TransactionContext implements Transaction, Cloneable {

    private final static SyncLongFactory syncLongFactory = DefaultSyncLongFactory.getInstance();

    private final static SyncLong totalTransactionNumber = syncLongFactory.newLong();

    private final static SyncLong openTransaction = syncLongFactory.newLong();

    private String state;

    private boolean transactionInit = false;
    private long transactionNumber;

    private Object savePoint = null;

    private final static String REF_MESSAGE_FULL = "LOG4JDBC : Memory Full, clean Queries Transaction";
    private ReferenceObject refQueriesTransaction = ReferenceFactory.newReference(REF_MESSAGE_FULL, new ArrayList());

    private void initTransaction() {
	if (!transactionInit) {
	    transactionInit = true;
	    transactionNumber = totalTransactionNumber.incrementAndGet();
	    openTransaction.incrementAndGet();
	}
    }

    public void addQuery(final QueryImpl query) {
	addQuery(query, false);
    }

    public void addQuery(final QueryImpl query, final boolean batch) {
	if (savePoint != null) {
	    query.setSavePoint(savePoint);
	}

	final List queriesTransaction = (List) refQueriesTransaction.get();
	if (queriesTransaction != null) {
	    queriesTransaction.add(query);
	}

	initTransaction();

	if (batch) {
	    if (!Transaction.STATE_EXECUTE.equals(state)) {
		state = Transaction.STATE_NOT_EXECUTE;
	    }
	    query.setState(Query.STATE_NOT_EXECUTE);
	} else {
	    state = Transaction.STATE_EXECUTE;
	    query.setState(Query.STATE_EXECUTE);
	}

	try {
	    query.setTransactionContext((TransactionContext) this.clone());
	} catch (final CloneNotSupportedException e) {
	    e.printStackTrace();
	}
    }

    public void rollback(final Object savePoint) {
	final List queriesTransaction = (List) refQueriesTransaction.get();
	if (queriesTransaction == null) {
	    return;
	}

	int rollback = -1;

	if (queriesTransaction.size() > 0) {
	    for (int i = 0; i < queriesTransaction.size(); i++) {
		final QueryImpl q = (QueryImpl) queriesTransaction.get(i);

		final Object savePointQuery = q.getSavePoint();

		if (savePoint == null) {
		    q.setState(Query.STATE_ROLLBACK);
		} else {
		    if (rollback == -1 && savePoint.equals(savePointQuery)) {
			rollback = i;
		    }
		    if (rollback != -1) {
			q.setState(Query.STATE_ROLLBACK);
		    }
		}
	    }
	}

	if (state != null) {
	    state = Transaction.STATE_ROLLBACK;
	}
    }

    public void executeBatch(final int[] updateCounts) {
	final List queriesTransaction = (List) refQueriesTransaction.get();
	if (queriesTransaction == null) {
	    return;
	}

	int compteur = -1;
	int updateCountsSize = 0;

	if (updateCounts != null) {
	    updateCountsSize = updateCounts.length;
	    compteur = 0;
	}

	if (queriesTransaction.size() > 0) {
	    for (int i = 0; i < queriesTransaction.size(); i++) {
		final QueryImpl q = (QueryImpl) queriesTransaction.get(i);

		if (Query.STATE_NOT_EXECUTE.equals(q.getState())) {
		    q.setState(Query.STATE_COMMIT);

		    if (compteur >= updateCountsSize) {

			q.setUpdateCount(Integer.valueOf(updateCounts[compteur]));
			compteur++;
		    }
		}
	    }
	}

	state = Transaction.STATE_EXECUTE;
    }

    public void commit() {
	final List queriesTransaction = (List) refQueriesTransaction.get();
	if (queriesTransaction == null) {
	    return;
	}

	if (queriesTransaction.size() > 0) {
	    for (int i = 0; i < queriesTransaction.size(); i++) {
		final QueryImpl q = (QueryImpl) queriesTransaction.get(i);

		if (Query.STATE_EXECUTE.equals(q.getState())) {
		    q.setState(Query.STATE_COMMIT);
		}
	    }
	}

	if (state != null) {
	    state = Transaction.STATE_COMMIT;
	}
    }

    public void setSavePoint(final Object savePoint) {
	this.savePoint = savePoint;
    }

    public long getOpenTransaction() {
	return openTransaction.get();
    }

    public void decrement() {
	if (transactionInit) {
	    openTransaction.decrementAndGet();
	}
    }

    public long getTransactionNumber() {
	return transactionNumber;
    }

    public Query[] getQueriesTransaction() {
	final List queriesTransaction = (List) refQueriesTransaction.get();
	if (queriesTransaction == null) {
	    return null;
	}
	return (Query[]) queriesTransaction.toArray(new Query[queriesTransaction.size()]);
    }

    public String getTransactionState() {
	return state;
    }

    public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + ((getTransactionState() == null) ? 0 : getTransactionState().hashCode());
	result = prime * result + (int) (getTransactionNumber() ^ (getTransactionNumber() >>> 32));
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
	final TransactionContext other = (TransactionContext) obj;
	if (getQueriesTransaction() == null) {
	    if (other.getQueriesTransaction() != null) {
		return false;
	    }
	} else if (getQueriesTransaction().length != other.getQueriesTransaction().length) {
	    return false;
	}
	if (getTransactionState() == null) {
	    if (other.getTransactionState() != null) {
		return false;
	    }
	} else if (!getTransactionState().equals(other.getTransactionState())) {
	    return false;
	}
	if (getTransactionNumber() != other.getTransactionNumber()) {
	    return false;
	}
	return true;
    }

    public Object clone() throws CloneNotSupportedException {
	final TransactionContext t = (TransactionContext) super.clone();
	final List queriesTransaction = (List) refQueriesTransaction.get();
	if (queriesTransaction == null) {
	    t.refQueriesTransaction = ReferenceFactory.newReference(REF_MESSAGE_FULL, new ArrayList());
	} else {
	    t.refQueriesTransaction = ReferenceFactory.newReference(REF_MESSAGE_FULL, new ArrayList(queriesTransaction));
	}
	return t;
    }

    public String toString() {
	final String nl = System.getProperty("line.separator");

	final StringMakerFactory stringFactory = DefaultStringMakerFactory.getInstance();
	final StringMaker sb = stringFactory.newString();

	sb.append(getTransactionNumber() + ". " + getOpenTransaction());
	sb.append(nl);
	sb.append("	State  : " + getTransactionState());
	sb.append(nl);

	if (getQueriesTransaction() != null && getQueriesTransaction().length > 0) {
	    sb.append("*********************");
	    sb.append(nl);
	    sb.append(getQueriesTransaction().length + " queries");

	    for (int i = 0; i < getQueriesTransaction().length; i++) {
		sb.append(nl);
		sb.append(getQueriesTransaction()[i]);
	    }
	}

	return sb.toString();

    }
}
