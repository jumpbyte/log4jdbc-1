package fr.ms.log4jdbc.proxy.handler;

import fr.ms.log4jdbc.SqlOperation;

public interface OperationContext {

    SqlOperation newSqlOperation();

    Object wrapInvoke();
}
