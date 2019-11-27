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
import ej.hoka.http.HTTPServer;

/**
 * <p>
 * Interface for defining HTTP transfer coding handlers.
 * </p>
 * <p>
 * The HTTP transfer coding handler decodes data from the body of a {@link HTTPRequest} and encodes the body of a
 * {@link HTTPResponse}.
 * </p>
 * <p>
 * Transfer coding is specified in <code>transfer-encoding</code> HTTP header.
 * </p>
 * <p>
 * Encoding handlers should be registered in the {@link HTTPServer} in order to use them.
 * </p>
 *
 * @see HTTPEncodingRegistry#registerTransferCodingHandler(IHTTPTransferCodingHandler)
 *
 */
public interface IHTTPTransferCodingHandler {

	/**
	 * <p>
	 * Returns the supported encoding id.
	 * </p>
	 *
	 * @return an internal {@link String} in lower case format.
	 */
	String getId();

	/**
	 * Opens an {@link InputStream} that can be used to decode message body of the given request. The returned
	 * {@link InputStream} MUST conforms to the followings:
	 * <ul>
	 * <li>The {@link InputStream} MUST reach the EOF (i.e. read methods returns <code>-1</code>) when the request body
	 * has been completely read.</li>
	 * <li>The {@link InputStream#close()} method MUST read any remaining bytes from the message body (if any) and MUST
	 * NOT close the underlying stream.</li>
	 * </ul>
	 *
	 * @param request
	 *            the {@link HTTPRequest} to be decoded by this transfer coding handler.
	 * @param input
	 *            the {@link InputStream} from which encoded message body can be read.
	 * @return the {@link InputStream} used to decode message body of the given request
	 * @throws IOException
	 *             if any I/O Error occurs
	 */
	InputStream open(HTTPRequest request, InputStream input) throws IOException;

	/**
	 * <p>
	 * Opens an {@link OutputStream} that can be used to encode the message body of the {@link HTTPResponse}.
	 * </p>
	 *
	 * @param response
	 *            the {@link HTTPResponse} to be encoded by this transfer coding handler.
	 * @param output
	 *            the {@link OutputStream} where the encoded message body is written.
	 * @return the output stream used to encode message body of the given response
	 * @throws IOException
	 *             if any I/O Error occurs
	 */
	OutputStream open(HTTPResponse response, OutputStream output) throws IOException;

}
