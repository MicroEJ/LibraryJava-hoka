/*
 * Java
 *
 * Copyright 2017 IS2T. All rights reserved.
 * IS2T PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package ej.hoka.http;

import java.io.IOException;
import java.io.InputStream;

/**
 * A parser called to read the body.
 */
public interface BodyParser {

	/**
	 * Parse the body.
	 *
	 * @param stream
	 *            the stream initialized at the start of the body.
	 * @throws IOException
	 *             when an {@link IOException} occurs during the parsing.
	 */
	public void parseBody(InputStream stream) throws IOException;

}
