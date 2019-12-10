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
import java.io.InputStreamReader;

/**
 * A body parser reading the full body and storing it in a string. The raw buffer of the body is appended into a String.
 */
public class StringBodyParser implements BodyParser<String> {

	private static final int BUFFSIZE = 512;

	@Override
	public String parseBody(InputStream inputStream, String contentType) throws IOException {
		return read(inputStream);
	}

	private static String read(InputStream stream) throws IOException {
		StringBuilder body = new StringBuilder(stream.available());

		int readLen = -1;
		char[] buff = new char[BUFFSIZE];
		try (InputStreamReader reader = new InputStreamReader(stream)) {
			while (true) {
				readLen = reader.read(buff);
				if (readLen == -1) {
					break;
				}

				body.append(buff, 0, readLen);
			}
		}
		buff = null;
		return body.toString();
	}

}
