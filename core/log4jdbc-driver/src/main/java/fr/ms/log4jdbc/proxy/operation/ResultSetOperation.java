package fr.ms.log4jdbc.proxy.operation;

import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;

import fr.ms.lang.reflect.TimeInvocation;
import fr.ms.log4jdbc.SqlOperation;
import fr.ms.log4jdbc.SqlOperationImpl;
import fr.ms.log4jdbc.context.internal.ConnectionContext;
import fr.ms.log4jdbc.proxy.handler.Log4JdbcOperation;
import fr.ms.log4jdbc.proxy.operation.factory.ResultSetOperationFactory;
import fr.ms.log4jdbc.resultset.ResultSetCollectorImpl;
import fr.ms.log4jdbc.sql.Query;

public class ResultSetOperation implements Log4JdbcOperation {

    private final ResultSetOperationFactory factory;

    public final Query query;

    private final ResultSetCollectorImpl resultSetCollector;

    private final ResultSet rs;

    private final ConnectionContext connectionContext;
    private final TimeInvocation timeInvocation;
    private final Object proxy;
    private final Method method;
    private final Object[] args;

    private final SqlOperationImpl sqlOperation;

    public ResultSetOperation(final ResultSetOperationFactory factory, final ConnectionContext connectionContext, final TimeInvocation timeInvocation,
	    final Object proxy, final Method method, final Object[] args) {
	this.factory = factory;

	this.query = factory.query;
	this.rs = factory.rs;
	this.resultSetCollector = (ResultSetCollectorImpl) query.getResultSetCollector();

	this.connectionContext = connectionContext;
	this.timeInvocation = timeInvocation;
	this.proxy = proxy;
	this.method = method;
	this.args = args;

	sqlOperation = new SqlOperationImpl(timeInvocation, connectionContext);
    }

    public SqlOperation newSqlOperation() {

	final Object invoke = timeInvocation.getInvoke();
	final Throwable targetException = timeInvocation.getTargetException();
	final String nameMethod = method.getName();

	final boolean nextMethod = nameMethod.equals("next") && invoke != null && ((Boolean) invoke).booleanValue();
	if (nextMethod) {
	    if (factory.position == -1) {
		try {
		    if (targetException == null) {
			factory.position = rs.getRow();
		    } else {
			factory.position = Integer.MAX_VALUE;
		    }
		} catch (final Throwable e) {
		    factory.position = Integer.MAX_VALUE;
		}
	    } else {
		factory.position++;
	    }

	    if (!resultSetCollector.isClosed()) {
		sqlOperation.setQuery(query);
	    }

	    return sqlOperation;
	}

	final boolean previousMethod = nameMethod.equals("previous") && invoke != null && ((Boolean) invoke).booleanValue();
	if (previousMethod) {
	    if (factory.position == -1) {
		try {
		    if (targetException == null) {
			factory.position = rs.getRow();
		    } else {
			factory.position = Integer.MAX_VALUE;
		    }
		} catch (final Throwable e) {
		    factory.position = Integer.MAX_VALUE;
		}
	    } else {
		factory.position--;
	    }

	    if (!resultSetCollector.isClosed()) {
		sqlOperation.setQuery(query);
	    }

	    return sqlOperation;
	}

	final boolean firstMethod = nameMethod.equals("first") && invoke != null && ((Boolean) invoke).booleanValue();
	if (firstMethod) {
	    factory.position = 1;

	    if (!resultSetCollector.isClosed()) {
		sqlOperation.setQuery(query);
	    }

	    return sqlOperation;
	}

	final boolean lastMethod = nameMethod.equals("last") && invoke != null && ((Boolean) invoke).booleanValue();
	if (lastMethod) {
	    try {
		if (targetException == null) {
		    factory.position = rs.getRow();
		} else {
		    factory.position = Integer.MAX_VALUE;
		}
	    } catch (final Throwable e) {
		factory.position = Integer.MAX_VALUE;
	    }

	    if (!resultSetCollector.isClosed()) {
		sqlOperation.setQuery(query);
	    }

	    return sqlOperation;
	}

	final boolean beforeFirstMethod = nameMethod.equals("beforeFirst");
	if (beforeFirstMethod) {
	    factory.position = 0;

	    if (!resultSetCollector.isClosed()) {
		sqlOperation.setQuery(query);
	    }

	    return sqlOperation;
	}

	final boolean afterLastMethod = nameMethod.equals("afterLast");
	if (afterLastMethod) {
	    factory.position = -1;

	    if (!resultSetCollector.isClosed()) {
		sqlOperation.setQuery(query);
	    }

	    return sqlOperation;
	}

	final boolean wasNullMethod = nameMethod.equals("wasNull") && factory.lastCell != null && invoke != null && ((Boolean) invoke).booleanValue();
	if (wasNullMethod) {
	    factory.lastCell.wasNull();
	    return sqlOperation;
	}

	final boolean getMetaDataMethod = nameMethod.startsWith("getMetaData") && invoke != null;
	if (getMetaDataMethod) {

	    if (resultSetCollector.getColumns().length == 0) {
		final ResultSetMetaData resultSetMetaData = (ResultSetMetaData) invoke;
		resultSetCollector.setColumnsDetail(resultSetMetaData);
	    }
	    return sqlOperation;
	}

	final boolean closeMethod = nameMethod.startsWith("close") && !resultSetCollector.isClosed();
	if (closeMethod) {
	    sqlOperation.setQuery(query);
	    resultSetCollector.close();
	    return sqlOperation;
	}

	final boolean getValueColumn = nameMethod.startsWith("get") && targetException == null && args != null && args.length > 0;
	if (getValueColumn) {
	    final Class arg0Type = method.getParameterTypes()[0];
	    if (Integer.class.equals(arg0Type) || Integer.TYPE.equals(arg0Type)) {
		final Integer arg = (Integer) args[0];
		factory.lastCell = resultSetCollector.addValueColumn(factory.position, invoke, arg.intValue());
	    } else if (String.class.equals(arg0Type)) {
		final String arg = (String) args[0];
		factory.lastCell = resultSetCollector.addValueColumn(factory.position, invoke, arg);
	    }
	    return sqlOperation;
	}

	return sqlOperation;
    }

    public Object wrapInvoke() {
	return timeInvocation.getInvoke();
    }
}
