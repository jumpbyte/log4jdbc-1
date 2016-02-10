package fr.ms.util.logging;

public class SystemOutLoggerFactory implements LoggerFactory {

    public Logger getLogger(final String name) {
	return new SystemOutLogger(name);
    }
}
