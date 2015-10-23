package fr.ms.log4jdbc.proxy.handler;

import fr.ms.log4jdbc.SqlOperation;

public interface Log4JdbcOperation {

    void buildSqlOperation();

    SqlOperation getSqlOperation();

    Object getInvoke();
}
