package fr.ms.util.logging;

import java.util.Iterator;

import fr.ms.util.Service;

public final class LoggerManager {

    private final static LoggerFactory factory = loadLoggerFactory();

    public static Logger getLogger(final Class clazz) {
	return getLogger(clazz.getName());
    }

    public static Logger getLogger(final String name) {
	final Logger logger = factory.getLogger(name);
	return logger;
    }

    private final static LoggerFactory loadLoggerFactory() {
	final Iterator providers = Service.providers(LoggerFactory.class);

	if (providers.hasNext()) {
	    return (LoggerFactory) providers.next();
	} else {
	    return new SystemOutLoggerFactory();
	}
    }
}
