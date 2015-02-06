/**
 * Java
 *
 * Copyright 2009-2015 IS2T. All rights reserved.
 * IS2T PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.is2t.server.log.impl;

import java.io.IOException;

import com.is2t.connector.net.ISocketConnection;
import com.is2t.server.log.Logger;

/**
 * IS2T-API
 * <p>
 * The Null logger implements the {@link Logger} interface but does not log
 * anything. When no logging is required this logger can be used.
 * </p>
 */
public class NullLogger implements Logger {
	/**
	 * <p>
	 * Returns the single instance of {@link NullLogger}.
	 * </p>
	 * 
	 * @return the single instance of {@link NullLogger}
	 */
	public static Logger getInstance() {
		if (Instance == null) {
			Instance = new NullLogger();
		}
		return Instance;
	}

	/**
	 * IS2T-API
	 * <p>
	 * Empty implementation (does not log anything).
	 * </p>
	 * 
	 * @param connection
	 *            the {@link ISocketConnection}
	 */
	public void connectionClosed(ISocketConnection connection) {
		// no-op
	}

	/**
	 * IS2T-API
	 * <p>
	 * Empty implementation (does not log anything).
	 * </p>
	 * 
	 * @param connection
	 *            the {@link ISocketConnection}
	 * @param e
	 *            the {@link IOException}
	 */
	public void connectionLost(ISocketConnection connection, IOException e) {
		// no-op
	}

	/**
	 * IS2T-API
	 * <p>
	 * Empty implementation (does not log anything).
	 * </p>
	 * 
	 * @param connection
	 *            the {@link ISocketConnection}
	 * @param status
	 *            the status
	 * @param message
	 *            the message
	 */
	public void httpError(ISocketConnection connection, String status,
			String message) {
		// no-op
	}

	/**
	 * IS2T-API
	 * <p>
	 * Empty implementation (does not log anything).
	 * </p>
	 * 
	 * @param streamConnection
	 *            the {@link ISocketConnection}
	 */
	public void newConnection(ISocketConnection streamConnection) {
		// no-op
	}

	/**
	 * IS2T-API
	 * <p>
	 * Empty implementation (does not log anything).
	 * </p>
	 * 
	 * @param streamConnection
	 *            the {@link ISocketConnection}
	 */
	public void processConnection(ISocketConnection streamConnection) {
		// no-op
	}

	/**
	 * IS2T-API
	 * <p>
	 * Empty implementation (does not log anything).
	 * </p>
	 */
	public void serverStarted() {
		// no-op
	}

	/**
	 * IS2T-API
	 * <p>
	 * Empty implementation (does not log anything).
	 * </p>
	 */

	public void serverStopped() {
		// no-op
	}

	/**
	 * IS2T-API
	 * <p>
	 * Empty implementation (does not log anything).
	 * </p>
	 * 
	 * @param nbOpen
	 *            the maximum number of connections
	 * @param connectionRefused
	 *            {@link ISocketConnection} the refused connection
	 */

	public void tooManyOpenConnections(int nbOpen,
			ISocketConnection connectionRefused) {
		// no-op
	}

	/**
	 * IS2T-API
	 * <p>
	 * Empty implementation (does not log anything).
	 * </p>
	 * 
	 * @param e
	 *            the {@link IOException}
	 */

	public void unexpectedError(Throwable e) {
		// no-op
	}

	/*************************************************************************
	 * NOT IN API
	 ************************************************************************/

	/**
	 * The static instance to use in factory method.
	 */
	private static Logger Instance;

	/**
	 * The private constructor to avoid direct instantiation.
	 */
	private NullLogger() {
		// private constructor
	}

}
