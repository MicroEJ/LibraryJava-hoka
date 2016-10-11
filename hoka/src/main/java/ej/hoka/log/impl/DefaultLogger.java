/*
 * Java
 *
 * Copyright 2009-2016 IS2T. All rights reserved.
 * IS2T PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package ej.hoka.log.impl;

import java.io.IOException;
import java.io.PrintStream;
import java.util.Date;

import ej.hoka.http.HTTPServer;
import ej.hoka.log.Logger;
import ej.hoka.net.ISocketConnection;

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
	protected PrintStream out;

	/**
	 * <p>
	 * Creates a new logger. Logs will be sent to the given {@link PrintStream}
	 * </p>
	 *
	 * @param out
	 *            the print stream to use for logging
	 */
	public DefaultLogger(PrintStream out) {
		this.out = out;
	}

	/**
	 * <p>
	 * Logs the "Connection closed" message.
	 * </p>
	 *
	 * @param c
	 *            the {@link ISocketConnection} to get the hash code to log
	 *
	 * @see #dumpConnectionEvent(ISocketConnection, String)
	 */
	@Override
	public void connectionClosed(ISocketConnection c) {
		dumpConnectionEvent(c, "Connection closed"); //$NON-NLS-1$
	}

	/**
	 * <p>
	 * Logs the "Connection lost([reason])" message.
	 * </p>
	 *
	 * @param c
	 *            the {@link ISocketConnection} to get the hash code to log
	 * @param e
	 *            the {@link IOException} to get the reason of why the connection has been lost.
	 *
	 * @see #dumpConnectionEvent(ISocketConnection, String)
	 */
	@Override
	public void connectionLost(ISocketConnection c, IOException e) {
		dumpConnectionEvent(c, "Connection lost (" + e.getMessage() + ")"); //$NON-NLS-1$ //$NON-NLS-2$
	}

	/**
	 * Prints a connection event in the following form:<br>
	 * [date] | [name of current thread] | [hash code of the {@link ISocketConnection} instance] | <code>message</code>.
	 *
	 * @param c
	 *            the {@link ISocketConnection} instance
	 * @param message
	 *            the message to log
	 */
	protected void dumpConnectionEvent(ISocketConnection c, String message) {
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
		synchronized (this.out) {
			this.out.print(d.toString());
			this.out.print(FIELD_SEP);
			this.out.print(Thread.currentThread().getName());
			this.out.print(FIELD_SEP);
			this.out.println(message);
		}
	}

	/**
	 * <p>
	 * Logs the <code>status</code> and <code>message</code>.
	 * </p>
	 *
	 * @param c
	 *            the {@link ISocketConnection} to get the hash code to log
	 * @param status
	 *            textual status
	 * @param message
	 *            optional textual message (could be <code>null</code>)
	 *
	 * @see #dumpConnectionEvent(ISocketConnection, String)
	 */
	@Override
	public void httpError(ISocketConnection c, String status, String message) {
		String msg = status;
		if (message != null) {
			StringBuffer sb = new StringBuffer();
			msg = sb.append(msg).append(": ").append(message).toString(); //$NON-NLS-1$
		}
		dumpConnectionEvent(c, msg);
	}

	/**
	 * <p>
	 * Displays the message "New connection from [remote IP address]" with the hash code of the
	 * {@link ISocketConnection} instance <code>c</code>.
	 * </p>
	 *
	 * @param c
	 *            the {@link ISocketConnection} to get the remote IP address
	 * @see #dumpConnectionEvent(ISocketConnection, String)
	 */
	@Override
	public void newConnection(ISocketConnection c) {
		String message = "New connection"; //$NON-NLS-1$
		String address;
		try {
			StringBuffer sb = new StringBuffer();
			address = c.getAddress();
			message = sb.append(message).append(" from ").append(address).toString(); //$NON-NLS-1$
		} catch (IOException e) {
			// FIXME handle error
			this.unexpectedError(e);
		}
		dumpConnectionEvent(c, message);
	}

	/**
	 * <p>
	 * Displays the message "Process connection" with the hash code of the {@link ISocketConnection} instance
	 * <code>c</code>.
	 * </p>
	 *
	 * @param c
	 *            the {@link ISocketConnection}
	 * @see #dumpConnectionEvent(ISocketConnection, String)
	 */
	@Override
	public void processConnection(ISocketConnection c) {
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
	 *            the refused {@link ISocketConnection}
	 * @see #dumpEvent(String)
	 */
	@Override
	public void tooManyOpenConnections(int nbOpen, ISocketConnection connectionRefused) {
		String address;
		try {
			address = "from " + connectionRefused.getAddress(); //$NON-NLS-1$
		} catch (IOException e) {
			address = ""; //$NON-NLS-1$
		}
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
		// This is a CLDC-1.1 lack: stack trace cannot be dumped to a
		// PrintStream other than System.err.
		e.printStackTrace();
	}

}
