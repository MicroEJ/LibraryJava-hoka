/*
 * Java
 *
 * Copyright 2019 MicroEJ Corp. All rights reserved.
 * For demonstration purpose only.
 * MicroEJ Corp. PROPRIETARY. Use is subject to license terms.
 */
package com.microej.example.hoka;

import java.net.ServerSocket;

import ej.hoka.http.HTTPServer;
import ej.hoka.http.HTTPServer.HTTPSessionFactory;
import ej.hoka.http.HTTPSession;
import ej.hoka.http.body.StringBodyParserFactory;
import ej.hoka.https.SSLServerSocketFactory;

/*
 * This simple server exposes resources from the src/resources folder
 * It uses the SimpleHTTPSession to serve a resource for the root of the server
 * It uses the private key and certificates from the src/resources folder to enable HTTPS.
 */
public class SimpleHTTPSServer {

	private static final int PORT = 8443;
	private static final String KEY_PATH = "/com/microej/example/hoka/hoka.key";
	private static final String CERTIFICATE_PATH = "/com/microej/example/hoka/hoka.crt";
	private static final String CA_CERTIFICATE_PATH = "/com/microej/example/hoka/ca.crt";

	public static void main(String[] args) throws Exception {

		// retrieve the SSL socket connector with our private key and associated
		// certification
		SSLServerSocketFactory sslServerSocketFactory = new SSLServerSocketFactory(KEY_PATH, "123456", CERTIFICATE_PATH,
				CA_CERTIFICATE_PATH);
		ServerSocket serverSocket = sslServerSocketFactory.createConnection(PORT);

		// create the http server with our custom http session
		HTTPServer server = new HTTPServer(serverSocket, new HTTPSessionFactory() {
			@Override
			public HTTPSession newHttpSession(HTTPServer server) {
				return new SimpleHTTPSession(server);
			}
		}, 10, 1);
		server.setBodyParserFactory(new StringBodyParserFactory());

		// Once started the server is accessible on https://localhost:8443
		server.start();
	}

}
