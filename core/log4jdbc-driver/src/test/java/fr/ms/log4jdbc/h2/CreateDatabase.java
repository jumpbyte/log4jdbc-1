package fr.ms.log4jdbc.h2;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class CreateDatabase {

    private final static String URL_H2 = "jdbc:h2:~/test";

    private final static String URL_H2_PROXY = "jdbc:log4" + URL_H2;

    private final static String USER = "SA";

    private final static String PASSWORD = "SA";

    public static final String getURL(final boolean proxy) {
	if (proxy) {
	    return URL_H2_PROXY;
	} else {
	    return URL_H2;
	}
    }

    public static final Driver getDriver(final String url) throws SQLException {

	return DriverManager.getDriver(url);
    }

    public static final Driver getDriver(final boolean proxy) throws SQLException {
	final String url = getURL(proxy);

	return getDriver(url);
    }

    public static final Connection createConnection(final boolean proxy) throws SQLException {
	final String url = getURL(proxy);
	return DriverManager.getConnection(url, USER, PASSWORD);
    }

    public static final void createDatabase(final Connection connection) throws SQLException {

	Statement statement = null;

	try {

	    statement = connection.createStatement();

	    statement.execute("DROP ALL OBJECTS");
	    statement.execute("RUNSCRIPT FROM 'classpath:schema.sql'");
	    statement.execute("RUNSCRIPT FROM 'classpath:data.sql'");
	} finally {
	    if (statement != null) {
		statement.close();
	    }
	}
    }
}
