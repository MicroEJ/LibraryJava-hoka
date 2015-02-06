/**
 * Java
 *
 * Copyright 2009-2010 IS2T. All rights reserved
 * IS2T PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.is2t.connector.net.netembedded;

import java.io.IOException;

import com.is2t.connector.net.IClientSocketConnection;
import com.is2t.connector.net.IClientSocketConnectionFactory;

public class ClientSocketConnectionFactory implements IClientSocketConnectionFactory {

	/** IS2T-API
	 * return a J2SE {@link java.net.Socket} IClientSocketConnection
	 */
	public IClientSocketConnection getNewClientSocketConnection(String host, int port) {
		try {
			return new ClientSocketConnection(host, port);
		} catch (IOException e) {
			throw new RuntimeException();
		}
	}

}
