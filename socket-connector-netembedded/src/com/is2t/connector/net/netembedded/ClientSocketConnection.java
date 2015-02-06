/**
 * Java
 *
 * Copyright 2009-2012 IS2T. All rights reserved
 * IS2T PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.is2t.connector.net.netembedded;

import java.io.IOException;
import java.net.Socket;

import com.is2t.connector.net.IClientSocketConnection;
import com.is2t.connector.net.ISocketConnection;

public class ClientSocketConnection implements IClientSocketConnection {

	protected Socket client;

	public ClientSocketConnection(String host, int port) throws IOException{
		client = new Socket(host, port);
		
	}
	
	public void close() throws IOException {
		client.close();
	}

	public ISocketConnection connect(String uri, int port) throws IOException {
		if (client != null) {
			client.close();
		}
		client = new Socket(uri, port);
		return new SocketConnection(client);
	}

	public String getAddress() throws IOException {
		return client.getInetAddress().toString();
	}

	public int getPort() throws IOException {
		return client.getLocalPort();
	}

}
