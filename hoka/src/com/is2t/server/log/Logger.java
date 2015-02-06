/**
 * Java
 *
 * Copyright 2009-2013 IS2T. All rights reserved.
 * IS2T PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.is2t.server.log;

import java.io.IOException;

import com.is2t.connector.net.ISocketConnection;
import com.is2t.server.http.HTTPServer;
import com.is2t.server.http.HTTPSession;

/**
 * IS2T-API
 * <p>
 * Interface for loggers for the {@link HTTPServer}.
 * </p>
 */
public interface Logger {

	/**
	 * IS2T-API
	 * <p>
	 * Called when {@link HTTPServer} is started.
	 * </p>
	 */
	public void serverStarted();

	/**
	 * IS2T-API
	 * <p>
	 * Called when {@link HTTPServer} is stopped. This is the last event logged.
	 * </p>
	 */
	public void serverStopped();

	/**
	 * IS2T-API
	 * <p>
	 * Called by a {@link HTTPServer} job when a new connection has been accepted.
	 * </p>
	 * 
	 * @param streamConnection
	 *            the {@link ISocketConnection}
	 */
	public void newConnection(ISocketConnection streamConnection);

	/**
	 * IS2T-API
	 * <p>
	 * Called by a {@link HTTPSession} job when a new connection is being
	 * processed.
	 * </p>
	 * 
	 * @param streamConnection
	 *            the {@link ISocketConnection}
	 */
	public void processConnection(ISocketConnection streamConnection);

	/**
	 * IS2T-API
	 * <p>
	 * Called by the {@link HTTPSession} job when an unexpected I/O error occurs.
	 * </p>
	 * 
	 * @param connection
	 *            the {@link ISocketConnection}
	 * @param e
	 *            the {@link IOException}
	 */
	public void connectionLost(ISocketConnection connection, IOException e);

	/**
	 * IS2T-API
	 * <p>
	 * Called by the {@link HTTPSession} job when the connection is closed.
	 * </p>
	 * 
	 * @param connection
	 *            the {@link ISocketConnection}
	 */
	public void connectionClosed(ISocketConnection connection);

	/**
	 * IS2T-API
	 * <p>
	 * Called by the {@link HTTPSession} to log an error.
	 * </p>
	 * 
	 * @param connection
	 *            the {@link ISocketConnection}
	 * @param status
	 *            the HTTP Error status
	 * @param message
	 *            the error message (may be <code>null</code>)
	 */
	public void httpError(ISocketConnection connection, String status,
			String message);

	/**
	 * IS2T-API
	 * <p>
	 * Called by a {@link HTTPServer} when a connection could not be handled
	 * because of too many open connections.
	 * </p>
	 * 
	 * @param count
	 *            the number of opened connection
	 * @param connectionRefused
	 *            the connection that has been refused
	 */
	public void tooManyOpenConnections(int count,
			ISocketConnection connectionRefused);

	/**
	 * IS2T-API
	 * <p>
	 * Called by a {@link HTTPServer} when an unexpected error occurs.
	 * </p>
	 * 
	 * @param e
	 *            the occurred error
	 */
	public void unexpectedError(Throwable e);

}
