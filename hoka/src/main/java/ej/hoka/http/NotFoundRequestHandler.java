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
 * Request handler used by {@link HTTPServer} to reply with a <code>"404 Not Found"</code> response as the last request
 * handler.
 * </p>
 */
class NotFoundRequestHandler implements RequestHandler {

	/**
	 * <p>
	 * The instance of this stateless request handler.
	 * </p>
	 */
	public static final NotFoundRequestHandler instance = new NotFoundRequestHandler();

	@Override
	public HTTPResponse process(HTTPRequest request) {
		return HTTPResponse.RESPONSE_NOT_FOUND;
	}

	private NotFoundRequestHandler() {
		// Forbid instantiation
	}

}
