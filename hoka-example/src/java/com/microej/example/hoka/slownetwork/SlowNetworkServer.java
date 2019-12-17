/*
 * Java
 *
 * Copyright 2017-2019 MicroEJ Corp. All rights reserved.
 * For demonstration purpose only.
 * MicroEJ Corp. PROPRIETARY. Use is subject to license terms.
 */
package com.microej.example.hoka.slownetwork;

import java.io.IOException;

import ej.hoka.http.HTTPServer;

public class SlowNetworkServer {

	// This example is designed for a slow network.
	// Resources are sent in a compressed form when available if supported by the
	// client to reduce the lengths of responses.
	// Also, immutable resources are cached in the user's browser.

	public static void main(String[] args) throws IOException {
		// Enable cache for immutable resource sent by sub-handlers.
		CacheHandler cacheHandler = new CacheHandler();

		// Match the request URI with a file in the /hoka/ directory of the filesystem.
		cacheHandler.addRequestHandler(new FileSystemView("/", "/hoka"));

		HTTPServer server = new HTTPServer(8080, 10, 3, cacheHandler);

		// Enable debug
		server.sendStackTraceOnException(true);

		server.start();
	}

}
