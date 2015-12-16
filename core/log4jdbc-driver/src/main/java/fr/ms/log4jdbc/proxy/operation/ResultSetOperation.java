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

    public void buildSqlOperation() {

	final Object invoke = timeInvocation.getInvoke();
	final Throwable targetException = timeInvocation.getTargetException();
	final String nameMethod = method.getName();

	final boolean nextMethod = nameMethod.equals("next") && invoke != null && ((Boolean) invoke).booleanValue();
	if (nextMethod) {
	    if (context.position == -1) {
		try {
		    if (targetException == null) {
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

	    return;
	}

	final boolean previousMethod = nameMethod.equals("previous") && invoke != null && ((Boolean) invoke).booleanValue();
	if (previousMethod) {
	    if (context.position == -1) {
		try {
		    if (targetException == null) {
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

	    return;
	}

	final boolean firstMethod = nameMethod.equals("first") && invoke != null && ((Boolean) invoke).booleanValue();
	if (firstMethod) {
	    context.position = 1;

	    resultSetCollector.getRow(context.position);

	    if (!resultSetCollector.isClosed()) {
		sqlOperation.setQuery(query);
	    }

	    return;
	}

	final boolean lastMethod = nameMethod.equals("last") && invoke != null && ((Boolean) invoke).booleanValue();
	if (lastMethod) {
	    try {
		if (targetException == null) {
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

	    return;
	}

	final boolean beforeFirstMethod = nameMethod.equals("beforeFirst");
	if (beforeFirstMethod) {
	    context.position = 0;

	    if (!resultSetCollector.isClosed()) {
		sqlOperation.setQuery(query);
	    }

	    return;
	}

	final boolean afterLastMethod = nameMethod.equals("afterLast");
	if (afterLastMethod) {
	    context.position = -1;

	    if (!resultSetCollector.isClosed()) {
		sqlOperation.setQuery(query);
	    }

	    return;
	}

	final boolean wasNullMethod = nameMethod.equals("wasNull") && context.lastCell != null && invoke != null && ((Boolean) invoke).booleanValue();
	if (wasNullMethod) {
	    context.lastCell.wasNull();
	    return;
	}

	final boolean getMetaDataMethod = nameMethod.startsWith("getMetaData") && invoke != null;
	if (getMetaDataMethod) {

	    if (resultSetCollector.getColumns().length == 0) {
		final ResultSetMetaData resultSetMetaData = (ResultSetMetaData) invoke;
		resultSetCollector.setColumnsDetail(resultSetMetaData);
	    }
	    return;
	}

	final boolean closeMethod = nameMethod.startsWith("close") && !resultSetCollector.isClosed();
	if (closeMethod) {
	    sqlOperation.setQuery(query);
	    resultSetCollector.close();
	    return;
	}

	final boolean getValueColumn = nameMethod.startsWith("get") && targetException == null && args != null && args.length > 0;
	if (getValueColumn) {
	    final Class arg0Type = method.getParameterTypes()[0];
	    if (Integer.class.equals(arg0Type) || Integer.TYPE.equals(arg0Type)) {
		final Integer arg = (Integer) args[0];
		context.lastCell = resultSetCollector.addValueColumn(context.position, invoke, arg.intValue());
	    } else if (String.class.equals(arg0Type)) {
		final String arg = (String) args[0];
		context.lastCell = resultSetCollector.addValueColumn(context.position, invoke, arg);
	    }
	}

	return;
    }
}
