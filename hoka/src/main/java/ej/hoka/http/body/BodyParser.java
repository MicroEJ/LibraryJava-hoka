/*
 * Java
 *
 * Copyright 2017-2019 MicroEJ Corp. All rights reserved.
 * This library is provided in source code for use, modification and test, subject to license terms.
 * Any modification of the source code will break MicroEJ Corp. warranties on the whole library.
 */
package ej.hoka.http.body;

import java.io.IOException;

import ej.hoka.http.HTTPRequest;

/**
 * A parser called to read the body.
 */
public interface BodyParser {

	/**
	 * Parse the body.
	 *
	 * @param httpRequest
	 *            the HttpRequest with the headers parsed, the stream is at the start of the body.
	 * @throws IOException
	 *             when an {@link IOException} occurs during the parsing.
	 */
	void parseBody(HTTPRequest httpRequest) throws IOException;

}
