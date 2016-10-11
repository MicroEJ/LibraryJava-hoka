/*
 * Java
 *
 * Copyright 2010-2016 IS2T. All rights reserved
 * IS2T PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package ej.hoka.net;

import java.io.IOException;

/**
 * A generic server socket connection abstraction allowing multiple background implementations.
 */
public interface IServerSocketConnection {

	/**
	 * Listens for a connection to be made to this server socket and accepts it. The method blocks until a connection is
	 * made.
	 *
	 * @return the new server socket connection.
	 * @throws IOException
	 *             if an I/O occurs when waiting for new connection.
	 */
	ISocketConnection accept() throws IOException;

	/**
	 * Closes this server connection.
	 *
	 * @throws IOException
	 *             if an I/O error occurs when closing connection.
	 */
	void close() throws IOException;

	/**
	 * Gets the local address of this server connection.
	 *
	 * @return the server connection local address.
	 * @throws IOException
	 *             if local address cannot be retrieved.
	 */
	String getAddress() throws IOException;

	/**
	 * Gets the local port of this server connection.
	 *
	 * @return the server connection local port.
	 * @throws IOException
	 *             if local port cannot be retrieved.
	 */
	int getPort() throws IOException;

	/**
	 * Creates a server connection, bound to the specified port.
	 *
	 * <p>
	 * A port number of 0 means that the port number is automatically allocated.
	 *
	 * <p>
	 * If the server connection is already active, it is closed and reopened using given port.
	 *
	 * @param port
	 *            the server connection port.
	 * @return the new server socket connection.
	 * @throws IOException
	 *             if an I/O occurs when opening connection.
	 */
	IServerSocketConnection setPort(int port) throws IOException;

}
