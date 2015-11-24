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
import fr.ms.log4jdbc.rdbms.GenericRdbmsSpecifics;
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
	    Assert.assertEquals(false, sqlOperationMessage.getArgs()[0]);
	    Assert.assertNotNull(sqlOperationMessage.getMethod());
	    Assert.assertNull(sqlOperationMessage.getInvoke());
	    Assert.assertNull(sqlOperationMessage.getException());

	    SqlOperation sqlOperation = sqlOperationMessage.getSqlOperation();
	    Assert.assertTrue(sqlOperation.getConnectionNumber() >= sqlOperation.getOpenConnection());
	    Assert.assertEquals(1, sqlOperation.getOpenConnection());
	    Assert.assertNotNull(sqlOperation.getDate());
	    Assert.assertNotNull(sqlOperation.getExecTime());
	    Assert.assertEquals(Driver.class, sqlOperation.getDriver().getClass());
	    Assert.assertEquals(GenericRdbmsSpecifics.class, sqlOperation.getRdbms().getClass());
	    Assert.assertEquals(CreateDatabase.getURL(false), sqlOperation.getUrl());
	    Assert.assertFalse(sqlOperation.isAutoCommit());
	    Assert.assertNull(sqlOperation.getTransaction());
	    Assert.assertNull(sqlOperation.getBatch());

	    Query query = sqlOperation.getQuery();
	    Assert.assertNull(query);

	    // create statement
	    statement = connection.createStatement();

	    sqlMessages = messages.getSqlMessages();

	    Assert.assertEquals(1, sqlMessages.size());

	    sqlOperationMessage = sqlMessages.get(0);

	    Assert.assertEquals(SqlOperationLogger.CONNECTION, sqlOperationMessage.getTypeLogger());
	    Assert.assertNull(sqlOperationMessage.getArgs());
	    Assert.assertNotNull(sqlOperationMessage.getMethod());
	    Assert.assertNotNull(sqlOperationMessage.getInvoke());
	    Assert.assertTrue(sqlOperationMessage.getInvoke() instanceof Statement);
	    Assert.assertNull(sqlOperationMessage.getException());

	    sqlOperation = sqlOperationMessage.getSqlOperation();
	    Assert.assertTrue(sqlOperation.getConnectionNumber() >= sqlOperation.getOpenConnection());
	    Assert.assertEquals(1, sqlOperation.getOpenConnection());
	    Assert.assertNotNull(sqlOperation.getDate());
	    Assert.assertNotNull(sqlOperation.getExecTime());
	    Assert.assertEquals(Driver.class, sqlOperation.getDriver().getClass());
	    Assert.assertEquals(GenericRdbmsSpecifics.class, sqlOperation.getRdbms().getClass());
	    Assert.assertEquals(CreateDatabase.getURL(false), sqlOperation.getUrl());
	    Assert.assertFalse(sqlOperation.isAutoCommit());
	    Assert.assertNull(sqlOperation.getTransaction());
	    Assert.assertNull(sqlOperation.getBatch());

	    query = sqlOperation.getQuery();
	    Assert.assertNull(query);

	    // Execute Query - INSERT
	    statement.execute("INSERT INTO PERSONNE (PRENOM, NOM, DATE_NAISSANCE) VALUES ('Transaction', 'SQL', '1970-01-01');");

	    sqlMessages = messages.getSqlMessages();

	    Assert.assertEquals(1, sqlMessages.size());

	    sqlOperationMessage = sqlMessages.get(0);

	    Assert.assertEquals(SqlOperationLogger.STATEMENT, sqlOperationMessage.getTypeLogger());
	    Assert.assertEquals("INSERT INTO PERSONNE (PRENOM, NOM, DATE_NAISSANCE) VALUES ('Transaction', 'SQL', '1970-01-01');",
		    sqlOperationMessage.getArgs()[0]);
	    Assert.assertNotNull(sqlOperationMessage.getMethod());
	    Assert.assertEquals(false, sqlOperationMessage.getInvoke());
	    Assert.assertNull(sqlOperationMessage.getException());

	    sqlOperation = sqlOperationMessage.getSqlOperation();
	    Assert.assertTrue(sqlOperation.getConnectionNumber() >= sqlOperation.getOpenConnection());
	    Assert.assertEquals(1, sqlOperation.getOpenConnection());
	    Assert.assertNotNull(sqlOperation.getDate());
	    Assert.assertNotNull(sqlOperation.getExecTime());
	    Assert.assertEquals(Driver.class, sqlOperation.getDriver().getClass());
	    Assert.assertEquals(GenericRdbmsSpecifics.class, sqlOperation.getRdbms().getClass());
	    Assert.assertEquals(CreateDatabase.getURL(false), sqlOperation.getUrl());
	    Assert.assertFalse(sqlOperation.isAutoCommit());
	    Assert.assertNotNull(sqlOperation.getTransaction());
	    Assert.assertNull(sqlOperation.getBatch());

	    query = sqlOperation.getQuery();
	    Assert.assertNotNull(query);
	    Assert.assertNotNull(query.getDate());
	    Assert.assertNotNull(query.getExecTime());
	    Assert.assertEquals(query.getJDBCQuery(), "INSERT INTO PERSONNE (PRENOM, NOM, DATE_NAISSANCE) VALUES ('Transaction', 'SQL', '1970-01-01');");
	    Assert.assertEquals(query.getJDBCParameters().size(), 0);
	    Assert.assertEquals(query.getSQLQuery(), "INSERT INTO PERSONNE (PRENOM, NOM, DATE_NAISSANCE) VALUES ('Transaction', 'SQL', '1970-01-01');");
	    Assert.assertEquals(query.getMethodQuery(), Query.METHOD_EXECUTE);
	    Assert.assertEquals(query.getState(), Query.STATE_EXECUTE);
	    Assert.assertNull(query.getResultSetCollector());
	    Assert.assertNotNull(query.getTransaction());
	    Assert.assertEquals(query.getTransaction().getTransactionState(), Query.STATE_EXECUTE);
	    Assert.assertEquals(query.getTransaction().getOpenTransaction(), 1);
	    Assert.assertEquals(query.getTransaction().getQueriesTransaction().length, 1);
	    Query transactionQuery = query.getTransaction().getQueriesTransaction()[0];

	    Assert.assertEquals(transactionQuery, query);

	    // Commit - Fin de la Transaction
	    connection.commit();

	    sqlMessages = messages.getSqlMessages();

	    Assert.assertEquals(1, sqlMessages.size());

	    sqlOperationMessage = sqlMessages.get(0);

	    Assert.assertEquals(SqlOperationLogger.CONNECTION, sqlOperationMessage.getTypeLogger());
	    Assert.assertNull(sqlOperationMessage.getArgs());
	    Assert.assertNotNull(sqlOperationMessage.getMethod());
	    Assert.assertNull(sqlOperationMessage.getInvoke());
	    Assert.assertNull(sqlOperationMessage.getException());

	    sqlOperation = sqlOperationMessage.getSqlOperation();
	    Assert.assertTrue(sqlOperation.getConnectionNumber() >= sqlOperation.getOpenConnection());
	    Assert.assertEquals(1, sqlOperation.getOpenConnection());
	    Assert.assertNotNull(sqlOperation.getDate());
	    Assert.assertNotNull(sqlOperation.getExecTime());
	    Assert.assertEquals(Driver.class, sqlOperation.getDriver().getClass());
	    Assert.assertEquals(GenericRdbmsSpecifics.class, sqlOperation.getRdbms().getClass());
	    Assert.assertEquals(CreateDatabase.getURL(false), sqlOperation.getUrl());
	    Assert.assertFalse(sqlOperation.isAutoCommit());
	    Assert.assertNotNull(sqlOperation.getTransaction());
	    Assert.assertEquals(query.getTransaction().getTransactionState(), Query.STATE_COMMIT);
	    Assert.assertEquals(query.getTransaction().getOpenTransaction(), 0);
	    Assert.assertEquals(query.getTransaction().getQueriesTransaction().length, 1);
	    transactionQuery = query.getTransaction().getQueriesTransaction()[0];

	    Assert.assertNotNull(transactionQuery.getDate());
	    Assert.assertNotNull(transactionQuery.getExecTime());
	    Assert.assertEquals(transactionQuery.getJDBCQuery(),
		    "INSERT INTO PERSONNE (PRENOM, NOM, DATE_NAISSANCE) VALUES ('Transaction', 'SQL', '1970-01-01');");
	    Assert.assertEquals(transactionQuery.getJDBCParameters().size(), 0);
	    Assert.assertEquals(transactionQuery.getSQLQuery(),
		    "INSERT INTO PERSONNE (PRENOM, NOM, DATE_NAISSANCE) VALUES ('Transaction', 'SQL', '1970-01-01');");
	    Assert.assertEquals(transactionQuery.getMethodQuery(), Query.METHOD_EXECUTE);
	    Assert.assertEquals(transactionQuery.getState(), Query.STATE_COMMIT);
	    Assert.assertNull(transactionQuery.getResultSetCollector());

	    Assert.assertNull(sqlOperation.getBatch());

	    query = sqlOperation.getQuery();
	    Assert.assertNull(query);
	} finally {

	    if (statement != null) {
		statement.close();
	    }
	    if (connection != null) {
		connection.close();
	    }
	}
    }

    @Test
    public void plusieursRequeteTransactionTest() throws SQLException {
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
	    Assert.assertEquals(false, sqlOperationMessage.getArgs()[0]);
	    Assert.assertNotNull(sqlOperationMessage.getMethod());
	    Assert.assertNull(sqlOperationMessage.getInvoke());
	    Assert.assertNull(sqlOperationMessage.getException());

	    SqlOperation sqlOperation = sqlOperationMessage.getSqlOperation();
	    Assert.assertTrue(sqlOperation.getConnectionNumber() >= sqlOperation.getOpenConnection());
	    Assert.assertEquals(1, sqlOperation.getOpenConnection());
	    Assert.assertNotNull(sqlOperation.getDate());
	    Assert.assertNotNull(sqlOperation.getExecTime());
	    Assert.assertEquals(Driver.class, sqlOperation.getDriver().getClass());
	    Assert.assertEquals(GenericRdbmsSpecifics.class, sqlOperation.getRdbms().getClass());
	    Assert.assertEquals(CreateDatabase.getURL(false), sqlOperation.getUrl());
	    Assert.assertFalse(sqlOperation.isAutoCommit());
	    Assert.assertNull(sqlOperation.getTransaction());
	    Assert.assertNull(sqlOperation.getBatch());

	    Query query = sqlOperation.getQuery();
	    Assert.assertNull(query);

	    // create statement
	    statement = connection.createStatement();

	    sqlMessages = messages.getSqlMessages();

	    Assert.assertEquals(1, sqlMessages.size());

	    sqlOperationMessage = sqlMessages.get(0);

	    Assert.assertEquals(SqlOperationLogger.CONNECTION, sqlOperationMessage.getTypeLogger());
	    Assert.assertNull(sqlOperationMessage.getArgs());
	    Assert.assertNotNull(sqlOperationMessage.getMethod());
	    Assert.assertNotNull(sqlOperationMessage.getInvoke());
	    Assert.assertTrue(sqlOperationMessage.getInvoke() instanceof Statement);
	    Assert.assertNull(sqlOperationMessage.getException());

	    sqlOperation = sqlOperationMessage.getSqlOperation();
	    Assert.assertTrue(sqlOperation.getConnectionNumber() >= sqlOperation.getOpenConnection());
	    Assert.assertEquals(1, sqlOperation.getOpenConnection());
	    Assert.assertNotNull(sqlOperation.getDate());
	    Assert.assertNotNull(sqlOperation.getExecTime());
	    Assert.assertEquals(Driver.class, sqlOperation.getDriver().getClass());
	    Assert.assertEquals(GenericRdbmsSpecifics.class, sqlOperation.getRdbms().getClass());
	    Assert.assertEquals(CreateDatabase.getURL(false), sqlOperation.getUrl());
	    Assert.assertFalse(sqlOperation.isAutoCommit());
	    Assert.assertNull(sqlOperation.getTransaction());
	    Assert.assertNull(sqlOperation.getBatch());

	    query = sqlOperation.getQuery();
	    Assert.assertNull(query);

	    // Execute Query - INSERT
	    statement.execute("INSERT INTO PERSONNE (PRENOM, NOM, DATE_NAISSANCE) VALUES ('Transaction', 'SQL', '1970-01-01');");

	    sqlMessages = messages.getSqlMessages();

	    Assert.assertEquals(1, sqlMessages.size());

	    sqlOperationMessage = sqlMessages.get(0);

	    Assert.assertEquals(SqlOperationLogger.STATEMENT, sqlOperationMessage.getTypeLogger());
	    Assert.assertEquals("INSERT INTO PERSONNE (PRENOM, NOM, DATE_NAISSANCE) VALUES ('Transaction', 'SQL', '1970-01-01');",
		    sqlOperationMessage.getArgs()[0]);
	    Assert.assertNotNull(sqlOperationMessage.getMethod());
	    Assert.assertEquals(false, sqlOperationMessage.getInvoke());
	    Assert.assertNull(sqlOperationMessage.getException());

	    sqlOperation = sqlOperationMessage.getSqlOperation();
	    Assert.assertTrue(sqlOperation.getConnectionNumber() >= sqlOperation.getOpenConnection());
	    Assert.assertEquals(1, sqlOperation.getOpenConnection());
	    Assert.assertNotNull(sqlOperation.getDate());
	    Assert.assertNotNull(sqlOperation.getExecTime());
	    Assert.assertEquals(Driver.class, sqlOperation.getDriver().getClass());
	    Assert.assertEquals(GenericRdbmsSpecifics.class, sqlOperation.getRdbms().getClass());
	    Assert.assertEquals(CreateDatabase.getURL(false), sqlOperation.getUrl());
	    Assert.assertFalse(sqlOperation.isAutoCommit());
	    Assert.assertNotNull(sqlOperation.getTransaction());
	    Assert.assertNull(sqlOperation.getBatch());

	    final Query query1 = sqlOperation.getQuery();
	    Assert.assertNotNull(query1);
	    Assert.assertNotNull(query1.getDate());
	    Assert.assertNotNull(query1.getExecTime());
	    Assert.assertEquals(query1.getJDBCQuery(), "INSERT INTO PERSONNE (PRENOM, NOM, DATE_NAISSANCE) VALUES ('Transaction', 'SQL', '1970-01-01');");
	    Assert.assertEquals(query1.getJDBCParameters().size(), 0);
	    Assert.assertEquals(query1.getSQLQuery(), "INSERT INTO PERSONNE (PRENOM, NOM, DATE_NAISSANCE) VALUES ('Transaction', 'SQL', '1970-01-01');");
	    Assert.assertEquals(query1.getMethodQuery(), Query.METHOD_EXECUTE);
	    Assert.assertEquals(query1.getState(), Query.STATE_EXECUTE);
	    Assert.assertNull(query1.getResultSetCollector());
	    Assert.assertNotNull(query1.getTransaction());
	    Assert.assertEquals(query1.getTransaction().getTransactionState(), Query.STATE_EXECUTE);
	    Assert.assertEquals(query1.getTransaction().getOpenTransaction(), 1);
	    Assert.assertEquals(query1.getTransaction().getQueriesTransaction().length, 1);
	    Query transactionQuery1 = query1.getTransaction().getQueriesTransaction()[0];

	    Assert.assertEquals(transactionQuery1, query1);

	    // Execute Query - INSERT 2
	    statement.execute("INSERT INTO PERSONNE (PRENOM, NOM, DATE_NAISSANCE) VALUES ('Transaction2', 'SQL2', '1970-01-01');");

	    sqlMessages = messages.getSqlMessages();

	    Assert.assertEquals(1, sqlMessages.size());

	    sqlOperationMessage = sqlMessages.get(0);

	    Assert.assertEquals(SqlOperationLogger.STATEMENT, sqlOperationMessage.getTypeLogger());
	    Assert.assertEquals("INSERT INTO PERSONNE (PRENOM, NOM, DATE_NAISSANCE) VALUES ('Transaction2', 'SQL2', '1970-01-01');",
		    sqlOperationMessage.getArgs()[0]);
	    Assert.assertNotNull(sqlOperationMessage.getMethod());
	    Assert.assertEquals(false, sqlOperationMessage.getInvoke());
	    Assert.assertNull(sqlOperationMessage.getException());

	    sqlOperation = sqlOperationMessage.getSqlOperation();
	    Assert.assertTrue(sqlOperation.getConnectionNumber() >= sqlOperation.getOpenConnection());
	    Assert.assertEquals(1, sqlOperation.getOpenConnection());
	    Assert.assertNotNull(sqlOperation.getDate());
	    Assert.assertNotNull(sqlOperation.getExecTime());
	    Assert.assertEquals(Driver.class, sqlOperation.getDriver().getClass());
	    Assert.assertEquals(GenericRdbmsSpecifics.class, sqlOperation.getRdbms().getClass());
	    Assert.assertEquals(CreateDatabase.getURL(false), sqlOperation.getUrl());
	    Assert.assertFalse(sqlOperation.isAutoCommit());
	    Assert.assertNotNull(sqlOperation.getTransaction());
	    Assert.assertNull(sqlOperation.getBatch());

	    final Query query2 = sqlOperation.getQuery();
	    Assert.assertNotNull(query2);
	    Assert.assertNotNull(query2.getDate());
	    Assert.assertNotNull(query2.getExecTime());
	    Assert.assertEquals(query2.getJDBCQuery(), "INSERT INTO PERSONNE (PRENOM, NOM, DATE_NAISSANCE) VALUES ('Transaction2', 'SQL2', '1970-01-01');");
	    Assert.assertEquals(query2.getJDBCParameters().size(), 0);
	    Assert.assertEquals(query2.getSQLQuery(), "INSERT INTO PERSONNE (PRENOM, NOM, DATE_NAISSANCE) VALUES ('Transaction2', 'SQL2', '1970-01-01');");
	    Assert.assertEquals(query2.getMethodQuery(), Query.METHOD_EXECUTE);
	    Assert.assertEquals(query2.getState(), Query.STATE_EXECUTE);
	    Assert.assertNull(query2.getResultSetCollector());
	    Assert.assertNotNull(query2.getTransaction());
	    Assert.assertEquals(query2.getTransaction().getTransactionState(), Query.STATE_EXECUTE);
	    Assert.assertEquals(query2.getTransaction().getOpenTransaction(), 1);
	    Assert.assertEquals(query2.getTransaction().getQueriesTransaction().length, 2);
	    final Query transactionQuery2 = query2.getTransaction().getQueriesTransaction()[1];

	    Assert.assertEquals(transactionQuery2, query2);

	    // Commit - Fin de la Transaction
	    connection.commit();

	    sqlMessages = messages.getSqlMessages();

	    Assert.assertEquals(1, sqlMessages.size());

	    sqlOperationMessage = sqlMessages.get(0);

	    Assert.assertEquals(SqlOperationLogger.CONNECTION, sqlOperationMessage.getTypeLogger());
	    Assert.assertNull(sqlOperationMessage.getArgs());
	    Assert.assertNotNull(sqlOperationMessage.getMethod());
	    Assert.assertNull(sqlOperationMessage.getInvoke());
	    Assert.assertNull(sqlOperationMessage.getException());

	    sqlOperation = sqlOperationMessage.getSqlOperation();
	    Assert.assertTrue(sqlOperation.getConnectionNumber() >= sqlOperation.getOpenConnection());
	    Assert.assertEquals(1, sqlOperation.getOpenConnection());
	    Assert.assertNotNull(sqlOperation.getDate());
	    Assert.assertNotNull(sqlOperation.getExecTime());
	    Assert.assertEquals(Driver.class, sqlOperation.getDriver().getClass());
	    Assert.assertEquals(GenericRdbmsSpecifics.class, sqlOperation.getRdbms().getClass());
	    Assert.assertEquals(CreateDatabase.getURL(false), sqlOperation.getUrl());
	    Assert.assertFalse(sqlOperation.isAutoCommit());
	    Assert.assertNotNull(sqlOperation.getTransaction());
	    Assert.assertEquals(query1.getTransaction().getTransactionState(), Query.STATE_COMMIT);
	    Assert.assertEquals(query1.getTransaction().getOpenTransaction(), 0);
	    Assert.assertEquals(query1.getTransaction().getQueriesTransaction().length, 2);
	    transactionQuery1 = query1.getTransaction().getQueriesTransaction()[0];

	    Assert.assertNotNull(transactionQuery1.getDate());
	    Assert.assertNotNull(transactionQuery1.getExecTime());
	    Assert.assertEquals(transactionQuery1.getJDBCQuery(),
		    "INSERT INTO PERSONNE (PRENOM, NOM, DATE_NAISSANCE) VALUES ('Transaction', 'SQL', '1970-01-01');");
	    Assert.assertEquals(transactionQuery1.getJDBCParameters().size(), 0);
	    Assert.assertEquals(transactionQuery1.getSQLQuery(),
		    "INSERT INTO PERSONNE (PRENOM, NOM, DATE_NAISSANCE) VALUES ('Transaction', 'SQL', '1970-01-01');");
	    Assert.assertEquals(transactionQuery1.getMethodQuery(), Query.METHOD_EXECUTE);
	    Assert.assertEquals(transactionQuery1.getState(), Query.STATE_COMMIT);
	    Assert.assertNull(transactionQuery1.getResultSetCollector());

	    Assert.assertNull(sqlOperation.getBatch());

	    query = sqlOperation.getQuery();
	    Assert.assertNull(query);
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
