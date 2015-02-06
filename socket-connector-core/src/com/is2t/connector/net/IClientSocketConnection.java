/**
 * Java
 *
 * Copyright 2009-2010 IS2T. All rights reserved
 * IS2T PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.is2t.connector.net;

import java.io.IOException;

/** IS2T-API
 * Generic Client Socket connection. (J2ME/J2SE independent)
 */
public interface IClientSocketConnection {

	/** IS2T-API
	 * connect to a socket at the address specified by port and uri
	 */
	public ISocketConnection connect(String uri, int port) throws IOException;

	/** IS2T-API
	 * close the socket connection
	 */
	public void close() throws IOException;

	/** IS2T-API
	 * return the local address
	 */
	public String getAddress() throws IOException;
	
	/** IS2T-API
	 * return the port which the socket is open on
	 */
	public int getPort() throws IOException;
	
}
