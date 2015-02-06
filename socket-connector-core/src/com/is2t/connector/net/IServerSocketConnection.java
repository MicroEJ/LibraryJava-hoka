/**
 * Java
 *
 * Copyright 2010 IS2T. All rights reserved
 * IS2T PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.is2t.connector.net;

import java.io.IOException;


/** IS2T-API
 * Generic Server Socket connection. (J2ME/J2SE independent)
 */
public interface IServerSocketConnection {

	/**
	 * IS2T-API
	 * return a connection to this server
	 */
	public ISocketConnection accept() throws IOException;
	/**
	 * IS2T-API
	 * close the server connection
	 */
	public void close() throws IOException;
	/**
	 * IS2T-API
	 * return the address of this connection
	 */
	public String getAddress() throws IOException;
	/**
	 * IS2T-API
	 * return the port which the socket is open on
	 */
	public int getPort() throws IOException;


}
