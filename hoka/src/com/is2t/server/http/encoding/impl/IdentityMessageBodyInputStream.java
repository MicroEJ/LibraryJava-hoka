/**
 * Java
 *
 * Copyright 2009-2015 IS2T. All rights reserved.
 * IS2T PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.is2t.server.http.encoding.impl;

import java.io.IOException;
import java.io.InputStream;

/**
 * IS2T-API
 * <p>
 * Identity input stream. Wraps an {@link InputStream} and all of the operations
 * on {@link IdentityMessageBodyInputStream} are delegated to this underlying
 * {@link InputStream}.
 * </p>
 */
public class IdentityMessageBodyInputStream extends InputStream {

	/**
	 * IS2T-API
	 * <p>
	 * Creates a new instance of {@link IdentityMessageBodyInputStream} with the
	 * {@link InputStream} <code>is</code> with the predefined length
	 * <code>bodyLength</code>. The <code>bodyLength</code> should be the
	 * maximum number of bytes can be read from the InputStream.
	 * </p>
	 * 
	 * @param is
	 *            the underlying {@link InputStream} to read the body content of
	 *            the HTTP message body
	 * @param bodyLength
	 *            the number of bytes can be read from the underlying
	 *            InputStream.
	 */
	public IdentityMessageBodyInputStream(final InputStream is,
			final int bodyLength) {
		this.is = is;
		remainingBytes = bodyLength;
	}

	/**
	 * IS2T-API
	 * <p>
	 * Returns the number of bytes that can be read (or skipped over) from this
	 * input stream without blocking.
	 * </p>
	 * 
	 * @return the number of available bytes could be read from the stream
	 * @throws IOException
	 *             if IO Error occurs.
	 */
	public int available() throws IOException {
		return Math.min(is.available(), remainingBytes);
	}

	/**
	 * IS2T-API
	 * <p>
	 * Reads all remaining message body data and then close this input stream.
	 * This method DOES NOT close the underlying stream (i.e. the TCP connection
	 * stream). It is the responsibility of the HTTPSession to close the
	 * underlying stream.
	 * </p>
	 * 
	 * @throws IOException
	 *             when an error occurs while closing the stream
	 */
	public void close() throws IOException {
		if (remainingBytes > 0) {
			byte[] buf = new byte[Math.min(512, remainingBytes)];
			while (remainingBytes > 0) {
				read(buf);
			}
		}
		closed = true;
	}

	/**
	 * IS2T-API
	 * <p>
	 * Reads the next byte of data from the input stream. The byte value is
	 * returned as an int in the range of 0 to 255. If no byte is available
	 * because the end of the stream has been reached, the value -1 is returned.
	 * This method blocks until input data is available, the end of the stream
	 * is detected, or an exception is thrown.
	 * </p>
	 * 
	 * @return the next byte of data from the input stream, or -1 if the end of
	 *         the input stream has been reached.
	 * @throws IOException
	 *             If a premature EOF is reached, this stream is closed and an
	 *             IOException is thrown.
	 */
	public int read() throws IOException {
		if (closed) {
			throw new IOException();
		}
		if (remainingBytes <= 0) {
			return -1;
		}
		int result = is.read();
		checkPrematureEOF(result);
		--remainingBytes;
		return result;
	}

	/**
	 * IS2T-API
	 * <p>
	 * Reads up to <code>length</code> bytes of data from the underlying
	 * {@link InputStream} into an array of bytes. An attempt is made to read as
	 * many as <code>length</code> bytes, but the amount of bytes can be read
	 * from the {@link InputStream} could be less than <code>length</code>, even
	 * <code>0</code>. The number of bytes actually read are returned as an integer.
	 * </p>
	 * 
	 * @see InputStream#read(byte[], int, int)
	 * @param data
	 *            the byte array to store the read bytes from the underlying
	 *            {@link InputStream}
	 * @param offset
	 *            the starting index of byte array <code>data</code> to store
	 *            the bytes
	 * @param length
	 *            the number of bytes intended to be read by the caller of this
	 *            method
	 * 
	 * @return the number of bytes actually read from the underlying
	 *         {@link InputStream}. If the end of stream has been reached,
	 *         returns <code>-1</code>
	 * 
	 * @throws IOException
	 *             thrown in the following cases:
	 *             <ul>
	 *             <li>if this stream is already closed
	 *             <li>if an EOF is reached prematurely on the underlying
	 *             InputStream
	 *             </ul>
	 */
	public int read(final byte[] data, final int offset, final int length)
			throws IOException {
		if (closed) {
			throw new IOException();
		}
		if (remainingBytes <= 0) {
			return -1;
		}

		int result = is.read(data, offset, Math.min(length, remainingBytes));
		checkPrematureEOF(result);

		remainingBytes -= result;
		return result;
	}

	/*************************************************************************
	 * NOT IN API
	 ************************************************************************/

	/**
	 * If true the input stream is considered to be closed and all further
	 * operation will result in IOException.
	 */
	private boolean closed = false;

	/**
	 * The underlying input stream to use.
	 */
	private final InputStream is;

	/**
	 * The number of remaining bytes can be read from the input stream.
	 */
	private int remainingBytes;

	/**
	 * Checks if the end-of-file has been reached on underlying input stream at
	 * an unexpected moment. If an EOF is reached, this stream is closed and an
	 * {@link IOException} is thrown.
	 * 
	 * @param i
	 *            the result of a read operation on the underlying input stream.
	 * @throws IOException
	 *             when I/O Error occurs.
	 */
	private void checkPrematureEOF(int i) throws IOException {
		if (i == -1) {
			closed = true;
			throw new IOException();
		}
	}
}
