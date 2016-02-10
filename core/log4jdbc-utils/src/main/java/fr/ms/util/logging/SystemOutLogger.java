package fr.ms.util.logging;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

public class SystemOutLogger implements Logger {

    private final static String PREFIX = "log4jdbc.log.";

    private final static Map LOG_LEVEL = new HashMap();

    private boolean debug;

    private boolean info;

    private boolean error;

    static {
	final Properties properties = System.getProperties();

	final Enumeration en = properties.propertyNames();
	while (en.hasMoreElements()) {
	    String propName = (String) en.nextElement();

	    if (propName.startsWith(PREFIX)) {
		final String propValue = properties.getProperty(propName);
		propName = propName.substring(PREFIX.length());

		LOG_LEVEL.put(propName, propValue);
	    }
	}
    }

    public SystemOutLogger(final String name) {

	String nameLogger = null;
	String levelLogger = null;

	final Iterator entries = LOG_LEVEL.entrySet().iterator();
	while (entries.hasNext()) {
	    final Entry element = (Entry) entries.next();
	    final String key = (String) element.getKey();
	    if (name.startsWith(key) && (nameLogger == null || key.length() > nameLogger.length())) {
		nameLogger = key;

		levelLogger = (String) element.getValue();
	    }

	}

	if (levelLogger != null) {
	    if (levelLogger.equals("debug")) {
		debug = true;
		info = true;
		error = true;
	    } else if (levelLogger.equals("info")) {
		info = true;
		error = true;
	    } else if (levelLogger.equals("error")) {
		error = true;
	    }
	}
    }

    public boolean isDebugEnabled() {
	return debug;
    }

    public boolean isInfoEnabled() {
	return info;
    }

    public boolean isErrorEnabled() {
	return error;
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
