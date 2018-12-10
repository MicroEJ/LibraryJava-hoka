/*
 * Java
 *
 * Copyright 2017-2018 IS2T. All rights reserved.
 * IS2T PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
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
