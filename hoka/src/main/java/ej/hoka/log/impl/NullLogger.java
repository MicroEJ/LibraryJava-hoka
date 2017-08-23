/*
 * Java
 *
 * Copyright 2009-2016 IS2T. All rights reserved.
 * IS2T PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package ej.hoka.log.impl;

import java.io.IOException;
import java.net.Socket;

import ej.hoka.log.Logger;

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

	@Override
	public void connectionClosed(Socket connection) {
		// Nothing
	}

	@Override
	public void connectionLost(Socket connection, IOException e) {
		// Nothing
	}

	@Override
	public void httpError(Socket connection, String status, String message) {
		// Nothing
	}

	@Override
	public void newConnection(Socket streamConnection) {
		// Nothing

	}

	@Override
	public void processConnection(Socket streamConnection) {
		// Nothing
	}

	@Override
	public void serverStarted() {
		// Nothing
	}

	@Override
	public void serverStopped() {
		// Nothing
	}

	@Override
	public void tooManyOpenConnections(int count, Socket connectionRefused) {
		// Nothing
	}

	@Override
	public void unexpectedError(Throwable e) {
		// Nothing
	}
}
