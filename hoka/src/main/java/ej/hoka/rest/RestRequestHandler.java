/*
 * Java
 *
 * Copyright 2016-2019 MicroEJ Corp. All rights reserved.
 * This library is provided in source code for use, modification and test, subject to license terms.
 * Any modification of the source code will break MicroEJ Corp. warranties on the whole library.
 */
package ej.hoka.rest;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import ej.hoka.http.HTTPRequest;
import ej.hoka.http.HTTPResponse;
import ej.hoka.http.requesthandler.RequestHandler;

/**
 * A request handler that exposes a REST API. Handles GET, POST, PUT and DELETE operations on endpoints.
 *
 * @see RestEndpoint
 */
public class RestRequestHandler implements RequestHandler {

	private final List<RestEndpoint> endpoints;

	/**
	 * Constructs a REST request handler with no endpoint.
	 *
	 * @see #addEndpoint(RestEndpoint)
	 *
	 */
	public RestRequestHandler() {
		this.endpoints = new ArrayList<>();
	}

	/**
	 * Adds an endpoint to this server.
	 *
	 * @param endpoint
	 *            the endpoint to add.
	 */
	public void addEndpoint(RestEndpoint endpoint) {
		this.endpoints.add(endpoint);
	}

	@Override
	public HTTPResponse process(HTTPRequest request, Map<String, String> attributes) {
		String uri = request.getURI();
		for (RestEndpoint endpoint : this.endpoints) {
			if (endpoint.getURI().equals(uri)) {
				switch (request.getMethod()) {
				case HTTPRequest.GET:
					return endpoint.get(request, attributes);
				case HTTPRequest.POST:
					return endpoint.post(request, attributes);
				case HTTPRequest.PUT:
					return endpoint.put(request, attributes);
				case HTTPRequest.DELETE:
					return endpoint.delete(request, attributes);
				default:
					return HTTPResponse.RESPONSE_NOT_FOUND;
				}
			}
		}
		return null;
	}

}
