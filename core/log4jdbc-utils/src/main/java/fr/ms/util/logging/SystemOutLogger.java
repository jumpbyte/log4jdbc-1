/*
 * This file is part of Log4Jdbc.
 *
 * Log4Jdbc is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Log4Jdbc is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Log4Jdbc.  If not, see <http://www.gnu.org/licenses/>.
 *
 */
package fr.ms.util.logging;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

/**
 *
 * @see <a href="http://marcosemiao4j.wordpress.com">Marco4J</a>
 *
 *
 * @author Marco Semiao
 *
 */
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
