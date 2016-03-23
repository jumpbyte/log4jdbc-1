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
package fr.ms.log4jdbc.context.xa;

import java.sql.Connection;
import java.sql.Driver;

import fr.ms.log4jdbc.context.Log4JdbcContext;
import fr.ms.log4jdbc.context.jdbc.ConnectionContextJDBC;

/**
 *
 * @see <a href="http://marcosemiao4j.wordpress.com">Marco4J</a>
 *
 *
 * @author Marco Semiao
 *
 */
public class Log4JdbcContextXA implements Log4JdbcContext {

    private ConnectionContextXA connectionContext;

    private TransactionContextXA transactionContext;

    public ConnectionContextJDBC newConnectionContext(final Connection connection, final Class clazz) {
	connectionContext = new ConnectionContextXA(clazz);

	connectionContext.setTransactionContextXA(transactionContext);
	connectionContext.setTransactionEnabled(transactionContext != null);

	return connectionContext;
    }

    public ConnectionContextJDBC newConnectionContext(final Connection connection, final Driver driver, final String url) {
	connectionContext = new ConnectionContextXA(driver, url);

	connectionContext.setTransactionContextXA(transactionContext);
	connectionContext.setTransactionEnabled(transactionContext != null);

	return connectionContext;
    }

    public ConnectionContextXA getConnectionContext() {
	return connectionContext;
    }

    public TransactionContextXA getTransactionContext() {
	return transactionContext;
    }

    public void setTransactionContext(final TransactionContextXA transactionContext) {
	this.transactionContext = transactionContext;
    }
}
