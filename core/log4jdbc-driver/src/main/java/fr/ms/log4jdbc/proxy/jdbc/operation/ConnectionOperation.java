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
package fr.ms.log4jdbc.proxy.jdbc.operation;

import java.lang.reflect.Method;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.Statement;

import fr.ms.lang.reflect.TimeInvocation;
import fr.ms.log4jdbc.SqlOperation;
import fr.ms.log4jdbc.SqlOperationContext;
import fr.ms.log4jdbc.context.jdbc.ConnectionContextJDBC;
import fr.ms.log4jdbc.context.jdbc.TransactionContextJDBC;
import fr.ms.log4jdbc.proxy.Log4JdbcProxy;
import fr.ms.log4jdbc.proxy.handler.Log4JdbcOperation;
import fr.ms.log4jdbc.proxy.jdbc.operation.factory.ConnectionOperationFactory;

/**
 *
 * @see <a href="http://marcosemiao4j.wordpress.com">Marco4J</a>
 *
 *
 * @author Marco Semiao
 *
 */
public class ConnectionOperation implements Log4JdbcOperation {

	private final ConnectionContextJDBC connectionContext;

	private final TimeInvocation timeInvocation;
	private final Method method;
	private final Object[] args;

	private final ConnectionOperationFactory connectionOperationFactory;

	public ConnectionOperation(final ConnectionOperationFactory connectionOperationFactory,
			final ConnectionContextJDBC connectionContext, final TimeInvocation timeInvocation, final Method method,
			final Object[] args) {
		this.connectionContext = connectionContext;
		this.timeInvocation = timeInvocation;
		this.method = method;
		this.args = args;

		this.connectionOperationFactory = connectionOperationFactory;
	}

	public SqlOperation getOperation() {
		final String nameMethod = method.getName();

		if (nameMethod.equals("setAutoCommit")) {
			setAutoCommit(args);
		} else if (nameMethod.equals("commit")) {
			commit();
		} else if (nameMethod.equals("rollback")) {
			rollback(args);
		} else if (nameMethod.equals("setSavepoint")) {
			setSavepoint(timeInvocation.getInvoke());
		} else if (nameMethod.equals("close")) {
			close();
		}

		final SqlOperationContext sqlOperationContext = new SqlOperationContext(timeInvocation, connectionContext);
		return sqlOperationContext;
	}

	private void setAutoCommit(final Object[] args) {
		final boolean autoCommit = ((Boolean) args[0]).booleanValue();
		final boolean commit = connectionOperationFactory.executeAutoCommit(autoCommit);

		connectionContext.setTransactionEnabled(!autoCommit);

		if (commit) {
			commit();
		}
	}

	private void commit() {
		connectionContext.commit();
	}

	private void setSavepoint(final Object savePoint) {
		final TransactionContextJDBC transactionContext = connectionContext.getTransactionContext();
		if (transactionContext != null) {
			transactionContext.setSavePoint(savePoint);
		}
	}

	private void rollback(final Object[] args) {
		Object savePoint = null;
		if (args != null && args[0] != null) {
			savePoint = args[0];
		}
		rollback(savePoint);
	}

	private void rollback(final Object savePoint) {
		connectionContext.rollback(savePoint);
	}

	private void close() {
		connectionContext.close();
	}

	public Object getInvoke() {
		final Object invoke = timeInvocation.getInvoke();
		if (invoke != null) {
			if (invoke instanceof CallableStatement) {
				final CallableStatement callableStatement = (CallableStatement) invoke;
				final String sql = (String) args[0];
				return Log4JdbcProxy.proxyCallableStatement(callableStatement, connectionContext, sql);
			} else if (invoke instanceof PreparedStatement) {
				final PreparedStatement preparedStatement = (PreparedStatement) invoke;
				final String sql = (String) args[0];
				return Log4JdbcProxy.proxyPreparedStatement(preparedStatement, connectionContext, sql);
			} else if (invoke instanceof Statement) {
				final Statement statement = (Statement) invoke;
				return Log4JdbcProxy.proxyStatement(statement, connectionContext);
			}
		}
		return invoke;
	}
}
