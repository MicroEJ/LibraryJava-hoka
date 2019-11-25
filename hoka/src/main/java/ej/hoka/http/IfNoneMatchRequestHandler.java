/*
 * Java
 *
 * Copyright 2019 MicroEJ Corp. All rights reserved.
 * This library is provided in source code for use, modification and test, subject to license terms.
 * Any modification of the source code will break MicroEJ Corp. warranties on the whole library.
 */
package ej.hoka.http;

import ej.hoka.http.requesthandler.RequestHandler;

/**
 * <p>
 * Request handler used by {@link HTTPServer} to manage client cache. See <code>If-None-Match</code> HTTP header.
 * </p>
 */
class IfNoneMatchRequestHandler implements RequestHandler {

	/**
	 * <p>
	 * The instance of this stateless request handler.
	 * </p>
	 */
	public static final IfNoneMatchRequestHandler instance = new IfNoneMatchRequestHandler();

	@Override
	public HTTPResponse process(HTTPRequest request) {
		String etag = request.getHeaderField(HTTPConstants.FIELD_IF_NONE_MATCH);

		if (etag == null) {
			return null;
		}

		// TODO: Handle dynamic resources
		// Return a 304 status given to the "If-None-Match" field presence in the request.
		HTTPResponse response = new HTTPResponse();
		response.setStatus(HTTPConstants.HTTP_STATUS_NOTMODIFIED);
		return response;
	}

	private IfNoneMatchRequestHandler() {
		// Forbid instantiation
	}

}
