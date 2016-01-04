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
package fr.ms.log4jdbc.proxy;

import java.lang.reflect.InvocationHandler;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import fr.ms.lang.SystemPropertyUtils;
import fr.ms.lang.reflect.ProxyUtils;
import fr.ms.lang.reflect.TraceTimeInvocationHandler;
import fr.ms.log4jdbc.SqlOperationLogger;
import fr.ms.log4jdbc.context.ConnectionContext;
import fr.ms.log4jdbc.context.Log4JdbcContext;
import fr.ms.log4jdbc.proxy.handler.Log4JdbcInvocationHandler;
import fr.ms.log4jdbc.proxy.handler.Log4JdbcOperationFactory;
import fr.ms.log4jdbc.proxy.handler.TraceTimeInvocationOperationFactory;
import fr.ms.log4jdbc.proxy.operation.factory.CallableStatementOperationFactory;
import fr.ms.log4jdbc.proxy.operation.factory.ConnectionOperationFactory;
import fr.ms.log4jdbc.proxy.operation.factory.PreparedStatementOperationFactory;
import fr.ms.log4jdbc.proxy.operation.factory.ResultSetOperationFactory;
import fr.ms.log4jdbc.proxy.operation.factory.StatementOperationFactory;
import fr.ms.log4jdbc.sql.QueryImpl;
import fr.ms.log4jdbc.utils.ServicesJDBC;

/**
 *
 * @see <a href="http://marcosemiao4j.wordpress.com">Marco4J</a>
 *
 *
 * @author Marco Semiao
 *
 */
public final class Log4JdbcProxy {

    private final static boolean devMode = SystemPropertyUtils.getProperty("log4jdbc.devMode", false);

    public static Connection proxyConnection(final Connection connection, final Log4JdbcContext log4JdbcContext, final Driver driver, final String url) {
	final ConnectionContext connectionContext = log4JdbcContext.newConnectionContext(driver, url);
	final Connection wrap = proxyConnection(connection, connectionContext);

	return wrap;
    }

    public static Connection proxyConnection(final Connection connection, final Log4JdbcContext log4JdbcContext, final Class clazz) {
	final ConnectionContext connectionContext = log4JdbcContext.newConnectionContext(clazz);
	final Connection wrap = proxyConnection(connection, connectionContext);

	return wrap;
    }

    private static Connection proxyConnection(final Connection connection, final ConnectionContext connectionContext) {
	final SqlOperationLogger[] logs = ServicesJDBC.getMessageLogger(SqlOperationLogger.CONNECTION);

	final Log4JdbcOperationFactory factory = new ConnectionOperationFactory(connectionContext, connection);

	final InvocationHandler handler = createHandler(connection, logs, factory);

	final Connection instance = (Connection) ProxyUtils.newProxyInstance(connection, handler);

	return instance;
    }

    public static Statement proxyStatement(final Statement statement, final ConnectionContext connectionContext) {
	final SqlOperationLogger[] logs = ServicesJDBC.getMessageLogger(SqlOperationLogger.STATEMENT);

	final Log4JdbcOperationFactory factory = new StatementOperationFactory(connectionContext, statement);

	final InvocationHandler handler = createHandler(statement, logs, factory);

	final Statement instance = (Statement) ProxyUtils.newProxyInstance(statement, handler);

	return instance;
    }

    public static PreparedStatement proxyPreparedStatement(final PreparedStatement statement, final ConnectionContext connectionContext, final String sql) {
	final SqlOperationLogger[] logs = ServicesJDBC.getMessageLogger(SqlOperationLogger.PREPARED_STATEMENT);

	final Log4JdbcOperationFactory factory = new PreparedStatementOperationFactory(connectionContext, statement, sql);

	final InvocationHandler handler = createHandler(statement, logs, factory);

	final PreparedStatement instance = (PreparedStatement) ProxyUtils.newProxyInstance(statement, handler);

	return instance;
    }

    public static CallableStatement proxyCallableStatement(final CallableStatement statement, final ConnectionContext connectionContext, final String sql) {
	final SqlOperationLogger[] logs = ServicesJDBC.getMessageLogger(SqlOperationLogger.CALLABLE_STATEMENT);

	final Log4JdbcOperationFactory factory = new CallableStatementOperationFactory(connectionContext, statement, sql);

	final InvocationHandler handler = createHandler(statement, logs, factory);

	final CallableStatement instance = (CallableStatement) ProxyUtils.newProxyInstance(statement, handler);

	return instance;
    }

    public static ResultSet proxyResultSet(final ResultSet resultSet, final ConnectionContext connectionContext, final QueryImpl query) {
	final SqlOperationLogger[] logs = ServicesJDBC.getMessageLogger(SqlOperationLogger.RESULT_SET);

	final Log4JdbcOperationFactory factory = new ResultSetOperationFactory(connectionContext, resultSet, query);

	final InvocationHandler handler = createHandler(resultSet, logs, factory);

	final ResultSet instance = (ResultSet) ProxyUtils.newProxyInstance(resultSet, handler);

	return instance;
    }

    private static final InvocationHandler createHandler(final Object implementation, final SqlOperationLogger[] logs, Log4JdbcOperationFactory factory) {
	if (devMode) {
	    factory = new TraceTimeInvocationOperationFactory(factory);
	    final InvocationHandler handler = new Log4JdbcInvocationHandler(implementation, logs, factory);
	    final InvocationHandler decorator = new TraceTimeInvocationHandler(handler);
	    return decorator;
	} else {
	    final InvocationHandler handler = new Log4JdbcInvocationHandler(implementation, logs, factory);
	    return handler;
	}
    }
}
