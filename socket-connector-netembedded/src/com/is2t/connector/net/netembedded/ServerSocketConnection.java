/**
 * Java
 *
 * Copyright 2011-2012 IS2T. All rights reserved
 * IS2T PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.is2t.connector.net.netembedded;

import java.io.IOException;
import java.net.ServerSocket;

import com.is2t.connector.net.IServerSocketConnection;
import com.is2t.connector.net.ISocketConnection;

/** IS2T-API
 * J2SE {@link java.net.ServerSocket} connection
 */
public class ServerSocketConnection implements IServerSocketConnection {
	
	protected ServerSocket server;
	
	/** IS2T-API
	 * Create a new connection wrapper on the given port
	 */
	public ServerSocketConnection(int port) throws IOException {
		server = new ServerSocket(port);
	}

	protected ServerSocketConnection() {
		//used for subclass
	}

	public void close() throws IOException {
		server.close();
	}
	
	public String getAddress() throws IOException {
		try {
			return server.getInetAddress().getHostAddress();
		}catch(NullPointerException e) {
			throw new IOException();
		}
	}

	public int getPort() throws IOException {
		return server.getLocalPort();
	}
	
	public ISocketConnection accept() throws IOException {
		return new SocketConnection(server.accept());
	}
	
}
