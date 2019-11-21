/*
 * Java
 *
 * Copyright 2019 MicroEJ Corp. All rights reserved.
 * This library is provided in source code for use, modification and test, subject to license terms.
 * Any modification of the source code will break MicroEJ Corp. warranties on the whole library.
 */
package ej.hoka.http.requesthandler;

import ej.hoka.http.HTTPConstants;
import ej.hoka.http.HTTPRequest;
import ej.hoka.http.HTTPResponse;

public class IfNoneMatchRequestHandler implements RequestHandler {

	@Override
	public HTTPResponse process(HTTPRequest request) {
		// Build response or return a 304 status given to the "If-None-Match" field presence in the request.
		String etag = request.getHeaderField(HTTPConstants.FIELD_IF_NONE_MATCH);
		if (etag != null) {
			HTTPResponse response = new HTTPResponse();
			response.setStatus(HTTPConstants.HTTP_STATUS_NOTMODIFIED);
			return response;
		}

		return null;
	}

}
