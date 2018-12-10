/*
 * Java
 *
 * Copyright 2017-2018 IS2T. All rights reserved.
 * IS2T PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
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
