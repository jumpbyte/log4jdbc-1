package fr.ms.log4jdbc.h2.connection;

import java.sql.SQLException;

import org.junit.After;
import org.junit.Test;

import fr.ms.log4jdbc.test.sqloperation.SqlMessage;

public class BatchTest {

    private final SqlMessage messages = SqlMessage.getInstance();

    @After
    public void clear() {
	messages.clear();
    }

    @Test
    public void simpleBatchTest() throws SQLException {

    }
}
