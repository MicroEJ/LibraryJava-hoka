/*
 * Java
 *
 * Copyright 2009-2016 IS2T. All rights reserved.
 * IS2T PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package ej.hoka.http;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import ej.hoka.io.ChunkedMessageBodyInputStream;
import ej.hoka.io.ChunkedMessageBodyOutputStream;

/**
 * <p>
 * HTTP-1.1 chunked transfer encoding handler to read and write data in chunked encoding.<br>
 * </p>
 */
public class ChunkedTransferCodingHandler implements IHTTPTransferCodingHandler {

	/**
	 * Instance to use in factory method.
	 */
	private static ChunkedTransferCodingHandler Instance;

	/**
	 * <p>
	 * Factory method to create an instance of ChunkedTransferCodingHandler.
	 * </p>
	 *
	 * @return an instance of {@link ChunkedTransferCodingHandler}
	 */
	public static ChunkedTransferCodingHandler getInstance() {
		if (Instance == null) {
			Instance = new ChunkedTransferCodingHandler();
		}
		return Instance;
	}

	/**
	 * Private default constructor to avoid direct instantiation.
	 */
	private ChunkedTransferCodingHandler() {
		// private constructor, because of singleton behaviour
	}

	// NOTES:
	// This encoding has been first encountered with AXIS2 client
	// Internal headers and extension are skipped (not used by AXIS2)
	/**
	 * <p>
	 * Returns the internal ID of the {@link ChunkedTransferCodingHandler}.
	 *
	 * </p>
	 *
	 * @return the String "chunked".
	 */
	@Override
	public String getId() {
		return "chunked"; //$NON-NLS-1$
	}

	/**
	 * <p>
	 * Creates a {@link ChunkedMessageBodyInputStream} to read the body of the HTTP request in "chunked" encoding from
	 * the {@link HTTPRequest} and the {@link InputStream}.
	 * </p>
	 *
	 * @param request
	 *            the {@link HTTPRequest}
	 * @param input
	 *            the {@link InputStream}
	 *
	 * @return a new instance of {@link ChunkedMessageBodyInputStream}
	 * @throws IOException
	 *             when I/O error occurs
	 */
	@Override
	public InputStream open(HTTPRequest request, InputStream input) throws IOException {
		return new ChunkedMessageBodyInputStream(input);
	}

	/**
	 * <p>
	 * Creates an {@link OutputStream} to write the body of the HTTP response in "chunked" encoding using the
	 * {@link HTTPResponse} and the {@link OutputStream}.
	 * </p>
	 *
	 * @param response
	 *            the {@link HTTPResponse}
	 * @param output
	 *            the {@link OutputStream}
	 * @return a new instance of {@link ChunkedMessageBodyOutputStream}
	 * @throws IOException
	 *             if an I/O error occurs
	 */
	@Override
	public OutputStream open(HTTPResponse response, OutputStream output) throws IOException {
		return new ChunkedMessageBodyOutputStream(output);
	}

}