/*
 * Java
 *
 * Copyright 2017 IS2T. All rights reserved.
 * IS2T PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
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
