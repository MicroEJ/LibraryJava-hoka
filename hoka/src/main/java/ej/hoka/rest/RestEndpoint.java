/*
 * Java
 *
 * Copyright 2016-2019 MicroEJ Corp. All rights reserved.
 * This library is provided in source code for use, modification and test, subject to license terms.
 * Any modification of the source code will break MicroEJ Corp. warranties on the whole library.
 */
package ej.hoka.rest;

import java.util.Map;

import ej.hoka.http.HTTPRequest;
import ej.hoka.http.HTTPResponse;
import ej.hoka.log.Messages;
import ej.util.message.Level;

/**
 * A REST endpoint exposes resources.
 * <p>
 * Allow HTTP verbs are {@code GET}, {@code POST}, {@code PUT} and {@code DELETE}.
 *
 * @see RestRequestHandler
 */
public class RestEndpoint {

	private static final String ENDPOINT_PREFIX = "/"; //$NON-NLS-1$

	/**
	 * The URI this endpoint answers.
	 */
	protected String uri;

	/**
	 * Create a new endpoint at given URI.
	 * <p>
	 * For example, assuming a server running at {@code 127.0.0.1:80} with a REST request handler, following code
	 * creates an endpoint at {@code http://127.0.0.1:80/my/custom/endpoint}
	 *
	 * <pre>
	 * restRequestHandler.addEndpoint(new RestEndpoint("/my/custom/endpoint"));
	 * </pre>
	 * <p>
	 * If URI does not start with a {@code /} character, it is automatically added.
	 *
	 * @param uri
	 *            the URI of this endpoint.
	 * @throws IllegalArgumentException
	 *             if URI is empty
	 */
	public RestEndpoint(String uri) {
		if (uri == null) {
			throw new NullPointerException();
		}

		uri = uri.trim();

		if (uri.isEmpty()) {
			throw new IllegalArgumentException(
					Messages.BUILDER.buildMessage(Level.SEVERE, Messages.CATEGORY_REST, Messages.EMPTY_URI));
		}

		if (!uri.startsWith(ENDPOINT_PREFIX)) {
			uri = ENDPOINT_PREFIX + uri;
		}

		this.uri = uri;
	}

	/**
	 * Gets this endpoint URI.
	 *
	 * @return this endpoint URI.
	 */
	public String getURI() {
		return this.uri;
	}

	/**
	 * Handles {@code GET} request on this endpoint.
	 * <p>
	 * Default implementation return a status code {@code 501}
	 *
	 * @param request
	 *            the request to handle.
	 * @param attributes
	 *            the attributes populated by the request processing.
	 * @return an HTTP response.
	 */
	public HTTPResponse get(HTTPRequest request, Map<String, String> attributes) {
		return HTTPResponse.RESPONSE_NOT_IMPLEMENTED;
	}

	/**
	 * Handles {@code POST} request on this endpoint.
	 * <p>
	 * Default implementation return a status code {@code 501}
	 *
	 * @param request
	 *            the request to handle.
	 * @param attributes
	 *            the attributes populated by the request processing.
	 * @return an HTTP response.
	 */
	public HTTPResponse post(HTTPRequest request, Map<String, String> attributes) {
		return HTTPResponse.RESPONSE_NOT_IMPLEMENTED;
	}

	/**
	 * Handles {@code PUT} request on this endpoint.
	 * <p>
	 * Default implementation return a status code {@code 501}
	 *
	 * @param request
	 *            the request to handle.
	 * @param attributes
	 *            the attributes populated by the request processing.
	 * @return an HTTP response.
	 */
	public HTTPResponse put(HTTPRequest request, Map<String, String> attributes) {
		return HTTPResponse.RESPONSE_NOT_IMPLEMENTED;
	}

	/**
	 * Handles {@code DELETE} request on this endpoint.
	 * <p>
	 * Default implementation return a status code {@code 501}
	 *
	 * @param request
	 *            the request to handle.
	 * @param attributes
	 *            the attributes populated by the request processing.
	 * @return an HTTP response.
	 */
	public HTTPResponse delete(HTTPRequest request, Map<String, String> attributes) {
		return HTTPResponse.RESPONSE_NOT_IMPLEMENTED;
	}

}
