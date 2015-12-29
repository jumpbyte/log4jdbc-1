package fr.ms.log4jdbc.proxy.operation.factory;

import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;

import fr.ms.lang.reflect.TimeInvocation;
import fr.ms.log4jdbc.context.jdbc.ConnectionJDBCContext;
import fr.ms.log4jdbc.proxy.handler.Log4JdbcOperation;
import fr.ms.log4jdbc.proxy.handler.Log4JdbcOperationFactory;
import fr.ms.log4jdbc.proxy.operation.ResultSetOperation;
import fr.ms.log4jdbc.resultset.CellImpl;
import fr.ms.log4jdbc.resultset.ResultSetCollectorImpl;
import fr.ms.log4jdbc.sql.QueryImpl;

public class ResultSetOperationFactory implements Log4JdbcOperationFactory {

    private final ConnectionJDBCContext connectionContext;

    public final QueryImpl query;

    public final ResultSet rs;

    public Integer position;

    public CellImpl lastCell;

    public ResultSetOperationFactory(final ConnectionJDBCContext connectionContext, final ResultSet rs, final QueryImpl query) {
	this.connectionContext = connectionContext;
	this.rs = rs;
	this.query = query;

	position = 0;
	try {
	    position = rs.getRow();
	} catch (final Throwable e) {
	}
    }

    public Log4JdbcOperation newLog4JdbcOperation(final TimeInvocation timeInvocation, final Object proxy, final Method method, final Object[] args) {
	final Log4JdbcOperation operation = new ResultSetOperation(this, connectionContext, timeInvocation, proxy, method, args);

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
	    position = 1;
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
	final ResultSetCollectorImpl resultSetCollector = query.getResultSetCollector();

	if (resultSetCollector.getColumns().length == 0) {
	    final ResultSetMetaData resultSetMetaData = (ResultSetMetaData) invoke;
	    resultSetCollector.setColumnsDetail(resultSetMetaData);
	}
    }

    public void addValueColumn(final Class clazz, final Object[] args, final Object invoke) {
	final ResultSetCollectorImpl resultSetCollector = query.getResultSetCollector();
	if (Integer.class.equals(clazz) || Integer.TYPE.equals(clazz)) {
	    final Integer arg = (Integer) args[0];
	    lastCell = resultSetCollector.addValueColumn(getPosition(), invoke, arg.intValue());
	} else if (String.class.equals(clazz)) {
	    final String arg = (String) args[0];
	    lastCell = resultSetCollector.addValueColumn(getPosition(), invoke, arg);
	}
    }

    public QueryImpl close() {
	final ResultSetCollectorImpl resultSetCollector = query.getResultSetCollector();

	if (!resultSetCollector.isClosed()) {
	    resultSetCollector.close();

	    return query;
	}

	return null;
    }

    private void getMaxValue() {
	position = Integer.MAX_VALUE;
	try {
	    position = rs.getRow();
	} catch (final Throwable e) {
	}
    }

    private int getPosition() {
	if (position == null) {
	    position = 1;
	    try {
		position = rs.getRow();
	    } catch (final Throwable e) {
	    }
	}

	return position;
    }

    private void addPosition(final int addValue) {
	if (position == null) {
	    getPosition();
	} else {
	    position = position + addValue;
	}
    }

    private QueryImpl getQuery() {

	final ResultSetCollectorImpl resultSetCollector = query.getResultSetCollector();
	resultSetCollector.getRow(position);

	if (!resultSetCollector.isClosed()) {
	    return query;
	}
	return null;
    }
}
