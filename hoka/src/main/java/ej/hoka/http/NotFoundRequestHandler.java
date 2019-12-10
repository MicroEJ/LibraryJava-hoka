/*
 * Java
 *
 * Copyright 2019 MicroEJ Corp. All rights reserved.
 * This library is provided in source code for use, modification and test, subject to license terms.
 * Any modification of the source code will break MicroEJ Corp. warranties on the whole library.
 */
package ej.hoka.http;

import java.util.Map;

import ej.hoka.http.requesthandler.RequestHandler;

/**
 * Request handler used by {@link HTTPServer} to reply with a <code>"404 Not Found"</code> response as the last request
 * handler.
 */
class NotFoundRequestHandler implements RequestHandler {

	/**
	 * The instance of this stateless request handler.
	 */
	static final NotFoundRequestHandler instance = new NotFoundRequestHandler();

	private static final HTTPResponse RESPONSE_NOT_FOUND = HTTPResponse
			.createResponseFromStatus(HTTPConstants.HTTP_STATUS_NOTFOUND);

	private NotFoundRequestHandler() {
		// Forbid instantiation
	}

	@Override
	public HTTPResponse process(HTTPRequest request, Map<String, String> attributes) {
		return RESPONSE_NOT_FOUND;
	}

}
