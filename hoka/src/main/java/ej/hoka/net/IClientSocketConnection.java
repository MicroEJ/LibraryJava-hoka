/*
 * Java
 *
 * Copyright 2009-2016 IS2T. All rights reserved
 * IS2T PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package ej.hoka.net;

import java.io.IOException;

/**
 * A generic client socket connection abstraction allowing multiple background implementations.
 */
public interface IClientSocketConnection {

	/**
	 * Closes this client connection.
	 *
	 * @throws IOException
	 *             if an I/O error occurs when closing connection.
	 */
	void close() throws IOException;

	/**
	 * Connects to given port on specified port.
	 *
	 * <p>
	 * If a connection already exists, it is closed before creating the new one.
	 *
	 * @param host
	 *            the host name.
	 * @param port
	 *            the port number.
	 * @return the created socket connection.
	 * @throws IOException
	 *             if an I/O occurs when creating connection.
	 */
	ISocketConnection connect(String host, int port) throws IOException;

	/**
	 * Gets the local address of this connection.
	 *
	 * @return the connection local address.
	 * @throws IOException
	 *             if local address cannot be retrieved.
	 */
	String getAddress() throws IOException;

	/**
	 * Gets the port of this client connection.
	 *
	 * @return the client connection port.
	 * @throws IOException
	 *             if port cannot be retrieved.
	 */
	int getPort() throws IOException;

}
