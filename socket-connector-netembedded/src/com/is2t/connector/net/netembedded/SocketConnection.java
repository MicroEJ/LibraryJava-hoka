/**
 * Java
 *
 * Copyright 2011-2012 IS2T. All rights reserved
 * IS2T PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.is2t.connector.net.netembedded;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import com.is2t.connector.net.ISocketConnection;

/** IS2T-API
 * J2SE {@link java.net.Socket} connection
 */
public class SocketConnection implements ISocketConnection {
	
	private Socket socket;
	
	/** IS2T-API
	 * Create a new connection wrapper on the given {@link Socket}
	 */
	public SocketConnection(Socket socket) {
		this.socket = socket;
	}

	
	public void close() throws IOException {
		socket.close();
	}
	
	public String getAddress() throws IOException {
		try {
			return socket.getInetAddress().getHostAddress();
		}catch(NullPointerException e) {
			throw new IOException();
		}
	}
	
	public InputStream getInputStream() throws IOException {
		return socket.getInputStream();
	}
	
	public OutputStream getOutputStream() throws IOException {
		return socket.getOutputStream();
	}
	
}
