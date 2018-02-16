/*
 * Java
 *
 * Copyright 2009-2018 IS2T. All rights reserved.
 * IS2T PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package ej.hoka.log.impl;

import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Date;
import java.util.logging.Level;

import ej.hoka.http.HTTPServer;
import ej.hoka.log.Logger;

/**
 * <p>
 * The default logger for {@link HTTPServer}.
 * </p>
 */
public class DefaultLogger implements Logger {

	/**
	 * Field separator character ("Pipe" character: "|").
	 */
	public static final String FIELD_SEP = " | "; //$NON-NLS-1$

	/**
	 * {@link PrintStream} used for logging output.
	 */
	protected java.util.logging.Logger logger;

	private Level eventLevel;

	private Level errorLevel;

	/**
	 * <p>
	 * Creates a new logger. Logs will be sent to the given {@link PrintStream}
	 * </p>
	 *
	 * @param logger
	 *            the logger to use for logging
	 */
	public DefaultLogger(java.util.logging.Logger logger) {
		this.logger = logger;
		this.eventLevel = Level.INFO;
		this.errorLevel = Level.SEVERE;
	}

	/**
	 * <p>
	 * Logs the "Connection closed" message.
	 * </p>
	 *
	 * @param c
	 *            the {@link Socket} to get the hash code to log
	 *
	 * @see #dumpConnectionEvent(Socket, String)
	 */
	@Override
	public void connectionClosed(Socket c) {
		dumpConnectionEvent(c, "Connection closed"); //$NON-NLS-1$
	}

	/**
	 * <p>
	 * Logs the "Connection lost([reason])" message.
	 * </p>
	 *
	 * @param c
	 *            the {@link Socket} to get the hash code to log
	 * @param e
	 *            the {@link IOException} to get the reason of why the connection has been lost.
	 *
	 * @see #dumpConnectionEvent(Socket, String)
	 */
	@Override
	public void connectionLost(Socket c, IOException e) {
		dumpConnectionEvent(c, "Connection lost (" + e.getMessage() + ")"); //$NON-NLS-1$ //$NON-NLS-2$
	}

	/**
	 * Prints a connection event in the following form:<br>
	 * [date] | [name of current thread] | [hash code of the {@link Socket} instance] | <code>message</code>.
	 *
	 * @param c
	 *            the {@link Socket} instance
	 * @param message
	 *            the message to log
	 */
	protected void dumpConnectionEvent(Socket c, String message) {
		dumpEvent(c.hashCode() + FIELD_SEP + message);
	}

	/**
	 *
	 * <p>
	 * Logs an event. The log entry is in the following form:<br>
	 * <code>[date] | [name of current thread] | message</code>
	 * </p>
	 *
	 * @param message
	 *            the message to log
	 */
	public void dumpEvent(String message) {
		Date d = new Date();
		StringBuilder builder = new StringBuilder();
		builder.append(d.toString());
		builder.append(FIELD_SEP);
		builder.append(Thread.currentThread().getName());
		builder.append(FIELD_SEP);
		builder.append(message);
		this.logger.log(this.eventLevel, builder.toString());
	}

	/**
	 * <p>
	 * Logs the <code>status</code> and <code>message</code>.
	 * </p>
	 *
	 * @param c
	 *            the {@link Socket} to get the hash code to log
	 * @param status
	 *            textual status
	 * @param message
	 *            optional textual message (could be <code>null</code>)
	 *
	 * @see #dumpConnectionEvent(Socket, String)
	 */
	@Override
	public void httpError(Socket c, String status, String message) {
		String msg = status;
		if (message != null) {
			StringBuffer sb = new StringBuffer();
			msg = sb.append(msg).append(": ").append(message).toString(); //$NON-NLS-1$
		}
		dumpConnectionEvent(c, msg);
	}

	/**
	 * <p>
	 * Displays the message "New connection from [remote IP address]" with the hash code of the {@link Socket} instance
	 * <code>c</code>.
	 * </p>
	 *
	 * @param c
	 *            the {@link Socket} to get the remote IP address
	 * @see #dumpConnectionEvent(Socket, String)
	 */
	@Override
	public void newConnection(Socket c) {
		String message = "New connection"; //$NON-NLS-1$
		String address;
		StringBuffer sb = new StringBuffer();
		address = c.getInetAddress().toString();
		message = sb.append(message).append(" from ").append(address).toString(); //$NON-NLS-1$
		dumpConnectionEvent(c, message);
	}

	/**
	 * <p>
	 * Displays the message "Process connection" with the hash code of the {@link Socket} instance <code>c</code>.
	 * </p>
	 *
	 * @param c
	 *            the {@link Socket}
	 * @see #dumpConnectionEvent(Socket, String)
	 */
	@Override
	public void processConnection(Socket c) {
		dumpConnectionEvent(c, "Process connection"); //$NON-NLS-1$
	}

	/**
	 * <p>
	 * Displays the message "Server started".
	 * </p>
	 *
	 * @see #dumpEvent(String)
	 */
	@Override
	public void serverStarted() {
		dumpEvent("Server started"); //$NON-NLS-1$
	}

	/**
	 * <p>
	 * Displays the message "Server stopped".
	 * </p>
	 *
	 * @see #dumpEvent(String)
	 */
	@Override
	public void serverStopped() {
		dumpEvent("Server stopped"); //$NON-NLS-1$
	}

	/**
	 * Logs the event of refusing an incoming connection request due to too many open connections.
	 *
	 * @param nbOpen
	 *            the maximum number of open connections
	 * @param connectionRefused
	 *            the refused {@link Socket}
	 * @see #dumpEvent(String)
	 */
	@Override
	public void tooManyOpenConnections(int nbOpen, Socket connectionRefused) {
		String address = "from " + connectionRefused.getInetAddress().toString(); //$NON-NLS-1$
		dumpEvent("Connection " + address + " refused. Too many open connections (" + nbOpen + ")."); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}

	/**
	 * Prints the stack trace of the {@link Throwable} <code>e</code> to the standard error stream.
	 *
	 * @param e
	 *            the {@link Throwable} to log
	 * @see Throwable#printStackTrace()
	 */
	@Override
	public void unexpectedError(Throwable e) {
		this.logger.log(this.errorLevel, "Unexpected.", e); //$NON-NLS-1$
	}

	@Override
	public void setEventLevel(Level level) {
		if (level == null) {
			throw new IllegalArgumentException();
		}
		this.eventLevel = level;

	}

	@Override
	public void setErrorLevel(Level level) {
		if (level == null) {
			throw new IllegalArgumentException();
		}
		this.errorLevel = level;
	}

	@Override
	public java.util.logging.Logger getLogger() {
		return this.logger;
	}
}
