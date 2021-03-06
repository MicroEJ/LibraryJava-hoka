/*
 * Java
 *
 * Copyright 2016-2019 MicroEJ Corp. All rights reserved.
 * This library is provided in source code for use, modification and test, subject to license terms.
 * Any modification of the source code will break MicroEJ Corp. warranties on the whole library.
 */
package ej.hoka.rest;

import java.util.Map;

import ej.hoka.http.HTTPConstants;
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

	private static final String SLASH = "/"; //$NON-NLS-1$

	private static final String GLOBAL_POSTFIX = "/*"; //$NON-NLS-1$

	private static final HTTPResponse RESPONSE_NOT_IMPLEMENTED = HTTPResponse
			.createResponseFromStatus(HTTPConstants.HTTP_STATUS_NOTIMPLEMENTED);

	/**
	 * The URI this endpoint answers.
	 */
	protected String uri;

	private final boolean isGlobal;

	/**
	 * Creates a new endpoint at given URI.
	 * <p>
	 * For example, assuming a server running at {@code 127.0.0.1:80} with a REST request handler, following code
	 * creates an endpoint at {@code http://127.0.0.1:80/my/custom/endpoint}
	 *
	 * <pre>
	 * restRequestHandler.addEndpoint(new RestEndpoint("/my/custom/endpoint"));
	 * </pre>
	 * <p>
	 * If URI does not start with a {@code /} character, it is automatically added.
	 * <p>
	 * If URI ends with <code>/*</code>, the {@link RestEndpoint} created is a global endpoint and is able to process
	 * requests to sub-endpoints.
	 * <p>
	 * For example, an endpoint created with the "/my/custom/*" URI is able to process, among others, the requests to
	 * "/my/custom" and "/my/custom/endpoint".
	 *
	 * @param uri
	 *            the URI of this endpoint.
	 * @throws IllegalArgumentException
	 *             if URI is empty
	 */
	public RestEndpoint(String uri) {
		uri = uri.trim();

		if (uri.isEmpty()) {
			throw new IllegalArgumentException(
					Messages.BUILDER.buildMessage(Level.SEVERE, Messages.CATEGORY_HOKA, Messages.EMPTY_URI));
		}

		if (!uri.startsWith(SLASH)) {
			uri = SLASH + uri;
		}

		boolean isGlobal = uri.endsWith(GLOBAL_POSTFIX);

		if (isGlobal) {
			uri = uri.substring(0, uri.length() - 2);
		}

		this.isGlobal = isGlobal;

		this.uri = uri;
	}

	/**
	 * Gets this endpoint URI.
	 * <p>
	 * If this endpoint is global, the returned URI doesn't contain the trailing <code>/*</code> of the URI given to the
	 * constructor.
	 *
	 * @return this endpoint URI, cannot be null.
	 */
	public String getURI() {
		return this.uri;
	}

	/**
	 * Returns whether or not this endpoint is global, which means it can process sub-endpoints.
	 * <p>
	 * For example, a global <code>/my/custom/*</code> endpoint can process request to <code>/my/custom/endpoint</code>.
	 *
	 * @return {@code true} if this endpoint is global, {@code false} otherwise.
	 * @see #RestEndpoint(String)
	 */
	public boolean isGlobal() {
		return this.isGlobal;
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
		return RESPONSE_NOT_IMPLEMENTED;
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
		return RESPONSE_NOT_IMPLEMENTED;
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
		return RESPONSE_NOT_IMPLEMENTED;
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
		return RESPONSE_NOT_IMPLEMENTED;
	}

}
