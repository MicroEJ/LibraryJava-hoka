/*
 * Java
 *
 * Copyright 2010-2016 IS2T. All rights reserved
 * IS2T PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package ej.hoka.net;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * A generic socket connection abstraction allowing multiple background implementations.
 */
public interface ISocketConnection {
	/**
	 * Closes current connection.
	 *
	 * @throws IOException
	 *             if an I/O error occurs when closing connection.
	 */
	void close() throws IOException;

	/**
	 * Gets the local address of this connection.
	 *
	 * @return the connection local address.
	 * @throws IOException
	 *             if local address cannot be retrieved.
	 */
	String getAddress() throws IOException;

	/**
	 * Gets an input stream for this connection.
	 *
	 * @return an input stream for this connection.
	 * @throws IOException
	 *             if an I/O occurs when creating the input stream.
	 */
	InputStream getInputStream() throws IOException;

	/**
	 * Gets an output stream for this connection.
	 *
	 * @return an output stream for this connection.
	 * @throws IOException
	 *             if an I/O occurs when creating the output stream.
	 */
	OutputStream getOutputStream() throws IOException;

}
