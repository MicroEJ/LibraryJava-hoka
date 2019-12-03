/*
 * Java
 *
 * Copyright 2019 MicroEJ Corp. All rights reserved.
 * This library is provided in source code for use, modification and test, subject to license terms.
 * Any modification of the source code will break MicroEJ Corp. warranties on the whole library.
 */
package ej.hoka.auth.session;

import ej.hoka.auth.SessionAuthenticator;
import ej.hoka.http.HTTPRequest;
import ej.hoka.rest.RestEndpoint;
import ej.hoka.rest.RestRequestHandler;

/**
 * An implementation of {@link AuthenticatedRequestHandler} for REST.
 * <p>
 * Uses a root URI like <code>/private/</code> so that only request with URIs that have this root as prefix are
 * considered.
 * <p>
 * For example, an {@link AuthenticatedRequestHandler} with <code>/private/</code> root handles a request to
 * <code>/private/my/endpoint</code> but not a request to <code>/public/another/endpoint</code>.
 */
public class RestAuthenticatedRequestHandler extends AuthenticatedRequestHandler {

	private final RestRequestHandler endpointHandler;

	private final String root;

	/**
	 * Constructs the REST request handler.
	 *
	 * @param authenticator
	 *            the {@link SessionAuthenticator} used to authenticate users.
	 * @param root
	 *            the URI root used to match the request.
	 */
	public RestAuthenticatedRequestHandler(SessionAuthenticator authenticator, String root) {
		super(authenticator);

		this.endpointHandler = new RestRequestHandler();
		this.root = root;
	}

	/**
	 * Add an endpoint to this handler. The URI of the endpoint must be prefixed by <code>root</code>.
	 *
	 * @param endpoint
	 *            the {@link RestEndpoint} to add.
	 * @throws IllegalArgumentException
	 *             if the endpoint URI isn't prefixed by <code>root</code>.
	 * @see RestAuthenticatedRequestHandler#RestAuthenticatedRequestHandler(SessionAuthenticator, String)
	 */
	public void addEndpoint(RestEndpoint endpoint) {
		if (!endpoint.getURI().startsWith(this.root)) {
			throw new IllegalArgumentException();
		}
		this.endpointHandler.addEndpoint(endpoint);
	}

	@Override
	protected boolean match(HTTPRequest request) {
		return request.getURI().startsWith(this.root);
	}

}