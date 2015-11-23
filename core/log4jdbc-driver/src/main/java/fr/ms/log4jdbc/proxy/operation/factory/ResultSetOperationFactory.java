package fr.ms.log4jdbc.proxy.operation.factory;

import java.lang.reflect.Method;
import java.sql.ResultSet;

import fr.ms.lang.reflect.TimeInvocation;
import fr.ms.log4jdbc.context.internal.ConnectionContext;
import fr.ms.log4jdbc.proxy.handler.Log4JdbcOperation;
import fr.ms.log4jdbc.proxy.handler.Log4JdbcOperationFactory;
import fr.ms.log4jdbc.proxy.operation.ResultSetOperation;
import fr.ms.log4jdbc.resultset.CellImpl;
import fr.ms.log4jdbc.sql.QueryImpl;

public class ResultSetOperationFactory implements Log4JdbcOperationFactory {

    public final QueryImpl query;

    public final ResultSet rs;

    public int position = 0;

    public CellImpl lastCell;

    public ResultSetOperationFactory(final ResultSet rs, final QueryImpl query) {
	this.rs = rs;
	this.query = query;
    }

    public Log4JdbcOperation newLog4JdbcOperation(final ConnectionContext connectionContext, final TimeInvocation timeInvocation, final Object proxy,
	    final Method method, final Object[] args) {

	final Log4JdbcOperation operation = new ResultSetOperation(this, connectionContext, timeInvocation, proxy, method, args);

	return operation;
    }

    public int getPosition() {
	return position;
    }

    public void setPosition(final int position) {
	this.position = position;
    }
}
