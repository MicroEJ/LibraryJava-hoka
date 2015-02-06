/*
* Java
*
* Copyright 2015 IS2T. All rights reserved.
* Use of this source code is governed by a BSD-style license that can be found at http://www.is2t.com/open-source-bsd-license/.
*/
package com.is2t.examples.hoka;

import java.io.IOException;

import com.is2t.connector.net.IServerSocketConnection;
import com.is2t.connector.net.SocketConnectionFactory;
import com.is2t.server.http.HTTPServer;
import com.is2t.server.http.HTTPSession;

/*
 * This simple server exposes resources from the src/resources folder
 * It uses the SimpleHTTPSession to serve a resource for the root of the server
 */
public class SimpleServer {

	private static final int PORT = 8080;

	public static void main(String[] args) {
		
		// retrieve the socket connector implementation of the platform
		SocketConnectionFactory factory = SocketConnectionFactory.getImpl();
		
		// and create a server socket with the factory
		IServerSocketConnection serverSocket = null;
		try {
			serverSocket = factory.newServerSocketConnection(PORT);
		} catch (IOException e) {
			//something went wrong
			//print the issue
			e.printStackTrace();
			//end the program
			return;
		}
		
		//create the http server with our custom http session
		HTTPServer server = new HTTPServer(serverSocket, 10, 1) {
			
			protected HTTPSession newHTTPSession() {
				return new SimpleHTTPSession(this);
			}
			
		};
		
		//once started the server is accessible on
		// http://localhost:8080
		server.start();

	}

}
