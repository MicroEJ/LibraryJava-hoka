/*
 * Java
 *
 * Copyright 2019 MicroEJ Corp. All rights reserved.
 * For demonstration purpose only.
 * MicroEJ Corp. PROPRIETARY. Use is subject to license terms.
 */
package com.microej.example.hoka;

import javax.net.ssl.SSLContext;

import ej.hoka.http.HTTPServer;
import ej.hoka.http.body.StringBodyParserFactory;
import ej.net.util.ssl.SslContextBuilder;

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

		// Setup the SSL context
		SslContextBuilder sslContextBuilder = new SslContextBuilder();
		sslContextBuilder.addClientKey(KEY_PATH, CERTIFICATE_PATH, CA_CERTIFICATE_PATH);
		SSLContext sslContext = sslContextBuilder.build("123456"); // password

		// create the http server with our custom http session
		HTTPServer server = new HTTPServer(PORT, 10, 1, new SimpleHTTPSession.Factory(),
				sslContext.getServerSocketFactory());
		// Set a timeout to close automatically pending HTTP inactive connections
		server.setRequestTimeoutDuration(20000);
		server.setBodyParserFactory(new StringBodyParserFactory());

		// Once started the server is accessible on https://localhost:8443
		server.start();
	}

}
