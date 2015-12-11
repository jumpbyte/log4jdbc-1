package fr.ms.log4jdbc.h2.xa;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.sql.XAConnection;
import javax.transaction.xa.XAException;
import javax.transaction.xa.XAResource;
import javax.transaction.xa.Xid;

import org.h2.jdbcx.JdbcDataSource;
import org.junit.Test;

import fr.ms.log4jdbc.h2.DatabaseUtil;

public class XATransaction {

    @Test
    public void test() throws SQLException, XAException {
	final JdbcDataSource ds = new JdbcDataSource();
	ds.setURL(DatabaseUtil.URL_H2);
	ds.setUser(DatabaseUtil.USER);
	ds.setPassword(DatabaseUtil.PASSWORD);

	final XAConnection xaConnection = ds.getXAConnection();

	final XAResource xaResource = xaConnection.getXAResource();

	final Connection connection = xaConnection.getConnection();

	DatabaseUtil.createDatabase(connection);

	final Statement statement = connection.createStatement();

	final Xid xid = new SimpleXid(100, new byte[] { 0x01 }, new byte[] { 0x02 });

	xaResource.start(xid, XAResource.TMNOFLAGS);

	statement.execute("INSERT INTO PERSONNE (PRENOM, NOM, DATE_NAISSANCE) VALUES ('RollBack1', 'SQL', '1970-01-01');");

	statement.execute("INSERT INTO PERSONNE (PRENOM, NOM, DATE_NAISSANCE) VALUES ('RollBack2', 'SQL', '1970-01-01');");

	statement.execute("INSERT INTO PERSONNE (PRENOM, NOM, DATE_NAISSANCE) VALUES ('RollBack3', 'SQL', '1970-01-01');");

	statement.execute("INSERT INTO PERSONNE (PRENOM, NOM, DATE_NAISSANCE) VALUES ('RollBack4', 'SQL', '1970-01-01');");

	statement.addBatch("INSERT INTO PERSONNE (PRENOM, NOM, DATE_NAISSANCE) VALUES ('RollBack5', 'SQL', '1970-01-01');");

	statement.addBatch("INSERT INTO PERSONNE (PRENOM, NOM, DATE_NAISSANCE) VALUES ('RollBack6', 'SQL', '1970-01-01');");

	statement.addBatch("INSERT INTO PERSONNE (PRENOM, NOM, DATE_NAISSANCE) VALUES ('RollBack7', 'SQL', '1970-01-01');");

	statement.addBatch("INSERT INTO PERSONNE (PRENOM, NOM, DATE_NAISSANCE) VALUES ('RollBack8', 'SQL', '1970-01-01');");

	xaResource.end(xid, XAResource.TMSUCCESS);

	statement.executeBatch();

	statement.execute("INSERT INTO PERSONNE (PRENOM, NOM, DATE_NAISSANCE) VALUES ('RollBack9', 'SQL', '1970-01-01');");

	final int prepare = xaResource.prepare(xid);
	if (prepare == XAResource.XA_OK) {
	    xaResource.commit(xid, false);
	}

	final ResultSet resultSet = statement.executeQuery("SELECT * FROM PERSONNE;");

	DatabaseUtil.printResultSet(resultSet);
    }
}
