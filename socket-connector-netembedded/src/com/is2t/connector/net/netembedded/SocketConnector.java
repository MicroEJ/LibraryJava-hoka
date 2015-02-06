package com.is2t.connector.net.netembedded;

import java.io.IOException;
import java.net.Socket;

import com.is2t.connector.net.IClientSocketConnection;
import com.is2t.connector.net.IClientSocketConnectionFactory;
import com.is2t.connector.net.IServerSocketConnection;
import com.is2t.connector.net.ISocketConnection;
import com.is2t.connector.net.SocketConnectionFactory;

public class SocketConnector extends SocketConnectionFactory{

	public IClientSocketConnection newClientSocketConnection(String host, int port) {
		return newClientSocketConnectionFactory().getNewClientSocketConnection(host, port);
	}

	public IClientSocketConnectionFactory newClientSocketConnectionFactory() {
		return new ClientSocketConnectionFactory();
	}

	public IServerSocketConnection newServerSocketConnection(int port) throws IOException {
		return new ServerSocketConnection(port);
	}

	public ISocketConnection newSocketConnection(String host, int port) throws IOException {
		return new SocketConnection(new Socket(host, port));
	}

}
