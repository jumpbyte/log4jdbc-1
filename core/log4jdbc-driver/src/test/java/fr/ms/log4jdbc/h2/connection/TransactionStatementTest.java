package fr.ms.log4jdbc.h2.connection;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import org.h2.Driver;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;

import fr.ms.log4jdbc.SqlOperation;
import fr.ms.log4jdbc.SqlOperationLogger;
import fr.ms.log4jdbc.h2.CreateDatabase;
import fr.ms.log4jdbc.sql.Query;
import fr.ms.log4jdbc.test.sqloperation.SqlMessage;
import fr.ms.log4jdbc.test.sqloperation.SqlOperationMessage;

public class TransactionStatementTest {

    private final SqlMessage messages = SqlMessage.getInstance();

    @After
    public void clear() {
	messages.clear();
    }

    @Test
    public void simpleTransactionTest() throws SQLException {
	Connection connection = null;
	Statement statement = null;
	try {
	    connection = CreateDatabase.createConnection(true);

	    // Set Auto Commit False - Debut Transaction
	    connection.setAutoCommit(false);
	    List<SqlOperationMessage> sqlMessages = messages.getSqlMessages();

	    Assert.assertEquals(1, sqlMessages.size());

	    SqlOperationMessage sqlOperationMessage = sqlMessages.get(0);

	    Assert.assertEquals(SqlOperationLogger.CONNECTION, sqlOperationMessage.getTypeLogger());

	    SqlOperation sqlOperation = sqlOperationMessage.getSqlOperation();
	    Assert.assertEquals(1, sqlOperation.getOpenConnection());
	    Assert.assertEquals(Driver.class, sqlOperation.getDriver().getClass());
	    Assert.assertEquals(CreateDatabase.getURL(false), sqlOperation.getUrl());
	    Assert.assertFalse(sqlOperation.isAutoCommit());
	    Assert.assertNotNull(sqlOperation.getTransaction());
	    Assert.assertNotNull(sqlOperation.getBatch());

	    Query query = sqlOperation.getQuery();
	    Assert.assertNull(query);

	    // create statement
	    statement = connection.createStatement();

	    sqlMessages = messages.getSqlMessages();

	    Assert.assertEquals(1, sqlMessages.size());

	    sqlOperationMessage = sqlMessages.get(0);

	    Assert.assertEquals(SqlOperationLogger.CONNECTION, sqlOperationMessage.getTypeLogger());

	    sqlOperation = sqlOperationMessage.getSqlOperation();
	    Assert.assertEquals(1, sqlOperation.getOpenConnection());
	    Assert.assertEquals(Driver.class, sqlOperation.getDriver().getClass());
	    Assert.assertEquals(CreateDatabase.getURL(false), sqlOperation.getUrl());
	    Assert.assertFalse(sqlOperation.isAutoCommit());
	    Assert.assertNotNull(sqlOperation.getTransaction());
	    Assert.assertNotNull(sqlOperation.getBatch());

	    sqlOperation = sqlOperationMessage.getSqlOperation();
	    Assert.assertFalse(sqlOperation.isAutoCommit());
	    Assert.assertNotNull(sqlOperation.getTransaction());
	    Assert.assertNotNull(sqlOperation.getBatch());

	    query = sqlOperation.getQuery();
	    Assert.assertNull(query);

	    // Execute Query - INSERT
	    statement.execute("INSERT INTO PERSONNE (PRENOM, NOM, DATE_NAISSANCE) VALUES ('Transaction', 'SQL', '1970-01-01');");

	    sqlMessages = messages.getSqlMessages();

	    Assert.assertEquals(1, sqlMessages.size());

	    sqlOperationMessage = sqlMessages.get(0);

	    Assert.assertEquals(SqlOperationLogger.STATEMENT, sqlOperationMessage.getTypeLogger());

	    sqlOperation = sqlOperationMessage.getSqlOperation();
	    Assert.assertEquals(1, sqlOperation.getOpenConnection());
	    Assert.assertEquals(Driver.class, sqlOperation.getDriver().getClass());
	    Assert.assertEquals(CreateDatabase.getURL(false), sqlOperation.getUrl());
	    Assert.assertFalse(sqlOperation.isAutoCommit());
	    Assert.assertNotNull(sqlOperation.getTransaction());
	    Assert.assertNotNull(sqlOperation.getBatch());

	    sqlOperation = sqlOperationMessage.getSqlOperation();
	    Assert.assertFalse(sqlOperation.isAutoCommit());
	    Assert.assertNotNull(sqlOperation.getTransaction());
	    Assert.assertNotNull(sqlOperation.getBatch());

	    query = sqlOperation.getQuery();
	    Assert.assertNotNull(query);

	    // Commit
	    connection.commit();

	    sqlMessages = messages.getSqlMessages();

	    Assert.assertEquals(1, sqlMessages.size());

	    sqlOperationMessage = sqlMessages.get(0);

	    Assert.assertEquals(SqlOperationLogger.CONNECTION, sqlOperationMessage.getTypeLogger());

	    sqlOperation = sqlOperationMessage.getSqlOperation();
	    Assert.assertEquals(1, sqlOperation.getOpenConnection());
	    Assert.assertEquals(Driver.class, sqlOperation.getDriver().getClass());
	    Assert.assertEquals(CreateDatabase.getURL(false), sqlOperation.getUrl());
	    Assert.assertFalse(sqlOperation.isAutoCommit());
	    Assert.assertNotNull(sqlOperation.getTransaction());
	    Assert.assertNotNull(sqlOperation.getBatch());

	    sqlOperation = sqlOperationMessage.getSqlOperation();
	    Assert.assertFalse(sqlOperation.isAutoCommit());
	    Assert.assertNotNull(sqlOperation.getTransaction());
	    Assert.assertNotNull(sqlOperation.getBatch());
	    Assert.assertNotNull(query);
	} finally {

	    if (statement != null) {
		statement.close();
	    }
	    if (connection != null) {
		connection.close();
	    }
	}
    }
}
