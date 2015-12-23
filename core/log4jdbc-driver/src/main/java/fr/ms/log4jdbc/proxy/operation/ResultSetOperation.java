package fr.ms.log4jdbc.proxy.operation;

import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;

import fr.ms.lang.reflect.TimeInvocation;
import fr.ms.log4jdbc.context.jdbc.ConnectionJDBCContext;
import fr.ms.log4jdbc.proxy.operation.factory.ResultSetOperationFactory;
import fr.ms.log4jdbc.resultset.ResultSetCollectorImpl;
import fr.ms.log4jdbc.sql.QueryImpl;

public class ResultSetOperation extends AbstractOperation {

    private final ResultSetOperationFactory context;

    private QueryImpl query;

    private ResultSetCollectorImpl resultSetCollector;

    private ResultSet rs;

    public ResultSetOperation(final ResultSetOperationFactory context, final ConnectionJDBCContext connectionContext, final TimeInvocation timeInvocation,
	    final Object proxy, final Method method, final Object[] args) {
	super(connectionContext, timeInvocation, proxy, method, args);
	this.context = context;
    }

    public void init() {
	this.query = context.query;
	this.rs = context.rs;
	this.resultSetCollector = (ResultSetCollectorImpl) query.getResultSetCollector();
    }

    private void next(final Throwable exception) {
	if (context.position == -1) {
	    try {
		if (exception == null) {
		    context.position = rs.getRow();
		} else {
		    context.position = Integer.MAX_VALUE;
		}
	    } catch (final Throwable e) {
		context.position = Integer.MAX_VALUE;
	    }
	} else {
	    context.position++;
	}

	resultSetCollector.getRow(context.position);

	if (!resultSetCollector.isClosed()) {
	    sqlOperation.setQuery(query);
	}
    }

    private void previous(final Throwable exception) {
	if (context.position == -1) {
	    try {
		if (exception == null) {
		    context.position = rs.getRow();
		} else {
		    context.position = Integer.MAX_VALUE;
		}
	    } catch (final Throwable e) {
		context.position = Integer.MAX_VALUE;
	    }
	} else {
	    context.position--;
	}

	resultSetCollector.getRow(context.position);

	if (!resultSetCollector.isClosed()) {
	    sqlOperation.setQuery(query);
	}
    }

    private void first() {
	context.position = 1;

	resultSetCollector.getRow(context.position);

	if (!resultSetCollector.isClosed()) {
	    sqlOperation.setQuery(query);
	}
    }

    private void last(final Throwable exception) {
	try {
	    if (exception == null) {
		context.position = rs.getRow();
	    } else {
		context.position = Integer.MAX_VALUE;
	    }
	} catch (final Throwable e) {
	    context.position = Integer.MAX_VALUE;
	}

	resultSetCollector.getRow(context.position);

	if (!resultSetCollector.isClosed()) {
	    sqlOperation.setQuery(query);
	}
    }

    private void beforeFirst() {
	context.position = 0;

	if (!resultSetCollector.isClosed()) {
	    sqlOperation.setQuery(query);
	}
    }

    private void afterLast() {
	context.position = -1;

	if (!resultSetCollector.isClosed()) {
	    sqlOperation.setQuery(query);
	}
    }

    private void wasNull() {
	context.lastCell.wasNull();
    }

    private void getMetaData(final Object invoke) {
	if (resultSetCollector.getColumns().length == 0) {
	    final ResultSetMetaData resultSetMetaData = (ResultSetMetaData) invoke;
	    resultSetCollector.setColumnsDetail(resultSetMetaData);
	}
    }

    private void close() {
	sqlOperation.setQuery(query);
	resultSetCollector.close();
    }

    private void get(final Object invoke) {
	final Class arg0Type = method.getParameterTypes()[0];
	if (Integer.class.equals(arg0Type) || Integer.TYPE.equals(arg0Type)) {
	    final Integer arg = (Integer) args[0];
	    context.lastCell = resultSetCollector.addValueColumn(context.position, invoke, arg.intValue());
	} else if (String.class.equals(arg0Type)) {
	    final String arg = (String) args[0];
	    context.lastCell = resultSetCollector.addValueColumn(context.position, invoke, arg);
	}
    }

    public void buildSqlOperation() {

	final Object invoke = timeInvocation.getInvoke();
	final Throwable exception = timeInvocation.getTargetException();
	final String nameMethod = method.getName();

	if (nameMethod.equals("next") && invoke != null && ((Boolean) invoke).booleanValue()) {
	    next(exception);
	} else if (nameMethod.equals("previous") && invoke != null && ((Boolean) invoke).booleanValue()) {
	    previous(exception);
	} else if (nameMethod.equals("first") && invoke != null && ((Boolean) invoke).booleanValue()) {
	    first();
	} else if (nameMethod.equals("last") && invoke != null && ((Boolean) invoke).booleanValue()) {
	    last(exception);
	} else if (nameMethod.equals("beforeFirst")) {
	    beforeFirst();
	} else if (nameMethod.equals("afterLast")) {
	    afterLast();
	} else if (nameMethod.equals("wasNull") && context.lastCell != null && invoke != null && ((Boolean) invoke).booleanValue()) {
	    wasNull();
	} else if (nameMethod.startsWith("getMetaData") && invoke != null) {
	    getMetaData(invoke);
	} else if (nameMethod.startsWith("close") && !resultSetCollector.isClosed()) {
	    close();
	} else if (nameMethod.startsWith("get") && exception == null && args != null && args.length > 0) {
	    get(invoke);
	}
    }
}
