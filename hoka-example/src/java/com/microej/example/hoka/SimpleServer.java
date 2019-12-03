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
import ej.hoka.http.requesthandler.ResourceRequestHandler;

/*
 * This simple server exposes resources from the src/resources/hoka folder.
 */
public class SimpleServer {

	private static final int PORT = 8080;

	public static void main(String[] args) {
		// Create the HTTP server with the default resource request handler
		HTTPServer server = new HTTPServer(PORT, 10, 3, new ResourceRequestHandler("/html/"));

		// Send the stack trace to the client when an exception is thrown
		server.sendStackTraceOnException(true);

		// Once started, the server is accessible on http://localhost:8080
		try {
			server.start();
		} catch (IOException e) {
			throw new RuntimeException("Port " + PORT + " already in use", e);
		}
	}

}
