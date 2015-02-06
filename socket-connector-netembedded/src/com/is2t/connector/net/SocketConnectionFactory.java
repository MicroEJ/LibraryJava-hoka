package com.is2t.connector.net;

import java.io.IOException;

import com.is2t.connector.net.IClientSocketConnection;
import com.is2t.connector.net.IClientSocketConnectionFactory;
import com.is2t.connector.net.IServerSocketConnection;
import com.is2t.connector.net.ISocketConnection;
import com.is2t.connector.net.netembedded.SocketConnector;

/**
 * IS2T-API
 * <p>Provide an entry point to create abstract socket connectors (client and server).</p>
 */
public abstract class SocketConnectionFactory {
	
	/**
	 * IS2T-API
	 * Returns the {@link SocketConnectionFactory} implementation of the platform. 
	 * @return the {@link SocketConnectionFactory} implementation of the platform.
	 */
	public static SocketConnectionFactory getImpl(){
		return new SocketConnector();
	}
	
	/**
	 * IS2T-API
	 * According to platform, creates a new implementation of {@link IClientSocketConnection}
	 * @param host the hostname to reach
	 * @param port the port of the connection to establish
	 * @return a new implementation of {@link IClientSocketConnection}
	 */
	public abstract IClientSocketConnection newClientSocketConnection(String host, int port) throws IOException;
	
	/**
	 * IS2T-API
	 * According to platform, creates a new implementation of {@link IClientSocketConnectionFactory}
	 * @return a new implementation of {@link IClientSocketConnectionFactory}
	 */
	public abstract IClientSocketConnectionFactory newClientSocketConnectionFactory() throws IOException;
	
	/**
	 * IS2T-API
	 * According to platform, creates a new implementation of {@link IServerSocketConnection}
	 * @return a new implementation of {@link IServerSocketConnection}
	 */
	public abstract IServerSocketConnection newServerSocketConnection(int port) throws IOException;
	
	/**
	 * IS2T-API
	 * According to platform, creates a new implementation of {@link ISocketConnection}
	 * @return a new implementation of {@link ISocketConnection}
	 */
	public abstract ISocketConnection newSocketConnection(String host, int port) throws IOException;
	
}
