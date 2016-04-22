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
package fr.ms.log4jdbc.context.jdbc;

import fr.ms.lang.delegate.DefaultStringMakerFactory;
import fr.ms.lang.delegate.StringMakerFactory;
import fr.ms.lang.stringmaker.impl.StringMaker;
import fr.ms.log4jdbc.context.ConnectionContextDefault;
import fr.ms.log4jdbc.sql.QueryImpl;

/**
 *
 * @see <a href="http://marcosemiao4j.wordpress.com">Marco4J</a>
 *
 *
 * @author Marco Semiao
 *
 */
public class ConnectionContextJDBC extends ConnectionContextDefault {

    protected boolean transactionEnabled;

    private TransactionContextJDBC transactionContextJDBC;

    public ConnectionContextJDBC(final Class clazz, final String url) {
	super(clazz, url);
    }

    public boolean isTransactionEnabled() {
	return transactionEnabled;
    }

    public void setTransactionEnabled(final boolean transactionEnabled) {
	if (!transactionEnabled) {
	    transactionContextJDBC = null;
	}

	this.transactionEnabled = transactionEnabled;
    }

    public QueryImpl addQuery(final QueryImpl query) {
	if (transactionEnabled) {
	    if (transactionContextJDBC == null) {
		transactionContextJDBC = new TransactionContextJDBC();
	    }
	    transactionContextJDBC.addQuery(query);
	}

	return query;
    }

    public TransactionContextJDBC getTransactionContext() {
	return transactionContextJDBC;
    }

    public void commit() {
	if (transactionContextJDBC != null) {
	    transactionContextJDBC.commit();
	}
    }

    public void rollback(final Object savePoint) {
	if (transactionContextJDBC != null) {
	    transactionContextJDBC.rollback(savePoint);
	}
    }

    public void resetTransaction() {
	if (transactionContextJDBC != null) {
	    transactionContextJDBC.close();
	    transactionContextJDBC = null;
	}
    }

    public String toString() {
	final StringMakerFactory stringFactory = DefaultStringMakerFactory.getInstance();
	final StringMaker buffer = stringFactory.newString();

	buffer.append("ConnectionContextJDBC [driverName=");
	buffer.append(driverName);
	buffer.append(", url=");
	buffer.append(url);
	buffer.append(", connectionNumber=");
	buffer.append(connectionNumber);
	buffer.append(", rdbmsSpecifics=");
	buffer.append(rdbmsSpecifics);
	buffer.append(", transactionContext=");
	buffer.append(transactionContextJDBC);
	buffer.append("]");

	return buffer.toString();
    }
}
