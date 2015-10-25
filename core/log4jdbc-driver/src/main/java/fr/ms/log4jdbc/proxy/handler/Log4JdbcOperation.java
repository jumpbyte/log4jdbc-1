package fr.ms.log4jdbc.proxy.handler;

import fr.ms.log4jdbc.SqlOperation;

public interface Log4JdbcOperation {

    SqlOperation newSqlOperation();

    Object getInvoke();
}
