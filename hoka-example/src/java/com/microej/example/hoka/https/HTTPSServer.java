/*
 * Java
 *
 * Copyright 2019 MicroEJ Corp. All rights reserved.
 * For demonstration purpose only.
 * MicroEJ Corp. PROPRIETARY. Use is subject to license terms.
 */
package com.microej.example.hoka.https;

import java.io.IOException;
import java.security.GeneralSecurityException;

import javax.net.ssl.SSLContext;

import ej.hoka.http.HTTPServer;
import ej.hoka.http.requesthandler.ResourceRequestHandler;
import ej.net.util.ssl.SslContextBuilder;

/*
 * This server example shows how to use the HTTP server in secure mode (HTTPS).
 * It uses the private key and certificates from the src/resources folder to enable HTTPS.
 */
public class HTTPSServer {

	private static final int PORT = 8443;

	private static final String PACKAGE = "/https/";

	private static final String KEY_PATH = PACKAGE + "hoka.key";
	private static final String CERTIFICATE_PATH = PACKAGE + "hoka.crt";
	private static final String CA_CERTIFICATE_PATH = PACKAGE + "ca.crt";

	public static void main(String[] args) {
		// Setup the SSL context with custom key and certificates.
		SSLContext sslContext;
		try {
			SslContextBuilder sslContextBuilder = new SslContextBuilder();
			sslContextBuilder.addClientKey(KEY_PATH, CERTIFICATE_PATH, CA_CERTIFICATE_PATH);
			sslContext = sslContextBuilder.build("123456"); // password
		} catch (GeneralSecurityException | IOException e) {
			throw new RuntimeException("Not able to setup SSL context", e);
		}

		// Create the HTTP server with the ServerSocketFactory of the SSL context
		HTTPServer server = new HTTPServer(PORT, 10, 3, new ResourceRequestHandler("/hoka/"),
				sslContext.getServerSocketFactory());

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
