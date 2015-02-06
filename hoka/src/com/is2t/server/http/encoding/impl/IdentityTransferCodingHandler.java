/**
 * Java
 *
 * Copyright 2009-2015 IS2T. All rights reserved.
 * IS2T PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.is2t.server.http.encoding.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.is2t.server.http.HTTPConstants;
import com.is2t.server.http.HTTPRequest;
import com.is2t.server.http.HTTPResponse;
import com.is2t.server.http.encoding.IHTTPTransferCodingHandler;

/**
 * IS2T-API
 * <p>
 * Identity transfer coding handler.
 * </p>
 */
public class IdentityTransferCodingHandler implements
		IHTTPTransferCodingHandler {

	/**
	 * IS2T-API
	 * <p>
	 * Returns an instance of {@link IdentityTransferCodingHandler}.
	 * </p>
	 * 
	 * @return an instance of {@link IdentityTransferCodingHandler}
	 */
	public static IdentityTransferCodingHandler getInstance() {
		if (Instance == null) {
			Instance = new IdentityTransferCodingHandler();
		}
		return Instance;
	}

	/**
	 * IS2T-API
	 * <p>
	 * Returns an internal ID of this encoding handler.
	 * </p>
	 * 
	 * @return null
	 */
	public String getId() {
		return null;
	}

	/**
	 * IS2T-API
	 * <p>
	 * Creates a new instance of {@link IdentityMessageBodyInputStream} to read
	 * the message body of the HTTP request.
	 * </p>
	 * 
	 * @see IdentityMessageBodyInputStream
	 * @param request
	 *            the HTTP request object
	 * @param input
	 *            the input stream to read the message body of the HTTP request
	 * @return {@link IdentityMessageBodyOutputStream}
	 * @throws IOException
	 *             if an I/O error occurs
	 */
	public InputStream open(HTTPRequest request, InputStream input)
			throws IOException {
		int bodyLength = 0;
		String contentLength = request
				.getHeaderField(HTTPConstants.FIELD_CONTENT_LENGTH);
		if (contentLength != null) {
			bodyLength = Integer.parseInt(contentLength);
		}
		return new IdentityMessageBodyInputStream(input, bodyLength);
	}

	/**
	 * IS2T-API
	 * <p>
	 * Creates an {@link IdentityMessageBodyOutputStream} to write the message
	 * body of the HTTP response.
	 * </p>
	 * 
	 * @see IdentityMessageBodyOutputStream
	 * @param response
	 *            the {@link HTTPResponse}
	 * @param output
	 *            the {@link OutputStream} to write the message body of the HTTP
	 *            response
	 * @return {@link IdentityMessageBodyOutputStream}
	 * @throws IOException
	 *             when I/O error occurs
	 */
	public OutputStream open(HTTPResponse response, OutputStream output)
			throws IOException {
		return new IdentityMessageBodyOutputStream(output);
	}

	/*************************************************************************
	 * NOT IN API
	 ************************************************************************/

	/**
	 * The static instance to use in factory method.
	 */
	private static IdentityTransferCodingHandler Instance;

	/**
	 * The private constructor to prevent direct instantiation.
	 */
	private IdentityTransferCodingHandler() {
		// private constructor, because of singleton behaviour
	}

}
