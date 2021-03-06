/*
 * Java
 *
 * Copyright 2018-2019 MicroEJ Corp. All rights reserved.
 * This library is provided in source code for use, modification and test, subject to license terms.
 * Any modification of the source code will break MicroEJ Corp. warranties on the whole library.
 */
package ej.hoka.log;

import ej.util.message.MessageBuilder;
import ej.util.message.MessageLogger;
import ej.util.message.basic.BasicMessageBuilder;
import ej.util.message.basic.BasicMessageLogger;

/**
 * Gathers the messages.
 */
public final class Messages {
	// ****************//
	// Error messages. //
	// ****************//

	/**
	 * Too many connections.
	 */
	public static final int TOO_MANY_CONNECTION = -1;

	/**
	 * Multiple start is forbidden.
	 */
	public static final int MULTIPLE_START_FORBIDDEN = -2;

	/**
	 * URI is empty.
	 */
	public static final int EMPTY_URI = -3;

	/**
	 * Requested URI contains "..".
	 */
	public static final int DIRECTORY_TRAVERSAL_URI = -4;

	/**
	 * Unknown error.
	 */
	public static final int ERROR_UNKNOWN = -255;

	// ****************//
	// Info messages. //
	// ****************//

	/**
	 * A new connection arrived.
	 */
	public static final int NEW_CONNECTION = 1;

	/**
	 * The server is started.
	 */
	public static final int SERVER_STARTED = 2;

	/**
	 * The server is stopped.
	 */
	public static final int SERVER_STOPPED = 3;

	/**
	 * A connection is being processed.
	 */
	public static final int PROCESS_CONNECTION = 4;

	/**
	 * A response has been sent.
	 */
	public static final int HTTP_RESPONSE = 5;

	/**
	 * The connection is lost.
	 */
	public static final int CONNECTION_LOST = 6;

	/**
	 * The connection is closed.
	 */
	public static final int CONNECTION_CLOSED = 7;

	/**
	 * Hoka category message.
	 */
	public static final String CATEGORY_HOKA = "Hoka"; //$NON-NLS-1$

	/**
	 * The message builder.
	 */
	public static final MessageBuilder BUILDER = new BasicMessageBuilder();

	/**
	 * The message logger.
	 */
	public static final MessageLogger LOGGER = new BasicMessageLogger(BUILDER);

	private Messages() {
		// Forbid instantiation
	}
}
