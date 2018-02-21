/*
 * Java
 *
 * Copyright 2009-2018 IS2T. All rights reserved.
 * IS2T PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package ej.hoka.ip;

import java.util.logging.Level;

import ej.hoka.log.Logger;
import ej.hoka.log.impl.DefaultLogger;

/**
 * <p>
 * Abstract IP server.
 * </p>
 */
public abstract class Server {

	/**
	 * <p>
	 * A logger storing server execution events.<br>
	 * Cannot be <code>null</code>.
	 * </p>
	 *
	 * <p>
	 * Always use {@link #setLogger(Logger)} to update this field to ensures "no null logger behaviour".
	 * </p>
	 *
	 * @see #setLogger(Logger)
	 * @see #getLogger()
	 */
	protected Logger logger;

	/**
	 * <p>
	 * Creates a new instance of {@link Server}, sets the current logger using the result of calling the method
	 * {@link #newLogger()}
	 */
	public Server() {
		setLogger(newLogger());
	}

	/**
	 * <p>
	 * Returns the {@link Logger} instance used to log server events.<br>
	 * </p>
	 *
	 * @return the {@link Logger} used to log server events.
	 */
	public Logger getLogger() {
		return this.logger;
	}

	/**
	 * Returns a new instance of the {@link DefaultLogger} initialized to use a Logger "Hoka".
	 *
	 * @return a new instance of the {@link DefaultLogger} initialized to use Logger "Hoka".
	 */
	protected Logger newLogger() {
		return new DefaultLogger(java.util.logging.Logger.getLogger("Hoka"));
	}

	/**
	 * <p>
	 * Set the <code>logger</code> as the current logger.<br>
	 * If the value of the <code>logger</code> argument is <code>null</code> the value of mute Logger is set as the
	 * current logger.
	 * </p>
	 *
	 * @param logger
	 *            the logger to be used (<code>null</code> indicates that logging is turned off)
	 * @see #getLogger()
	 * @see Logger
	 * @see DefaultLogger
	 */
	public void setLogger(Logger logger) {
		if (logger == null) {
			logger = newLogger();
			logger.getLogger().setLevel(Level.OFF);
		}
		this.logger = logger;
	}

}
