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
package fr.ms.log4jdbc.context;

import fr.ms.log4jdbc.context.Transaction;
import fr.ms.log4jdbc.context.internal.TransactionContext;
import fr.ms.log4jdbc.sql.Query;

/**
 *
 * @see <a href="http://marcosemiao4j.wordpress.com">Marco4J</a>
 *
 *
 * @author Marco Semiao
 *
 */
public class TransactionImpl implements Transaction {

    private final TransactionContext transactionContext;

    public TransactionImpl(final TransactionContext transactionContext) {
	this.transactionContext = transactionContext;
    }

    public String getTransactionState() {
	return transactionContext.getState();
    }

    public long getTransactionNumber() {
	return transactionContext.getTransactionNumber();
    }

    public long getOpenTransaction() {
	return transactionContext.getOpenTransaction();
    }

    public Query[] getQueriesTransaction() {
	return transactionContext.getQueriesTransaction();
    }
}
