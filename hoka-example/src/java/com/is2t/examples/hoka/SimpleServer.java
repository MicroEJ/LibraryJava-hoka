/*
* Java
*
* Copyright 2015 IS2T. All rights reserved.
* Use of this source code is governed by a BSD-style license that can be found at http://www.is2t.com/open-source-bsd-license/.
*/
package com.is2t.examples.hoka;

import java.io.IOException;
import java.net.ServerSocket;

import ej.hoka.http.HTTPServer;
import ej.hoka.http.HTTPSession;

/*
 * This simple server exposes resources from the src/resources folder
 * It uses the SimpleHTTPSession to serve a resource for the root of the server
 */
public class SimpleServer {

	private static final int PORT = 8080;

	public static void main(String[] args) throws IOException {
		
		// retrieve the socket connector implementation of the platform
		ServerSocket serverSocket = new ServerSocket(PORT);
		
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
