package fr.ms.log4jdbc.proxy.handler;

import fr.ms.lang.reflect.ProxyOperation;
import fr.ms.log4jdbc.SqlOperation;

public interface Log4JdbcOperation extends ProxyOperation {

    SqlOperation getOperation();

    void postOperation();
}
