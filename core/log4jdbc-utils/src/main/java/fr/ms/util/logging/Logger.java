package fr.ms.util.logging;

public interface Logger {

    boolean isDebugEnabled();

    boolean isInfoEnabled();

    boolean isErrorEnabled();

    void debug(String message);

    void info(String message);

    void error(String message);
}
