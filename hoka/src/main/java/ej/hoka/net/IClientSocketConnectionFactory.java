/*
 * Java
 *
 * Copyright 2009-2016 IS2T. All rights reserved
 * IS2T PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package ej.hoka.net;

import java.io.IOException;

/**
 * A generic client socket connection factory abstraction allowing multiple background implementations.
 */
public interface IClientSocketConnectionFactory {

	/**
	 * Creates a new client socket connection on given port to specified host.
	 *
	 * @param host
	 *            the host name.
	 * @param port
	 *            the port number.
	 * @return the created client socket connection.
	 * @throws IOException
	 *             if an I/O occurs when creating connection.
	 */
	IClientSocketConnection getNewClientSocketConnection(String host, int port) throws IOException;
}
