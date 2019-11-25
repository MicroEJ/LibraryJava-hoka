/*
 * Java
 *
 * Copyright 2019 MicroEJ Corp. All rights reserved.
 * This library is provided in source code for use, modification and test, subject to license terms.
 * Any modification of the source code will break MicroEJ Corp. warranties on the whole library.
 */
package ej.hoka.http.requesthandler;

import ej.hoka.http.HTTPRequest;
import ej.hoka.http.HTTPResponse;

/**
 * <p>
 * A handler to process request and create appropriate response.
 * </p>
 */
public interface RequestHandler {

	/**
	 * Processes the request and creates the appropriate response, or null if this request handler doesn't match the
	 * request.
	 *
	 * @param request
	 *            the {@link HTTPRequest} to process.
	 * @return the {@link HTTPResponse} to send, or null if not handled by this {@link RequestHandler}.
	 */
	HTTPResponse process(HTTPRequest request);

}
