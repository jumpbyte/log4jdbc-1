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

import fr.ms.lang.delegate.DefaultStringMakerFactory;
import fr.ms.lang.delegate.DefaultSyncLongFactory;
import fr.ms.lang.delegate.StringMakerFactory;
import fr.ms.lang.delegate.SyncLongFactory;
import fr.ms.lang.stringmaker.impl.StringMaker;
import fr.ms.lang.sync.impl.SyncLong;
import fr.ms.log4jdbc.rdbms.GenericRdbmsSpecifics;
import fr.ms.log4jdbc.rdbms.RdbmsSpecifics;
import fr.ms.log4jdbc.utils.ServicesJDBC;

/**
 *
 * @see <a href="http://marcosemiao4j.wordpress.com">Marco4J</a>
 *
 *
 * @author Marco Semiao
 *
 */
public class ConnectionContextDefault {

    private final static SyncLongFactory syncLongFactory = DefaultSyncLongFactory.getInstance();

    private final static SyncLong totalConnectionNumber = syncLongFactory.newLong();

    private final static SyncLong openConnection = syncLongFactory.newLong();

    protected long connectionNumber;

    protected String driverName;

    protected String url;

    protected final RdbmsSpecifics rdbmsSpecifics;

    {
	this.connectionNumber = totalConnectionNumber.incrementAndGet();
	openConnection.incrementAndGet();
    }

    public ConnectionContextDefault(final Class clazz, final String url) {
	driverName = clazz.getName();
	this.rdbmsSpecifics = getRdbms(driverName);
	this.url = url;
    }

    public void close() {
	openConnection.decrementAndGet();
    }

    public long getConnectionNumber() {
	return connectionNumber;
    }

    public SyncLong getTotalConnectionNumber() {
	return totalConnectionNumber;
    }

    public SyncLong getOpenConnection() {
	return openConnection;
    }

    public String getDriverName() {
	return driverName;
    }

    public String getUrl() {
	return url;
    }

    public RdbmsSpecifics getRdbmsSpecifics() {
	return rdbmsSpecifics;
    }

    public String toString() {
	final StringMakerFactory stringFactory = DefaultStringMakerFactory.getInstance();
	final StringMaker buffer = stringFactory.newString();

	buffer.append("ConnectionContextDefault [driverName=");
	buffer.append(driverName);
	buffer.append(", url=");
	buffer.append(url);
	buffer.append(", connectionNumber=");
	buffer.append(connectionNumber);
	buffer.append(", rdbmsSpecifics=");
	buffer.append(rdbmsSpecifics);
	buffer.append("]");

	return buffer.toString();
    }

    private final static RdbmsSpecifics getRdbms(final String driverClass) {
	RdbmsSpecifics rdbmsSpecifics = ServicesJDBC.getRdbmsSpecifics(driverClass);
	if (rdbmsSpecifics == null) {
	    rdbmsSpecifics = GenericRdbmsSpecifics.getInstance();
	}

	return rdbmsSpecifics;
    }
}
