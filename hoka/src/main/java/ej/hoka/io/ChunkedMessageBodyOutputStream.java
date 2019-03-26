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

import ej.hoka.http.HTTPConstants;

/**
 * <p>
 * Output Stream for writing HTTP 1.1 Chunked transfer encoding data.
 * </p>
 * <p>
 * Each chunk starts with the number of octets of the data it embeds, expressed as a hexadecimal numbers in ASCII and a
 * terminating CRLF sequence, followed by the chunk data. The chunk is terminated by CRLF.
 * </p>
 */
public class ChunkedMessageBodyOutputStream extends OutputStream {
	/**
	 * CR LF.
	 */
	private static final byte[] CRLF = HTTPConstants.END_OF_LINE.getBytes();

	/**
	 * Size of the pending bytes array.
	 */
	private static final int PENDING_BYTE_LENGTH = 32;

	/**
	 * Closed flag.
	 */
	private boolean closed = false;

	/**
	 * Number of pending bytes.
	 */
	private int nbPendingBytes = 0;

	/**
	 * The {@link OutputStream} to use.
	 */
	private final OutputStream os;

	/**
	 * Pending bytes.
	 */
	private byte[] pendingBytes = null; // lazily created

	/**
	 * <p>
	 * Creates a new instance of {@link ChunkedMessageBodyOutputStream} using the specified {@link OutputStream} as the
	 * underlying OutputStream.
	 * </p>
	 *
	 * @param os
	 *            the underlying {@link OutputStream} to use
	 */
	public ChunkedMessageBodyOutputStream(final OutputStream os) {
		this.os = os;
	}

	/**
	 * <p>
	 * Close this output stream. This method DOES NOT close the underlying stream (i.e. the TCP connection stream). It
	 * is the responsibility of the HTTPSession to close the underlying stream.
	 * </p>
	 *
	 * @throws IOException
	 *             when an error occurs while closing the stream
	 */
	@Override
	public final void close() throws IOException {
		writePendingBytes();
		// write last-chunk
		this.os.write('0');
		this.os.write(CRLF);
		// write end
		this.os.write(CRLF);
		this.os.flush();
		this.closed = true;
		this.pendingBytes = null; // GC
	}

	/**
	 * <p>
	 * Writes the pending data and flush underlying stream.
	 * </p>
	 *
	 * @throws IOException
	 *             when the connection is closed.
	 */
	@Override
	public final void flush() throws IOException {
		if (this.closed) {
			throw new IOException();
		}
		writePendingBytes();
		this.os.flush();
	}

	/**
	 * <p>
	 * Writes the content of the byte array <code>b</code> from the offset <code>off</code> in length <code>len</code>
	 * in chunked encoding using the underlying {@link OutputStream}.
	 * </p>
	 *
	 * @param b
	 *            the byte array
	 * @param off
	 *            the starting index in byte array <code>b</code>
	 * @param len
	 *            the number of bytes written to the underlying Output stream in chunked encoding.
	 * @throws IOException
	 *             if the ChunkedMessageBodyOutputStream is already closed.
	 */
	@Override
	public final void write(final byte[] b, final int off, final int len) throws IOException {
		if (this.closed) {
			throw new IOException();
		}
		writePendingBytes();
		writeChunk(b, off, len);
	}

	/**
	 * <p>
	 * Write one byte of data. The byte is not sent immediately, it is stored in a buffer and will be sent in a chunk
	 * when the buffer is full.
	 * </p>
	 *
	 * @param b
	 *            the byte to write to the underlying {@link OutputStream }
	 * @throws IOException
	 *             if the ChunkedMessageBodyOutputStream is already closed.
	 *
	 * @see OutputStream#write(int)
	 */
	@Override
	public final void write(final int b) throws IOException {
		if (this.closed) {
			throw new IOException();
		}

		if (this.pendingBytes == null) {
			this.pendingBytes = new byte[PENDING_BYTE_LENGTH];
		}

		this.pendingBytes[this.nbPendingBytes++] = (byte) b;

		if (this.nbPendingBytes == PENDING_BYTE_LENGTH) {
			// pendingBytes full, write it.
			writePendingBytes();
		}
	}

	/**
	 * Writes the HTTP chunk in the following format:<br>
	 * <code>
	 * chunk-length-in-hexadecimal-format CRLF<br>
	 * chunk-data CRLF
	 * </code>.
	 *
	 * @param b
	 *            the byte array containing the bytes to write to the chunk
	 * @param off
	 *            the starting index in the byte sequence in the byte array
	 * @param len
	 *            the length of byte sequence to write to the chunk
	 * @throws IOException
	 *             when the connection is lost
	 */
	private void writeChunk(final byte[] b, final int off, final int len) throws IOException {
		// write chunk size
		this.os.write(Integer.toString(len, 16).getBytes());
		this.os.write(CRLF);
		// write chunk data
		this.os.write(b, off, len);
		this.os.write(CRLF);
	}

	/**
	 * Writes the pending bytes as HTTP chunk.
	 *
	 * @throws IOException
	 *             when connection is lost
	 * @see #writeChunk(byte[], int, int)
	 */
	private void writePendingBytes() throws IOException {
		if (this.nbPendingBytes == 0) {
			return;
		}

		writeChunk(this.pendingBytes, 0, this.nbPendingBytes);
		this.nbPendingBytes = 0;
	}
}
