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
package fr.ms.util.logging.impl;

import java.io.PrintWriter;
import java.io.Writer;
import java.util.Date;

import fr.ms.lang.delegate.DefaultStringMakerFactory;
import fr.ms.lang.delegate.StringMakerFactory;
import fr.ms.lang.stringmaker.impl.StringMaker;
import fr.ms.util.logging.Logger;

/**
 *
 * @see <a href="http://marcosemiao4j.wordpress.com">Marco4J</a>
 *
 *
 * @author Marco Semiao
 *
 */
public class DefaultLogger implements Logger {

    private final static StringMakerFactory stringMakerFactory = DefaultStringMakerFactory.getInstance();

    private final PrintHandler printHandler;

    private final String name;

    private String level;

    private boolean debug;

    private boolean info;

    private boolean error;

    public DefaultLogger(final PrintHandler printHandler, final String level, final String name) {

	this.printHandler = printHandler;

	if (level != null) {
	    if (level.equals("debug")) {
		debug = true;
		info = true;
		error = true;
	    } else if (level.equals("info")) {
		info = true;
		error = true;
	    } else if (level.equals("error")) {
		error = true;
	    }

	    this.level = level.toUpperCase();
	}

	this.name = name;
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

    public void debug(final String message) {
	if (isDebugEnabled()) {
	    final String formatMessage = formatMessage(message);
	    printHandler.debug(formatMessage);
	}
    }

    public void info(final String message) {
	if (isInfoEnabled()) {
	    final String formatMessage = formatMessage(message);
	    printHandler.info(formatMessage);
	}
    }

    public void error(final String message) {
	if (isErrorEnabled()) {
	    final String formatMessage = formatMessage(message);
	    printHandler.error(formatMessage);
	}
    }

    private String formatMessage(final String message) {
	final Date now = new Date();

	final StringMaker newMessage = stringMakerFactory.newString();
	newMessage.append("[");
	newMessage.append(now);
	newMessage.append("]");

	newMessage.append(" [");
	newMessage.append(level);
	newMessage.append("]");

	newMessage.append(" [");
	newMessage.append(name);
	newMessage.append("] ");

	newMessage.append(message);

	return newMessage.toString();
    }

    public PrintWriter getPrintWriter() {
	final Writer writerLogger = new WriterLogger(this);
	return new PrintWriter(writerLogger);
    }

    public String toString() {
	return "DefaultLogger [name=" + name + ", level=" + level + "]";
    }
}
