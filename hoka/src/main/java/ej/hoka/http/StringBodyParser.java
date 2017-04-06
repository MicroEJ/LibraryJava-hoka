/*
 * Java
 *
 * Copyright 2017 IS2T. All rights reserved.
 * IS2T PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package ej.hoka.http;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * A body parser reading the full body and storing it in a string.
 */
public class StringBodyParser implements BodyParser {

	private static final int BUFFSIZE = 512;
	private String body;

	@Override
	public void parseBody(InputStream stream) throws IOException {
		StringBuilder builder = new StringBuilder(stream.available());
		InputStreamReader reader = new InputStreamReader(stream);

		char[] buf = new char[BUFFSIZE];
		int read = Math.min(BUFFSIZE, stream.available());
		while (read > 0) {
			read = reader.read(buf, 0, read);
			if (read != -1) {
				builder.append(buf, 0, read);
			}
			read = Math.min(BUFFSIZE, stream.available());
		}
		this.body = builder.toString();
	}

	/**
	 * Gets the body.
	 *
	 * @return the body.
	 */
	public String getBody() {
		return this.body;
	}

	@Override
	public String toString() {
		return this.body;
	}
}
