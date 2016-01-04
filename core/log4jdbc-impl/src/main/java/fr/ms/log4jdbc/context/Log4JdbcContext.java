package fr.ms.log4jdbc.context;

import java.sql.Driver;

public interface Log4JdbcContext {
    ConnectionContext newConnectionContext(Class clazz);

    ConnectionContext newConnectionContext(Driver driver, String url);
}
