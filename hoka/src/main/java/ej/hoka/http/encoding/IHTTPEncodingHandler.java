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

import ej.hoka.http.HTTPServer;

/**
 * <p>
 * Interface for defining HTTP encoding handlers. A HTTP encoding handler is able to decode data from an
 * {@link InputStream} and encode data to an {@link OutputStream}.
 * </p>
 * <p>
 * Encodings are specified in HTTP headers such as <code>content-encoding</code>, <code>transfer-encoding</code>,
 * <code>accept-encoding</code>.
 * </p>
 * <p>
 * Encoding handlers should be registered in the {@link HTTPServer} in order to use them.
 * </p>
 *
 * @see HTTPEncodingRegistry#registerEncodingHandler(IHTTPEncodingHandler)
 *
 */
public interface IHTTPEncodingHandler {

	/**
	 * <p>
	 * Returns the name of the supported encoding.
	 * </p>
	 *
	 * @return an internal {@link String} in lower case format.
	 */
	String getId();

	/**
	 * <p>
	 * Returns an {@link InputStream} to read the decoded data from the <code>original</code> {@link InputStream}.
	 * </p>
	 *
	 * @param original
	 *            the {@link InputStream} to read the encoded data.
	 * @return the {@link InputStream} to read the decoded data.
	 * @throws IOException
	 *             if any I/O error occurs
	 */
	InputStream open(InputStream original) throws IOException;

	/**
	 * <p>
	 * Wraps the <code>original</code> {@link OutputStream} with a special {@link OutputStream} which performs the
	 * encoding. Returns an {@link OutputStream} to encode the data from the <code>original</code> {@link OutputStream}.
	 * </p>
	 *
	 * @param original
	 *            the output stream to wrap
	 * @return the {@link OutputStream} to encode the data.
	 * @throws IOException
	 *             if any I/O error occurs
	 */
	OutputStream open(OutputStream original) throws IOException;

}
