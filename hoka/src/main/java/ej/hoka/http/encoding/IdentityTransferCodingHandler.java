/*
 * Java
 *
 * Copyright 2009-2019 MicroEJ Corp. All rights reserved.
 * This library is provided in source code for use, modification and test, subject to license terms.
 * Any modification of the source code will break MicroEJ Corp. warranties on the whole library.
 */
package ej.hoka.http.encoding;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import ej.hoka.http.HTTPConstants;
import ej.hoka.http.HTTPRequest;
import ej.hoka.http.HTTPResponse;
import ej.hoka.io.IdentityMessageBodyInputStream;
import ej.hoka.io.IdentityMessageBodyOutputStream;

/**
 * Identity transfer coding handler.
 */
public class IdentityTransferCodingHandler implements IHTTPTransferCodingHandler {

	/**
	 * The static instance to use in factory method.
	 */
	private static IdentityTransferCodingHandler instance;

	/**
	 * The private constructor to prevent direct instantiation.
	 */
	private IdentityTransferCodingHandler() {
		// private constructor, because of singleton behaviour
	}

	/**
	 * Returns an instance of {@link IdentityTransferCodingHandler}.
	 *
	 * @return an instance of {@link IdentityTransferCodingHandler}
	 */
	public static IdentityTransferCodingHandler getInstance() {
		if (instance == null) {
			instance = new IdentityTransferCodingHandler();
		}
		return instance;
	}

	/**
	 * Returns an internal ID of this encoding handler.
	 *
	 * @return null
	 */
	@Override
	public String getId() {
		return null;
	}

	/**
	 * Creates a new instance of {@link IdentityMessageBodyInputStream} to read the message body of the HTTP request.
	 *
	 * @see IdentityMessageBodyInputStream
	 * @param request
	 *            the HTTP request object.
	 * @param input
	 *            the input stream to read the message body of the HTTP request.
	 * @return {@link IdentityMessageBodyOutputStream}.
	 * @throws IOException
	 *             if an I/O error occurs.
	 */
	@Override
	public InputStream open(HTTPRequest request, InputStream input) throws IOException {
		int bodyLength = 0;
		String contentLength = request.getHeaderField(HTTPConstants.FIELD_CONTENT_LENGTH);
		if (contentLength != null) {
			bodyLength = Integer.parseInt(contentLength);
		}
		return new IdentityMessageBodyInputStream(input, bodyLength);
	}

	/**
	 * Creates an {@link IdentityMessageBodyOutputStream} to write the message body of the HTTP response.
	 *
	 * @see IdentityMessageBodyOutputStream
	 * @param response
	 *            the {@link HTTPResponse}
	 * @param output
	 *            the {@link OutputStream} to write the message body of the HTTP response
	 * @return {@link IdentityMessageBodyOutputStream}
	 * @throws IOException
	 *             when I/O error occurs
	 */
	@Override
	public OutputStream open(HTTPResponse response, OutputStream output) throws IOException {
		return new IdentityMessageBodyOutputStream(output);
	}

}
