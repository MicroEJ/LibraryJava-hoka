/*
 * Java
 *
 * Copyright 2009-2019 MicroEJ Corp. All rights reserved.
 * This library is provided in source code for use, modification and test, subject to license terms.
 * Any modification of the source code will break MicroEJ Corp. warranties on the whole library.
 */
package ej.hoka.io;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Identity output stream. Wraps an {@link OutputStream} and all of the operations on
 * {@link IdentityMessageBodyOutputStream} are delegated to this underlying {@link OutputStream}.
 */
public class IdentityMessageBodyOutputStream extends OutputStream {

	/**
	 * If true this output stream is considered to be closed and all further calls on this output stream will result in
	 * IOException.
	 */
	private boolean closed = false;

	/**
	 * The underlying output stream.
	 */
	private final OutputStream os;

	/**
	 * Creates a new instance of {@link IdentityMessageBodyOutputStream} using the {@link OutputStream} as the
	 * underlying OutputStream to write the data to.
	 *
	 * @param os
	 *            the underlying {@link OutputStream} to use
	 */
	public IdentityMessageBodyOutputStream(final OutputStream os) {
		this.os = os;
	}

	/**
	 * Closes this output stream and flushes the underlying {@link OutputStream} . This method DOES NOT close the
	 * underlying stream (i.e. the TCP connection stream). It is the responsibility of the caller to close the
	 * underlying stream.
	 *
	 * @throws IOException
	 *             when an I/O error occurs while closing the stream
	 */
	@Override
	public final void close() throws IOException {
		this.os.flush();
		this.closed = true;
	}

	/**
	 * Flushes the underlying output stream.
	 *
	 * @throws IOException
	 *             if this output stream has already been closed.
	 */
	@Override
	public final void flush() throws IOException {
		if (this.closed) {
			throw new IOException();
		}
		this.os.flush();
	}

	/**
	 * Writes the content of the byte array to the underlying output stream.
	 *
	 * @param b
	 *            the byte array to read bytes from and write to the underlying output stream.
	 * @throws IOException
	 *             if this output stream has already been closed.
	 */
	@Override
	public final void write(final byte[] b) throws IOException {
		if (this.closed) {
			throw new IOException();
		}
		this.os.write(b);
	}

	/**
	 * Writes <code>len</code> bytes from the specified byte array starting at offset <code>off</code> to this output
	 * stream. The general contract for write(b, off, len) is that some of the bytes in the array <code>b</code> are
	 * written to the output stream in order; element b[off] is the first byte written and b[off+len-1] is the last byte
	 * written by this operation. If <code>b</code> is null, a {@link NullPointerException} is thrown. If
	 * <code>off</code> is negative, or <code>len</code> is negative, or <code>off</code>+<code>len</code> is greater
	 * than the length of the array <code>b</code>, then an {@link IndexOutOfBoundsException} is thrown.
	 *
	 * @param b
	 *            the byte array to read bytes from
	 * @param off
	 *            the starting index in byte array <code>b</code> to read bytes from
	 * @param len
	 *            number of bytes to read from the byte array <code>b</code>
	 * @throws IOException
	 *             if this output stream has already been closed
	 */
	@Override
	public final void write(final byte[] b, final int off, final int len) throws IOException {
		if (this.closed) {
			throw new IOException();
		}
		this.os.write(b, off, len);
	}

	/**
	 * Writes the specified byte to this output stream. The general contract for write is that one byte is written to
	 * the output stream. The byte to be written is the eight low-order bits of the argument <code>b</code>. The 24
	 * high-order bits of b are ignored.
	 *
	 * @param b
	 *            the value to be written to the underlying output stream
	 * @throws IOException
	 *             if this output stream has already been closed
	 */
	@Override
	public final void write(final int b) throws IOException {
		if (this.closed) {
			throw new IOException();
		}
		this.os.write(b);
	}

}
