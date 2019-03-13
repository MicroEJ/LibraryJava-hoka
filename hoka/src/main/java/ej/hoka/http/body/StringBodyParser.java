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
import java.util.ArrayList;
import java.util.List;

import ej.hoka.http.HTTPConstants;
import ej.hoka.http.HTTPRequest;
import ej.hoka.http.support.MIMEUtils;

/**
 * A body parser reading the full body and storing it in a string. The raw buffer of the body is appended into a String.
 */
public class StringBodyParser implements BodyParser {

	private static final int BUFFSIZE = 512;
	private String body;

	private String[] parts;
	private boolean isMultipartFormEncoded;

	@Override
	public void parseBody(HTTPRequest httpRequest) throws IOException {
		InputStream inputStream = httpRequest.getStream();
		String contentType = httpRequest.getHeaderField(HTTPConstants.FIELD_CONTENT_TYPE);
		// 3. the body contains a multipart form encoded
		if ((contentType != null) && contentType.startsWith(MIMEUtils.MIME_MULTIPART_FORM_ENCODED_DATA)) {
			String boundary = contentType.substring(contentType.indexOf(';') + 1);

			boundary = boundary
					.substring(boundary.indexOf(MultiPartBodyParser.BOUNDARY) + MultiPartBodyParser.BOUNDARY.length());
			String multipartBody = read(inputStream);
			this.parts = split(multipartBody, boundary);
			this.isMultipartFormEncoded = true;
		} else {
			this.body = read(inputStream);
		}
	}

	/**
	 * Gets the body.
	 *
	 * @return the body.
	 */
	public String getBody() {
		return this.body;
	}

	/**
	 * The request contains multipart form encoded.
	 *
	 * @return true if the request has some form encoded multiparts.
	 */
	public boolean isMultipartFormEncoded() {
		return this.isMultipartFormEncoded;
	}

	/**
	 * The multiparts. The parts should not be modified.
	 *
	 * @return the parts if the request has some form encoded multiparts, null otherwise.
	 */
	public String[] parts() {
		return this.parts;
	}

	@Override
	public String toString() {
		return this.body;
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

	private static String[] split(String toSplit, String separator) {
		int index = toSplit.indexOf(separator);
		List<String> parts = new ArrayList<String>();

		while (index > -1) {
			int indexEnd = toSplit.indexOf(separator, index + separator.length());

			if (indexEnd != -1) {
				parts.add(toSplit.substring(index + separator.length() + 2, indexEnd - 4));
			} else {
				parts.add(toSplit.substring(index + separator.length() + 2, toSplit.length() - 2));
			}
			index = indexEnd;
		}

		return parts.toArray(new String[parts.size()]);
	}
}
