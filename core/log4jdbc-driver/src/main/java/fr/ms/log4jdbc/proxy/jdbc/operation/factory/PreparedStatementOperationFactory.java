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
package fr.ms.log4jdbc.proxy.jdbc.operation.factory;

import java.lang.reflect.Method;
import java.sql.PreparedStatement;

import fr.ms.lang.reflect.ProxyOperation;
import fr.ms.lang.reflect.TimeInvocation;
import fr.ms.log4jdbc.context.jdbc.ConnectionContextJDBC;
import fr.ms.log4jdbc.proxy.jdbc.operation.PreparedStatementOperation;
import fr.ms.log4jdbc.sql.internal.QueryFactory;

/**
 *
 * @see <a href="http://marcosemiao4j.wordpress.com">Marco4J</a>
 *
 *
 * @author Marco Semiao
 *
 */
public class PreparedStatementOperationFactory extends StatementOperationFactory {

    private final String sql;

    public PreparedStatementOperationFactory(final ConnectionContextJDBC connectionContext, final PreparedStatement statement, final QueryFactory queryFactory,
	    final String sql) {
	super(connectionContext, statement, queryFactory);
	this.sql = sql;
	query = queryFactory.newQuery(connectionContext, sql);
    }

    public ProxyOperation newOperation(final TimeInvocation timeInvocation, final Object proxy, final Method method, final Object[] args) {
	final ProxyOperation operation = new PreparedStatementOperation(queryFactory, this, statement, connectionContext, timeInvocation, method, args);

	return operation;
    }

    public void newQuery() {
	query = queryFactory.newQuery(connectionContext, sql, query.getJDBCParameters());
    }
}
