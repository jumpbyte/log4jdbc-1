package fr.ms.log4jdbc.proxy.operation;

import java.lang.reflect.Method;

import fr.ms.lang.reflect.TimeInvocation;
import fr.ms.log4jdbc.SqlOperation;
import fr.ms.log4jdbc.SqlOperationDecorator;
import fr.ms.log4jdbc.SqlOperationImpl;
import fr.ms.log4jdbc.SqlOperationLogger;
import fr.ms.log4jdbc.context.internal.ConnectionContext;
import fr.ms.log4jdbc.proxy.handler.Log4JdbcOperation;
import fr.ms.log4jdbc.sql.FormatQuery;
import fr.ms.log4jdbc.sql.FormatQueryFactory;

public abstract class AbstractOperation implements Log4JdbcOperation {

    protected final ConnectionContext connectionContext;
    protected final TimeInvocation timeInvocation;
    protected final Object proxy;
    protected final Method method;
    protected final Object[] args;

    protected SqlOperationImpl sqlOperation;

    private Object invoke;

    public AbstractOperation(final ConnectionContext connectionContext, final TimeInvocation timeInvocation, final Object proxy, final Method method,
	    final Object[] args) {
	this.connectionContext = connectionContext;
	this.timeInvocation = timeInvocation;
	this.proxy = proxy;
	this.method = method;
	this.args = args;

	this.sqlOperation = new SqlOperationImpl(timeInvocation, connectionContext);
    }

    public void init() {
    }

    public abstract SqlOperationImpl newSqlOperation();

    public Object newResultMethod() {
	return timeInvocation.getInvoke();
    }

    public void buildOperation() {
	init();
	sqlOperation = newSqlOperation();
	invoke = newResultMethod();
    }

    public Object getResultMethod() {
	if (invoke == null) {
	    invoke = newResultMethod();
	}
	return invoke;
    }

    public SqlOperation getSqlOperation(final SqlOperationLogger log) {
	if (log instanceof FormatQueryFactory) {
	    final FormatQueryFactory formatQueryFactory = (FormatQueryFactory) log;

	    final FormatQuery formatQuery = formatQueryFactory.getFormatQuery();

	    if (formatQuery != null) {
		final SqlOperation wrap = new SqlOperationDecorator(sqlOperation, formatQuery);
		return wrap;
	    }
	}

	return sqlOperation;
    }
}
