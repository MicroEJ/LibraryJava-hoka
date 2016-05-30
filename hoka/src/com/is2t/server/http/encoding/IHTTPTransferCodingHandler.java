/**
 * Java
 *
 * Copyright 2009-2013 IS2T. All rights reserved.
 * IS2T PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.is2t.server.http.encoding;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.is2t.server.http.HTTPRequest;
import com.is2t.server.http.HTTPResponse;
import com.is2t.server.http.HTTPServer;

/**
 * IS2T-API
 * <p>
 * Interface for defining HTTP transfer coding handlers.
 * </p>
 * The HTTP transfer coding handler decodes data from the body of a
 * {@link HTTPRequest} and encodes the body of a {@link HTTPResponse}. </p>
 * <p>
 * Transfer coding is specified in <code>transfer-encoding</code> HTTP header.
 * </p>
 * <p>
 * Encoding handlers should be registered in the {@link HTTPServer} in order to
 * use them.
 * </p>
 * 
 * @see HTTPServer#registerTransferCodingHandler(IHTTPTransferCodingHandler)
 * 
 */
public interface IHTTPTransferCodingHandler {

	/**
	 * IS2T-API
	 * <p>
	 * Returns the supported encoding id.
	 * </p>
	 * 
	 * @return an internal {@link String} in lower case format.
	 */
	public String getId();

	/**
	 * IS2T-API
	 * <p>
	 * Opens an {@link InputStream} that can be used to decode message body of
	 * the given request. The returned {@link InputStream} MUST conforms to the
	 * followings:
	 * <ul>
	 * <li>
	 * The {@link InputStream} MUST reach the EOF (i.e. read methods returns
	 * <code>-1</code>) when the request body has been completely read.</li>
	 * <li>
	 * The {@link InputStream#close()} method MUST read any remaining bytes from
	 * the message body (if any) and MUST NOT close the underlying stream.</li>
	 * </ul>
	 * </p>
	 * 
	 * @param request
	 *            the {@link HTTPRequest} to be decoded by this transfer coding
	 *            handler.
	 * @param input
	 *            the {@link InputStream} from which encoded message body can be
	 *            read.
	 * @return the {@link InputStream} used to decode message body of the given request
	 * @throws IOException
	 *             if any I/O Error occurs
	 */
	public InputStream open(HTTPRequest request, InputStream input)
			throws IOException;

	/**
	 * IS2T-API
	 * <p>
	 * Opens an {@link OutputStream} that can be used to encode the message body of
	 * the {@link HTTPResponse}.
	 * </p>
	 * 
	 * @param response
	 *            the {@link HTTPResponse} to be encoded by this transfer coding
	 *            handler.
	 * @param output
	 *            the {@link OutputStream} where the encoded message body is
	 *            written.
	 * @return the output stream used to encode message body of the given
	 *         response
	 * @throws IOException
	 *             if any I/O Error occurs
	 */
	public OutputStream open(HTTPResponse response, OutputStream output)
			throws IOException;

}