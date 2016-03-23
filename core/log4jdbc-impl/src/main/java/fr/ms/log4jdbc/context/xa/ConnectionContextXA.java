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

import java.sql.Driver;

import fr.ms.log4jdbc.context.jdbc.ConnectionContextJDBC;
import fr.ms.log4jdbc.context.jdbc.TransactionContextJDBC;
import fr.ms.log4jdbc.sql.QueryImpl;

/**
 *
 * @see <a href="http://marcosemiao4j.wordpress.com">Marco4J</a>
 *
 *
 * @author Marco Semiao
 *
 */
public class ConnectionContextXA extends ConnectionContextJDBC {

    private boolean transactionActive = false;

    private TransactionContextXA transactionContextXA;

    public ConnectionContextXA(final Class clazz) {
	super(clazz);
    }

    public ConnectionContextXA(final Driver driver, final String url) {
	super(driver, url);
    }

    public void setTransactionEnabled(final boolean transactionEnabled) {
	if (!transactionEnabled) {
	    transactionContextXA = null;
	    transactionActive = false;
	}
	super.setTransactionEnabled(transactionEnabled);
    }

    public QueryImpl addQuery(final QueryImpl query) {
	if (transactionContextXA == null) {
	    return super.addQuery(query);
	}

	if (transactionEnabled) {
	    transactionActive = true;
	    transactionContextXA.addQuery(query);
	}

	return query;
    }

    public void setTransactionContextXA(final TransactionContextXA transactionContextXA) {
	this.transactionContextXA = transactionContextXA;
	setTransactionEnabled(this.transactionContextXA != null);
    }

    public TransactionContextJDBC getTransactionContext() {
	if (transactionContextXA == null) {
	    return super.getTransactionContext();
	}
	if (transactionActive) {
	    return transactionContextXA;
	}

	return null;
    }

    public void commit() {
	if (transactionContextXA == null) {
	    super.commit();
	} else {
	    transactionContextXA.commit();
	}

    }

    public void rollback(final Object savePoint) {
	if (transactionContextXA == null) {
	    super.rollback(savePoint);
	} else {
	    transactionContextXA.rollback(null);
	}
    }

    public void resetTransaction() {
	transactionContextXA.close();
	setTransactionEnabled(false);
    }
}
