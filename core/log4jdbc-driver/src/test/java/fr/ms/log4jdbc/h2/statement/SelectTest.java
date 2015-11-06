package fr.ms.log4jdbc.h2.statement;

import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.junit.Test;

import fr.ms.log4jdbc.h2.CreateDatabase;

public class SelectTest {

    @Test
    public void selectTest() throws SQLException {

	Connection connection = null;

	Statement statement = null;

	ResultSet resultSet = null;

	try {
	    connection = CreateDatabase.createConnection(false);

	    CreateDatabase.createDatabase(connection);

	    statement = connection.createStatement();

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
