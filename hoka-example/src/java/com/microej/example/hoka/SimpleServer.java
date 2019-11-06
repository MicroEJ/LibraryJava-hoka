/*
 * Java
 *
 * Copyright 2015-2019 MicroEJ Corp. All rights reserved.
 * For demonstration purpose only.
 * MicroEJ Corp. PROPRIETARY. Use is subject to license terms.
 */
package com.microej.example.hoka;

import java.io.IOException;

import ej.hoka.http.HTTPServer;
import ej.hoka.http.body.StringBodyParserFactory;

/*
 * This simple server exposes resources from the src/resources folder
 * It uses the SimpleHTTPSession to serve a resource for the root of the server
 */
public class SimpleServer {

	private static final int PORT = 8080;

	public static void main(String[] args) throws IOException {
		//create the http server with our custom http session
		HTTPServer server = new HTTPServer(PORT, 10, 1, new SimpleHTTPSession.Factory());
		server.setRequestTimeoutDuration(20000);
		server.setBodyParserFactory(new StringBodyParserFactory());

		//once started the server is accessible on
		// http://localhost:8080
		server.start();
	}

}
