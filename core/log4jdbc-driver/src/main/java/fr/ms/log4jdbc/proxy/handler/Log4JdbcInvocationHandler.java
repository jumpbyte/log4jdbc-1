package fr.ms.log4jdbc.proxy.handler;

import java.lang.reflect.Method;

import fr.ms.lang.reflect.ProxyOperation;
import fr.ms.lang.reflect.ProxyOperationFactory;
import fr.ms.lang.reflect.ProxyOperationInvocationHandler;
import fr.ms.lang.reflect.TimeInvocation;
import fr.ms.log4jdbc.SqlOperation;
import fr.ms.log4jdbc.SqlOperationDecorator;
import fr.ms.log4jdbc.SqlOperationLogger;
import fr.ms.log4jdbc.sql.FormatQuery;
import fr.ms.log4jdbc.sql.FormatQueryFactory;

public class Log4JdbcInvocationHandler extends ProxyOperationInvocationHandler {

    private final SqlOperationLogger[] logs;

    public Log4JdbcInvocationHandler(final Object implementation, final SqlOperationLogger[] logs, final ProxyOperationFactory factory) {
	super(implementation, factory);
	this.logs = logs;
    }

    public boolean preProcess() {
	final boolean buildOperation = (logs != null && logs.length != 0);
	return buildOperation;
    }

    public void postProcess(final ProxyOperation operationContext, final TimeInvocation timeInvocation, final Object proxy, final Method method,
	    final Object[] args) {
	final Object proxyOperation = operationContext.getOperation();

	final SqlOperation sqlOperation = (SqlOperation) proxyOperation;

	final Object invoke = timeInvocation.getInvoke();
	final Throwable targetException = timeInvocation.getTargetException();
	for (int i = 0; i < logs.length; i++) {
	    final SqlOperationLogger log = logs[i];

	    if (log != null && log.isEnabled()) {
		try {
		    final SqlOperation sqlOperationFormatQuery = getSqlOperation(sqlOperation, log);
		    if (targetException == null) {
			log.buildLog(sqlOperationFormatQuery, method, args, invoke);
		    } else {
			log.buildLog(sqlOperationFormatQuery, method, args, targetException);
		    }
		} catch (final Throwable t) {
		    t.printStackTrace();
		}
	    }
	}
    }

    public static SqlOperation getSqlOperation(final SqlOperation sqlOperation, final SqlOperationLogger log) {
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
