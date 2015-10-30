package fr.ms.log4jdbc.h2;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.junit.Test;

public class TestH2 {

    private final static String URL = "jdbc:h2:~/test";

    private final static String USER = "SA";

    private final static String PASSWORD = "SA";

    @Test
    public void testH2() throws SQLException {
	Connection connection = null;

	Statement statement = null;

	ResultSet resultSet = null;

	try {
	    connection = DriverManager.getConnection(URL, USER, PASSWORD);

	    statement = connection.createStatement();

	    statement.execute("DROP ALL OBJECTS");
	    statement.execute("RUNSCRIPT FROM 'classpath:schema.sql'");
	    statement.execute("RUNSCRIPT FROM 'classpath:data.sql'");

	    resultSet = statement.executeQuery("SELECT * FROM PERSONNE");

	    while (resultSet.next()) {
		final int id = resultSet.getInt(1);
		final String nom = resultSet.getString(2);
		final String prenom = resultSet.getString(3);
		final Date date = resultSet.getDate(4);

		System.out.println(id);
		System.out.println(nom);
		System.out.println(prenom);
		System.out.println(date);
	    }
	} finally {
	    if (resultSet != null) {
		resultSet.close();
	    }

	    if (statement != null) {
		statement.close();
	    }

	    if (connection != null) {
		connection.close();
	    }
	}
    }
}
