package fr.ms.log4jdbc;

import java.lang.reflect.Method;

public class DummyLogger implements SqlOperationLogger {

    public boolean isLogger(final String typeLogger) {
	return true;
    }

    public boolean isEnabled() {
	return true;
    }

    public void buildLog(final SqlOperation sqlOperation, final Method method, final Object[] args, final Object invoke) {
    }

    public void buildLog(final SqlOperation sqlOperation, final Method method, final Object[] args, final Throwable exception) {

    }
}
