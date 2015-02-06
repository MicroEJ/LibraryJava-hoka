/**
 * Java
 *
 * Copyright 2010-2012 IS2T. All rights reserved
 * IS2T PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.is2t.connector.net;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/** IS2T-API
 * Generic Socket connection. (J2ME/J2SE independent)
 */
public interface ISocketConnection {
	/**
	 * IS2T-API
	 * open an {@link InputStream} on this connection
	 */
	public InputStream getInputStream() throws IOException;
	/**
	 * IS2T-API
	 * open an {@link OutputStream} on this connection
	 */
	public OutputStream getOutputStream() throws IOException;
	/**
	 * IS2T-API
	 * close the current connection
	 */
	public void close() throws IOException;
	/**
	 * IS2T-API
	 * return the local address
	 */
	public String getAddress() throws IOException;


}
