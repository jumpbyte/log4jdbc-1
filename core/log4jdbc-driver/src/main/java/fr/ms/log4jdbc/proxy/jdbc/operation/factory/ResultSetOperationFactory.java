package fr.ms.log4jdbc.proxy.jdbc.operation.factory;

import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;

import fr.ms.lang.reflect.ProxyOperation;
import fr.ms.lang.reflect.ProxyOperationFactory;
import fr.ms.lang.reflect.TimeInvocation;
import fr.ms.log4jdbc.context.jdbc.ConnectionContextJDBC;
import fr.ms.log4jdbc.proxy.jdbc.operation.ResultSetOperation;
import fr.ms.log4jdbc.resultset.CellImpl;
import fr.ms.log4jdbc.resultset.ResultSetCollectorImpl;
import fr.ms.log4jdbc.sql.QueryImpl;

public class ResultSetOperationFactory implements ProxyOperationFactory {

    private final ConnectionContextJDBC connectionContext;

    public final QueryImpl query;

    public final ResultSet rs;

    public Integer position;

    public CellImpl lastCell;

    public ResultSetOperationFactory(final ConnectionContextJDBC connectionContext, final ResultSet rs, final QueryImpl query) {
	this.connectionContext = connectionContext;
	this.rs = rs;
	this.query = query;

	position = new Integer(0);
	try {
	    position = new Integer(rs.getRow());
	} catch (final Throwable e) {
	}
    }

    public ProxyOperation newOperation(final TimeInvocation timeInvocation, final Object proxy, final Method method, final Object[] args) {
	final ProxyOperation operation = new ResultSetOperation(this, connectionContext, timeInvocation, method, args);

	return operation;
    }

    public QueryImpl next(final boolean valid) {
	if (valid) {
	    addPosition(1);
	    return getQuery();
	} else {
	    position = null;
	    return null;
	}
    }

    public QueryImpl previous(final boolean valid) {
	if (valid) {
	    if (position == null) {
		getMaxValue();
	    } else {
		addPosition(-1);
	    }
	    return getQuery();
	} else {
	    position = null;
	    return null;
	}
    }

    public QueryImpl first(final boolean valid) {
	if (valid) {
	    position = new Integer(1);
	    return getQuery();
	} else {
	    position = null;
	    return null;
	}
    }

    public QueryImpl last(final boolean valid) {
	if (valid) {
	    getMaxValue();
	    return getQuery();
	} else {
	    position = null;
	    return null;
	}
    }

    public void beforeFirst() {
	position = null;
    }

    public void afterLast() {
	position = null;
    }

    public void wasNull() {
	lastCell.wasNull();
    }

    public void getMetaData(final Object invoke) {
	final ResultSetCollectorImpl resultSetCollector = (ResultSetCollectorImpl) query.getResultSetCollector();

	if (resultSetCollector.getColumns().length == 0) {
	    final ResultSetMetaData resultSetMetaData = (ResultSetMetaData) invoke;
	    resultSetCollector.setColumnsDetail(resultSetMetaData);
	}
    }

    public void addValueColumn(final Class clazz, final Object[] args, final Object invoke) {
	final ResultSetCollectorImpl resultSetCollector = (ResultSetCollectorImpl) query.getResultSetCollector();
	if (Integer.class.equals(clazz) || Integer.TYPE.equals(clazz)) {
	    final Integer arg = (Integer) args[0];
	    lastCell = resultSetCollector.addValueColumn(getPosition(), invoke, arg.intValue());
	} else if (String.class.equals(clazz)) {
	    final String arg = (String) args[0];
	    lastCell = resultSetCollector.addValueColumn(getPosition(), invoke, arg);
	}
    }

    public QueryImpl close() {
	final ResultSetCollectorImpl resultSetCollector = (ResultSetCollectorImpl) query.getResultSetCollector();

	if (!resultSetCollector.isClosed()) {
	    resultSetCollector.close();

	    return query;
	}

	return null;
    }

    private void getMaxValue() {
	position = new Integer(Integer.MAX_VALUE);
	try {
	    position = new Integer(rs.getRow());
	} catch (final Throwable e) {
	}
    }

    private int getPosition() {
	if (position == null) {
	    position = new Integer(1);
	    try {
		position = new Integer(rs.getRow());
	    } catch (final Throwable e) {
	    }
	}

	return position.intValue();
    }

    private void addPosition(final int addValue) {
	if (position == null) {
	    getPosition();
	} else {
	    position = new Integer(position.intValue() + addValue);
	}
    }

    private QueryImpl getQuery() {
	final ResultSetCollectorImpl resultSetCollector = (ResultSetCollectorImpl) query.getResultSetCollector();
	resultSetCollector.getRow(position.intValue());

	if (!resultSetCollector.isClosed()) {
	    return query;
	}
	return null;
    }
}
