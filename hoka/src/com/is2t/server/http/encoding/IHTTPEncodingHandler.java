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

import com.is2t.server.http.HTTPServer;

/**
 * IS2T-API
 * <p>
 * Interface for defining HTTP encoding handlers. A HTTP encoding handler is
 * able to decode data from an {@link InputStream} and encode data to an
 * {@link OutputStream}.
 * </p>
 * <p>
 * Encodings are specified in HTTP headers such as <code>content-encoding</code>, <code>transfer-encoding</code>, <code>accept-encoding</code>.
 * </p>
 * <p>
 * Encoding handlers should be registered in the {@link HTTPServer} in order to
 * use them.
 * </p>
 * 
 * @see HTTPServer#registerEncodingHandler(IHTTPEncodingHandler)
 * 
 */
public interface IHTTPEncodingHandler {

	/**
	 * IS2T-API
	 * <p>
	 * Returns the name of the supported encoding.
	 * </p>
	 * 
	 * @return an internal {@link String} in lower case format.
	 */
	public String getId();

	/**
	 * IS2T-API
	 * <p>
	 * Returns an {@link InputStream} to read the decoded data from the
	 * <code>original</code> {@link InputStream}.
	 * </p>
	 * 
	 * @param original
	 *            the {@link InputStream} to read the encoded data.
	 * @return the {@link InputStream} to read the decoded data.
	 * @throws IOException
	 *             if any I/O error occurs
	 */
	public InputStream open(InputStream original) throws IOException;

	/**
	 * IS2T-API
	 * <p>
	 * Wraps the <code>original</code> {@link OutputStream} with a special
	 * {@link OutputStream} which performs the encoding. Returns an
	 * {@link OutputStream} to encode the data from the <code>original</code>
	 * {@link OutputStream}.
	 * </p>
	 * 
	 * @param original
	 *            the output stream to wrap
	 * @return the {@link OutputStream} to encode the data.
	 * @throws IOException
	 *             if any I/O error occurs
	 */
	public OutputStream open(OutputStream original) throws IOException;

}
