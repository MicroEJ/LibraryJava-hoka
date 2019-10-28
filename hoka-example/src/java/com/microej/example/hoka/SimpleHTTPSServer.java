/*
 * Java
 *
 * Copyright 2019 MicroEJ Corp. All rights reserved.
 * For demonstration purpose only.
 * MicroEJ Corp. PROPRIETARY. Use is subject to license terms.
 */
package com.microej.example.hoka;

import ej.hoka.http.HTTPServer;
import ej.hoka.http.HTTPSession;
import ej.hoka.http.body.StringBodyParserFactory;
import ej.hoka.https.HTTPSServerFactory;

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
		// Create the https server factory with our custom http session
		HTTPSServerFactory httpsServerFactory = new HTTPSServerFactory() {
			@Override
			protected HTTPSession newHTTPSession(HTTPServer server) {
				return new SimpleHTTPSession(server);
			}
		};

		// Initializes the ssl context used for https
		httpsServerFactory.initSSLContext(KEY_PATH, "123456", CERTIFICATE_PATH, CA_CERTIFICATE_PATH);

		// Creates the https server with our configuration
		HTTPServer server = httpsServerFactory.create(PORT, 10, 1);
		server.setBodyParserFactory(new StringBodyParserFactory());

		// Once started the server is accessible on https://localhost:8443
		server.start();
	}

}
