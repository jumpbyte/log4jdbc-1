package fr.ms.log4jdbc.context;

import java.util.ArrayList;
import java.util.List;

import fr.ms.lang.ref.ReferenceFactory;
import fr.ms.lang.ref.ReferenceObject;
import fr.ms.log4jdbc.sql.Query;
import fr.ms.log4jdbc.sql.QueryImpl;

public class TransactionJDBCContext implements TransactionContext {

    private boolean enabled;

    private String state;

    private Object savePoint = null;

    private final static String REF_MESSAGE_FULL = "LOG4JDBC : Memory Full, clean Queries Transaction";
    private ReferenceObject refQueriesTransaction = ReferenceFactory.newReference(REF_MESSAGE_FULL, new ArrayList());

    public void addQuery(final QueryImpl query, final boolean batch) {
	if (savePoint != null) {
	    query.setSavePoint(savePoint);
	}

	final List queriesTransaction = (List) refQueriesTransaction.get();
	if (queriesTransaction != null) {
	    queriesTransaction.add(query);
	}

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
	    query.setTransactionContext((TransactionJDBCContextOld) this.clone());
	} catch (final CloneNotSupportedException e) {
	    e.printStackTrace();
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
		    q.setState(Query.STATE_EXECUTE);

		    if (compteur <= updateCountsSize) {

			q.setUpdateCount(Integer.valueOf(updateCounts[compteur]));
			compteur++;
		    }
		}
	    }
	}

	state = Transaction.STATE_EXECUTE;
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

    public String getTransactionType() {
	return "JDBC";
    }

    public String getTransactionState() {
	return state;
    }

    public Query[] getQueriesTransaction() {
	final List queriesTransaction = (List) refQueriesTransaction.get();
	if (queriesTransaction == null) {
	    return null;
	}
	return (Query[]) queriesTransaction.toArray(new Query[queriesTransaction.size()]);
    }

    public boolean isEnabled() {
	return enabled;
    }

    public void setEnabled(final boolean enabled) {
	this.enabled = enabled;
    }

    public void setSavePoint(final Object savePoint) {
	this.savePoint = savePoint;
    }

    public Object clone() throws CloneNotSupportedException {
	final TransactionJDBCContext t = (TransactionJDBCContext) super.clone();
	final List queriesTransaction = (List) refQueriesTransaction.get();
	if (queriesTransaction == null) {
	    t.refQueriesTransaction = ReferenceFactory.newReference(REF_MESSAGE_FULL, new ArrayList());
	} else {
	    final List copies = new ArrayList(queriesTransaction.size());
	    for (int i = 0; i < queriesTransaction.size(); i++) {
		final QueryImpl query = (QueryImpl) queriesTransaction.get(i);
		copies.add(query.clone());
	    }

	    t.refQueriesTransaction = ReferenceFactory.newReference(REF_MESSAGE_FULL, copies);
	}
	return t;
    }

    public void reset() {

    }

    public long getTransactionNumber() {
	throw new UnsupportedOperationException();
    }

    public long getOpenTransaction() {
	throw new UnsupportedOperationException();
    }
}
