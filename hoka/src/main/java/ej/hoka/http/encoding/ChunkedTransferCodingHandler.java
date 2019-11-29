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

import ej.hoka.http.HTTPRequest;
import ej.hoka.http.HTTPResponse;
import ej.hoka.io.ChunkedMessageBodyInputStream;
import ej.hoka.io.ChunkedMessageBodyOutputStream;

/**
 * HTTP-1.1 chunked transfer encoding handler to read and write data in chunked encoding.
 */
public class ChunkedTransferCodingHandler implements IHTTPTransferCodingHandler {

	/**
	 * Instance to use in factory method.
	 */
	private static ChunkedTransferCodingHandler instance;

	/**
	 * Private default constructor to avoid direct instantiation.
	 */
	private ChunkedTransferCodingHandler() {
		// private constructor, because of singleton behavior
	}

	/**
	 * Factory method to create an instance of ChunkedTransferCodingHandler.
	 *
	 * @return an instance of {@link ChunkedTransferCodingHandler}
	 */
	public static ChunkedTransferCodingHandler getInstance() {
		if (instance == null) {
			instance = new ChunkedTransferCodingHandler();
		}
		return instance;
	}

	// NOTES:
	// This encoding has been first encountered with AXIS2 client
	// Internal headers and extension are skipped (not used by AXIS2)
	/**
	 * Returns the internal ID of the {@link ChunkedTransferCodingHandler}.
	 *
	 * @return the String "chunked".
	 */
	@Override
	public String getId() {
		return "chunked"; //$NON-NLS-1$
	}

	/**
	 * Creates a {@link ChunkedMessageBodyInputStream} to read the body of the HTTP request in "chunked" encoding from
	 * the {@link HTTPRequest} and the {@link InputStream}.
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
	 * Creates an {@link OutputStream} to write the body of the HTTP response in "chunked" encoding using the
	 * {@link HTTPResponse} and the {@link OutputStream}.
	 *
	 * @param response
	 *            the {@link HTTPResponse}.
	 * @param output
	 *            the {@link OutputStream}.
	 * @return a new instance of {@link ChunkedMessageBodyOutputStream}.
	 * @throws IOException
	 *             if an I/O error occurs.
	 */
	@Override
	public OutputStream open(HTTPResponse response, OutputStream output) throws IOException {
		return new ChunkedMessageBodyOutputStream(output);
	}

}
