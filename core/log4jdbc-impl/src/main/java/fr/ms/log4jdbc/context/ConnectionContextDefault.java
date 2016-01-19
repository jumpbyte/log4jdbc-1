package fr.ms.log4jdbc.context;

import java.sql.Driver;

import fr.ms.lang.delegate.DefaultStringMakerFactory;
import fr.ms.lang.delegate.DefaultSyncLongFactory;
import fr.ms.lang.delegate.StringMakerFactory;
import fr.ms.lang.delegate.SyncLongFactory;
import fr.ms.lang.stringmaker.impl.StringMaker;
import fr.ms.lang.sync.impl.SyncLong;
import fr.ms.log4jdbc.rdbms.GenericRdbmsSpecifics;
import fr.ms.log4jdbc.rdbms.RdbmsSpecifics;
import fr.ms.log4jdbc.utils.ServicesJDBC;

public class ConnectionContextDefault {

    private final static SyncLongFactory syncLongFactory = DefaultSyncLongFactory.getInstance();

    private final static SyncLong totalConnectionNumber = syncLongFactory.newLong();

    private final static SyncLong openConnection = syncLongFactory.newLong();

    protected long connectionNumber;

    protected Driver driver;

    protected String url;

    protected final RdbmsSpecifics rdbmsSpecifics;

    {
	this.connectionNumber = totalConnectionNumber.incrementAndGet();
	openConnection.incrementAndGet();
    }

    public ConnectionContextDefault(final Class clazz) {
	this.rdbmsSpecifics = getRdbms(clazz);
    }

    public ConnectionContextDefault(final Driver driver, final String url) {
	this.driver = driver;
	this.url = url;
	this.rdbmsSpecifics = getRdbms(driver.getClass());
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

    public Driver getDriver() {
	return driver;
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

	buffer.append("ConnectionContextDefault [driver=");
	buffer.append(driver);
	buffer.append(", url=");
	buffer.append(url);
	buffer.append(", connectionNumber=");
	buffer.append(connectionNumber);
	buffer.append(", rdbmsSpecifics=");
	buffer.append(rdbmsSpecifics);
	buffer.append("]");

	return buffer.toString();
    }

    private final static RdbmsSpecifics getRdbms(final Class driverClass) {
	final String classType = driverClass.getName();
	RdbmsSpecifics rdbmsSpecifics = ServicesJDBC.getRdbmsSpecifics(classType);
	if (rdbmsSpecifics == null) {
	    rdbmsSpecifics = GenericRdbmsSpecifics.getInstance();
	}

	return rdbmsSpecifics;
    }
}
