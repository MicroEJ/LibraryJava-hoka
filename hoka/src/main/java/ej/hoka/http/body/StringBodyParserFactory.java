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
 * Factory that instantiates new {@link StringBodyParser}.
 */
public class StringBodyParserFactory implements BodyParserFactory {

	@Override
	public BodyParser newBodyParser(HTTPRequest request) {
		return new StringBodyParser();
	}

}
