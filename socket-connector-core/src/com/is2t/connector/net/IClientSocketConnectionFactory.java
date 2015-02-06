/**
 * Java
 *
 * Copyright 2009-2010 IS2T. All rights reserved
 * IS2T PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.is2t.connector.net;

/** IS2T-API
 * Factory which creates new instances of IClientSocketConnection
 *
 */
public interface IClientSocketConnectionFactory {
	/** IS2T-API
	 * return a new Instance of {@link IClientSocketConnection}
	 */
	public IClientSocketConnection getNewClientSocketConnection(String host, int port);
}
