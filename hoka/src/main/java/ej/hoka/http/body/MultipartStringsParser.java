/*
 * Java
 *
 * Copyright 2019 MicroEJ Corp. All rights reserved.
 * Use of this source code is governed by a BSD-style license that can be found with this software.
 */
package ej.hoka.http.body;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import ej.hoka.http.support.MIMEUtils;

/**
 * A body parser for {@link MIMEUtils#MIME_MULTIPART_FORM_ENCODED_DATA} that parse parts as strings.
 */
public class MultipartStringsParser implements BodyParser<String[]> {

	@Override
	public String[] parseBody(InputStream inputStream, String contentType) throws IOException {
		if ((contentType != null) && contentType.startsWith(MIMEUtils.MIME_MULTIPART_FORM_ENCODED_DATA)) {
			String boundary = contentType.substring(contentType.indexOf(';') + 1);

			boundary = boundary
					.substring(boundary.indexOf(MultiPartBodyParser.BOUNDARY) + MultiPartBodyParser.BOUNDARY.length());
			String multipartBody = new StringBodyParser().parseBody(inputStream, null);
			return split(multipartBody, boundary);
		}

		throw new IllegalArgumentException();
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
