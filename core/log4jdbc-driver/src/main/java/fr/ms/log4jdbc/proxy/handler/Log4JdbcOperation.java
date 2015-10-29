package fr.ms.log4jdbc.proxy.handler;

import fr.ms.log4jdbc.SqlOperation;
import fr.ms.log4jdbc.SqlOperationLogger;

public interface Log4JdbcOperation {

    void buildOperation();

    SqlOperation getSqlOperation(SqlOperationLogger log);

    Object getResultMethod();
}
