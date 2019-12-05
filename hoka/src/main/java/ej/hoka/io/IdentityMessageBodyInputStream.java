/*
 * Java
 *
 * Copyright 2009-2019 MicroEJ Corp. All rights reserved.
 * This library is provided in source code for use, modification and test, subject to license terms.
 * Any modification of the source code will break MicroEJ Corp. warranties on the whole library.
 */
package ej.hoka.io;

import java.io.IOException;
import java.io.InputStream;

/**
 * Identity input stream. Wraps an {@link InputStream} and all of the operations on
 * {@link IdentityMessageBodyInputStream} are delegated to this underlying {@link InputStream}.
 */
public class IdentityMessageBodyInputStream extends InputStream {

	private static final int BUFFER_SIZE = 512;

	/**
	 * If true the input stream is considered to be closed and all further operation will result in IOException.
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
	 * Creates a new instance of {@link IdentityMessageBodyInputStream} with the {@link InputStream} <code>is</code>
	 * with the predefined length <code>bodyLength</code>. The <code>bodyLength</code> should be the maximum number of
	 * bytes can be read from the InputStream.
	 *
	 * @param is
	 *            the underlying {@link InputStream} to read the body content of the HTTP message body
	 * @param bodyLength
	 *            the number of bytes can be read from the underlying InputStream.
	 */
	public IdentityMessageBodyInputStream(final InputStream is, final int bodyLength) {
		this.is = is;
		this.remainingBytes = bodyLength;
	}

	/**
	 * Returns the number of bytes that can be read (or skipped over) from this input stream without blocking.
	 *
	 * @return the number of available bytes could be read from the stream
	 * @throws IOException
	 *             if IO Error occurs.
	 */
	@Override
	public int available() throws IOException {
		return Math.min(this.is.available(), this.remainingBytes);
	}

	/**
	 * Checks if the end-of-file has been reached on underlying input stream at an unexpected moment. If an EOF is
	 * reached, this stream is closed and an {@link IOException} is thrown.
	 *
	 * @param i
	 *            the result of a read operation on the underlying input stream.
	 * @throws IOException
	 *             when I/O Error occurs.
	 */
	private void checkPrematureEOF(int i) throws IOException {
		if (i == -1) {
			this.closed = true;
			throw new IOException();
		}
	}

	/**
	 * Reads all remaining message body data and then close this input stream. This method DOES NOT close the underlying
	 * stream (i.e. the TCP connection stream). It is the responsibility of the HTTPSession to close the underlying
	 * stream.
	 *
	 * @throws IOException
	 *             when an error occurs while closing the stream
	 */
	@Override
	public void close() throws IOException {
		if (this.remainingBytes > 0) {
			byte[] buf = new byte[Math.min(BUFFER_SIZE, this.remainingBytes)];
			while (this.remainingBytes > 0) {
				read(buf);
			}
		}
		this.closed = true;
	}

	/**
	 * Reads the next byte of data from the input stream. The byte value is returned as an int in the range of 0 to 255.
	 * If no byte is available because the end of the stream has been reached, the value -1 is returned. This method
	 * blocks until input data is available, the end of the stream is detected, or an exception is thrown.
	 *
	 * @return the next byte of data from the input stream, or -1 if the end of the input stream has been reached.
	 * @throws IOException
	 *             If a premature EOF is reached, this stream is closed and an IOException is thrown.
	 */
	@Override
	public int read() throws IOException {
		if (this.closed) {
			throw new IOException();
		}
		if (this.remainingBytes <= 0) {
			return -1;
		}
		int result = this.is.read();
		checkPrematureEOF(result);
		--this.remainingBytes;
		return result;
	}

	/**
	 * Reads up to <code>length</code> bytes of data from the underlying {@link InputStream} into an array of bytes. An
	 * attempt is made to read as many as <code>length</code> bytes, but the amount of bytes can be read from the
	 * {@link InputStream} could be less than <code>length</code>, even <code>0</code>. The number of bytes actually
	 * read are returned as an integer.
	 *
	 * @see InputStream#read(byte[], int, int)
	 * @param data
	 *            the byte array to store the read bytes from the underlying {@link InputStream}
	 * @param offset
	 *            the starting index of byte array <code>data</code> to store the bytes
	 * @param length
	 *            the number of bytes intended to be read by the caller of this method
	 *
	 * @return the number of bytes actually read from the underlying {@link InputStream}. If the end of stream has been
	 *         reached, returns <code>-1</code>
	 *
	 * @throws IOException
	 *             thrown in the following cases:
	 *             <ul>
	 *             <li>if this stream is already closed
	 *             <li>if an EOF is reached prematurely on the underlying InputStream
	 *             </ul>
	 */
	@Override
	public int read(final byte[] data, final int offset, final int length) throws IOException {
		if (this.closed) {
			throw new IOException();
		}
		if (this.remainingBytes <= 0) {
			return -1;
		}

		int result = this.is.read(data, offset, Math.min(length, this.remainingBytes));
		checkPrematureEOF(result);

		this.remainingBytes -= result;
		return result;
	}

}
