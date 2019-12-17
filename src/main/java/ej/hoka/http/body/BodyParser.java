/*
 * Java
 *
 * Copyright 2017-2019 MicroEJ Corp. All rights reserved.
 * This library is provided in source code for use, modification and test, subject to license terms.
 * Any modification of the source code will break MicroEJ Corp. warranties on the whole library.
 */
package ej.hoka.http.body;

import java.io.IOException;
import java.io.InputStream;

import ej.hoka.http.HTTPRequest;

/**
 * A parser called to read the body of an {@link HTTPRequest}.
 *
 * @param <T>
 *            the body type.
 */
public interface BodyParser<T> {

	/**
	 * Parse the body.
	 *
	 * @param inputStream
	 *            the body {@link InputStream}.
	 * @param contentType
	 *            the <code>"content-type"</code> header of the request.
	 * @return the body.
	 * @throws IOException
	 *             when an {@link IOException} occurs during the parsing.
	 */
	T parseBody(InputStream inputStream, String contentType) throws IOException;

}
