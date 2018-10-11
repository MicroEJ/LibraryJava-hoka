/*
 * Java
 *
 * Copyright 2015-2018 IS2T. All rights reserved.
 * For demonstration purpose only.
 * IS2T PROPRIETARY. Use is subject to license terms.
 */
package com.microej.example.hoka;

import java.io.IOException;
import java.net.ServerSocket;

import ej.hoka.http.HTTPServer;
import ej.hoka.http.HTTPSession;
import ej.hoka.http.body.StringBodyParserFactory;

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

			@Override
			protected HTTPSession newHTTPSession() {
				return new SimpleHTTPSession(this);
			}
		};
		server.setBodyParserFactory(new StringBodyParserFactory());

		//once started the server is accessible on
		// http://localhost:8080
		server.start();
	}

}
