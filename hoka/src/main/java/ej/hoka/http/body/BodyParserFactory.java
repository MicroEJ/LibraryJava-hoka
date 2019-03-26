/*
 * Java
 *
 * Copyright 2017-2019 MicroEJ Corp. All rights reserved.
 * This library is provided in source code for use, modification and test, subject to license terms.
 * Any modification of the source code will break MicroEJ Corp. warranties on the whole library.
 */
package ej.hoka.http.body;

import ej.hoka.http.HTTPRequest;

/**
 *
 * Factory to instantiate BodyParser for each request.
 *
 */
public interface BodyParserFactory {
	/**
	 * @param request
	 *            the request to parse.
	 * @return a new instance of a {@link BodyParser}
	 */
	BodyParser newBodyParser(HTTPRequest request);
}
