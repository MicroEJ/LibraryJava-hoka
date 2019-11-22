/*
 * Java
 *
 * Copyright 2019 MicroEJ Corp. All rights reserved.
 * This library is provided in source code for use, modification and test, subject to license terms.
 * Any modification of the source code will break MicroEJ Corp. warranties on the whole library.
 */
package ej.hoka.http.requesthandler;

import ej.hoka.http.HTTPConstants;
import ej.hoka.http.HTTPErrorException;
import ej.hoka.http.HTTPRequest;
import ej.hoka.http.HTTPResponse;

public class NotFoundRequestHandler implements RequestHandler {

	@Override
	public HTTPResponse process(HTTPRequest request) throws HTTPErrorException {
		throw new HTTPErrorException(HTTPConstants.HTTP_STATUS_NOTFOUND, request.getURI());
	}

}
