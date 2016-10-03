/*
 * Java
 *
 * Copyright 2009-2016 IS2T. All rights reserved.
 * IS2T PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package ej.hoka.log.impl;

import java.io.IOException;

import ej.hoka.log.Logger;
import ej.hoka.net.ISocketConnection;

/**
 * <p>
 * The Null logger implements the {@link Logger} interface but does not log anything. When no logging is required this
 * logger can be used.
 * </p>
 */
public class NullLogger implements Logger {
	/**
	 * The static instance to use in factory method.
	 */
	private static Logger Instance;

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
	 * The private constructor to avoid direct instantiation.
	 */
	private NullLogger() {
		// private constructor
	}

	/**
	 * <p>
	 * Empty implementation (does not log anything).
	 * </p>
	 *
	 * @param connection
	 *            the {@link ISocketConnection}
	 */
	@Override
	public void connectionClosed(ISocketConnection connection) {
		// no-op
	}

	/**
	 * <p>
	 * Empty implementation (does not log anything).
	 * </p>
	 *
	 * @param connection
	 *            the {@link ISocketConnection}
	 * @param e
	 *            the {@link IOException}
	 */
	@Override
	public void connectionLost(ISocketConnection connection, IOException e) {
		// no-op
	}

	/**
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
	@Override
	public void httpError(ISocketConnection connection, String status, String message) {
		// no-op
	}

	/**
	 * <p>
	 * Empty implementation (does not log anything).
	 * </p>
	 *
	 * @param streamConnection
	 *            the {@link ISocketConnection}
	 */
	@Override
	public void newConnection(ISocketConnection streamConnection) {
		// no-op
	}

	/**
	 * <p>
	 * Empty implementation (does not log anything).
	 * </p>
	 *
	 * @param streamConnection
	 *            the {@link ISocketConnection}
	 */
	@Override
	public void processConnection(ISocketConnection streamConnection) {
		// no-op
	}

	/**
	 * <p>
	 * Empty implementation (does not log anything).
	 * </p>
	 */
	@Override
	public void serverStarted() {
		// no-op
	}

	/**
	 * <p>
	 * Empty implementation (does not log anything).
	 * </p>
	 */

	@Override
	public void serverStopped() {
		// no-op
	}

	/**
	 * <p>
	 * Empty implementation (does not log anything).
	 * </p>
	 *
	 * @param nbOpen
	 *            the maximum number of connections
	 * @param connectionRefused
	 *            {@link ISocketConnection} the refused connection
	 */

	@Override
	public void tooManyOpenConnections(int nbOpen, ISocketConnection connectionRefused) {
		// no-op
	}

	/**
	 * <p>
	 * Empty implementation (does not log anything).
	 * </p>
	 *
	 * @param e
	 *            the {@link IOException}
	 */

	@Override
	public void unexpectedError(Throwable e) {
		// no-op
	}

}
