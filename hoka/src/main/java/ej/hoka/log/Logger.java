/*
 * Java
 *
 * Copyright 2009-2016 IS2T. All rights reserved.
 * IS2T PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package ej.hoka.log;

import java.io.IOException;
import java.net.Socket;

import ej.hoka.http.HTTPServer;
import ej.hoka.http.HTTPSession;

/**
 * <p>
 * Interface for loggers for the {@link HTTPServer}.
 * </p>
 */
public interface Logger {

	/**
	 * <p>
	 * Called by the {@link HTTPSession} job when the connection is closed.
	 * </p>
	 *
	 * @param connection
	 *            the {@link Socket}
	 */
	void connectionClosed(Socket connection);

	/**
	 * <p>
	 * Called by the {@link HTTPSession} job when an unexpected I/O error occurs.
	 * </p>
	 *
	 * @param connection
	 *            the {@link Socket}
	 * @param e
	 *            the {@link IOException}
	 */
	void connectionLost(Socket connection, IOException e);

	/**
	 * <p>
	 * Called by the {@link HTTPSession} to log an error.
	 * </p>
	 *
	 * @param connection
	 *            the {@link Socket}
	 * @param status
	 *            the HTTP Error status
	 * @param message
	 *            the error message (may be <code>null</code>)
	 */
	void httpError(Socket connection, String status, String message);

	/**
	 * <p>
	 * Called by a {@link HTTPServer} job when a new connection has been accepted.
	 * </p>
	 *
	 * @param streamConnection
	 *            the {@link Socket}
	 */
	void newConnection(Socket streamConnection);

	/**
	 * <p>
	 * Called by a {@link HTTPSession} job when a new connection is being processed.
	 * </p>
	 *
	 * @param streamConnection
	 *            the {@link Socket}
	 */
	void processConnection(Socket streamConnection);

	/**
	 * <p>
	 * Called when {@link HTTPServer} is started.
	 * </p>
	 */
	void serverStarted();

	/**
	 * <p>
	 * Called when {@link HTTPServer} is stopped. This is the last event logged.
	 * </p>
	 */
	void serverStopped();

	/**
	 * <p>
	 * Called by a {@link HTTPServer} when a connection could not be handled because of too many open connections.
	 * </p>
	 *
	 * @param count
	 *            the number of opened connection
	 * @param connectionRefused
	 *            the connection that has been refused
	 */
	void tooManyOpenConnections(int count, Socket connectionRefused);

	/**
	 * <p>
	 * Called by a {@link HTTPServer} when an unexpected error occurs.
	 * </p>
	 *
	 * @param e
	 *            the occurred error
	 */
	void unexpectedError(Throwable e);

}
