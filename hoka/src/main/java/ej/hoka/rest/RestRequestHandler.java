/*
 * Java
 *
 * Copyright 2016-2019 MicroEJ Corp. All rights reserved.
 * This library is provided in source code for use, modification and test, subject to license terms.
 * Any modification of the source code will break MicroEJ Corp. warranties on the whole library.
 */
package ej.hoka.rest;

import java.util.Map;

import ej.basictool.map.PackedMap;
import ej.hoka.http.HTTPRequest;
import ej.hoka.http.HTTPResponse;
import ej.hoka.http.requesthandler.RequestHandler;

/**
 * A request handler that exposes a REST API. Handles GET, POST, PUT and DELETE operations on endpoints.
 * <p>
 * The endpoint that handles the request is the endpoint with the most specific URI that matches the request. With two
 * endpoints at <code>/api/</code> and <code>/api/my/endpoint</code>, the second is used when requesting
 * <code>/api/my/endpoint</code> and also when requesting <code>/api/my/endpoint/and/extension</code> if it is a global
 * endpoint.
 *
 * @see RestEndpoint
 * @see RestEndpoint#isGlobal()
 */
public class RestRequestHandler implements RequestHandler {

	private final PackedMap<String, RestEndpoint> endpoints;

	/**
	 * Constructs a REST request handler with no endpoint.
	 *
	 * @see #addEndpoint(RestEndpoint)
	 *
	 */
	public RestRequestHandler() {
		this.endpoints = new PackedMap<>();
	}

	/**
	 * Adds an endpoint to this handler.
	 *
	 * @param endpoint
	 *            the endpoint to add.
	 */
	public synchronized void addEndpoint(RestEndpoint endpoint) {
		this.endpoints.put(endpoint.getURI(), endpoint);
	}

	@Override
	public HTTPResponse process(HTTPRequest request, Map<String, String> attributes) {
		RestEndpoint endpoint = getEndpointFromURI(request.getURI());

		if (endpoint == null) {
			return null;
		}

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
			return null;
		}
	}

	/**
	 * Find the endpoint the most specific that matches the request URI.
	 *
	 * @param uri
	 *            the URI to match.
	 * @return the {@link RestEndpoint} the most specific that matches the request URI.
	 */
	private RestEndpoint getEndpointFromURI(String uri) {
		PackedMap<String, RestEndpoint> endpoints = this.endpoints;

		if (endpoints.containsKey(uri)) {
			return endpoints.get(uri);
		}

		while (!uri.isEmpty()) {
			int i = uri.lastIndexOf('/');
			if (i == -1) {
				break; // Should not happen if uri starts with '/'
			}
			uri = uri.substring(0, i);

			if (endpoints.containsKey(uri)) {
				RestEndpoint endpoint = endpoints.get(uri);
				if (endpoint.isGlobal()) {
					return endpoint;
				}
			}
		}

		return null;
	}

}
