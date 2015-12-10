package fr.ms.util.logging;

public class SystemOutLogger implements Logger {

    public boolean isDebugEnabled() {
	return true;
    }

    public boolean isInfoEnabled() {
	return true;
    }

    public boolean isErrorEnabled() {
	return true;
    }

    public void info(final String message) {
	if (isInfoEnabled()) {
	    System.out.println(message);
	}
    }

    public void debug(final String message) {
	if (isDebugEnabled()) {
	    System.out.println(message);
	}
    }

    public void error(final String message) {
	if (isErrorEnabled()) {
	    System.out.println(message);
	}
    }
}
