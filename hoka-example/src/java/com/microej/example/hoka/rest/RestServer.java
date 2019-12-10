/*
 * Java
 *
 * Copyright 2017-2019 MicroEJ Corp. All rights reserved.
 * For demonstration purpose only.
 * MicroEJ Corp. PROPRIETARY. Use is subject to license terms.
 */
package com.microej.example.hoka.rest;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.Set;

import ej.hoka.http.HTTPServer;
import ej.hoka.http.requesthandler.RequestHandlerComposite;
import ej.hoka.rest.RestEndpoint;
import ej.hoka.rest.RestRequestHandler;
import ej.hoka.rest.endpoint.AliasEndpoint;
import ej.hoka.rest.endpoint.GzipResourceEndpoint;
import ej.hoka.rest.endpoint.ResourceRestEndpoint;

public class RestServer {

	private static final String SLASH = "/";

	public static void main(String[] args) {
		HTTPServer restServer;
		try {
			RequestHandlerComposite rhc = new RequestHandlerComposite();

			RestRequestHandler endpointHandler = new RestRequestHandler();
			endpointHandler.addEndpoint(new HelloEndPoint());
			// Creates an endpoint for each resource in /rest/example.resources.list.
			// Compressed resources are served as-is and decompressed by the browser.
			try (InputStream resourceFile = RestServer.class
					.getResourceAsStream("/rest/example.resources.list")) {
				createStaticEndpoints(endpointHandler, resourceFile, "/index.html", "/hoka/");
			}

			rhc.addRequestHandler(endpointHandler);
			rhc.addRequestHandler(new DumpRequestHandler()); // Used in case endpointHandler doesn't process the request

			restServer = new HTTPServer(8080, 10, 3, rhc);
			restServer.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void createStaticEndpoints(RestRequestHandler endpoints, InputStream resourceFile, String homePage,
			String baseResourceDir) throws IOException {
		Properties filesProperties = new Properties();
		filesProperties.load(resourceFile);
		Set<String> files = filesProperties.stringPropertyNames();
		for (String filePath : files) {
			filePath = filePath.trim();
			String endpoint = filePath;
			if (filePath.startsWith(baseResourceDir)) {
				endpoint = SLASH + filePath.substring(baseResourceDir.length());
			}
			RestEndpoint restEndpoint;
			if (filePath.endsWith(GzipResourceEndpoint.GZIP_FILE_EXTENSION)) {
				endpoint = endpoint.substring(0, endpoint.length() - GzipResourceEndpoint.GZIP_FILE_EXTENSION.length());
				restEndpoint = new GzipResourceEndpoint(endpoint, filePath);
			} else {
				restEndpoint = new ResourceRestEndpoint(endpoint, filePath);
			}

			endpoints.addEndpoint(restEndpoint);
			if (endpoint.equals(homePage)) {
				endpoints.addEndpoint(new AliasEndpoint(SLASH, restEndpoint));
			}
		}
	}

}
