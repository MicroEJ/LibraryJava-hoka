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
 * Input Stream for reading HTTP 1.1 Chunked transfer encoding data.
 * <p>
 * Each chunk starts with the number of octets of the data it embeds, expressed as a hexadecimal number in ASCII,
 * followed by optional parameters (chunk extension) and a terminating CRLF sequence, followed by the chunk data. The
 * chunk is terminated by CRLF. If chunk extensions are provided, the chunk size is terminated by a semicolon followed
 * with the extension name and an optional equal sign and value. (chunk extensions are skipped).
 */
public class ChunkedMessageBodyInputStream extends InputStream {

	private static final int BUFFER_SIZE = 512;

	/**
	 * Carriage return character.
	 */
	private static final char CARRIAGE_RETURN_CHAR = '\r';

	/**
	 * Indicates that the "middle" of the chunked data stream.
	 */
	private static final int STATE_CONSUME_CHUNK = 2;

	/**
	 * Indicates that the end of the last chunk has been reached.
	 */
	private static final int STATE_END = 3;

	/**
	 * Indicates the begin of the first chunk.
	 */
	private static final int STATE_START = 1;

	/**
	 * If true, the chunked InputStream is considered to be closed and any further read operation will result in an
	 * IOException.
	 */
	private boolean closed = false;

	/**
	 * The underlying InputStream to read data from.
	 */
	private final InputStream is;

	/**
	 * Remaining bytes that can be read in the current chunk.
	 */
	private int remainingBytes; // Initialized to 0

	/**
	 * The current state of the chunk reading.
	 */
	private int state;

	/**
	 * Creates a new <code>ChunkedMessageBodyInputStream</code> using the InputStream <code>is</code> as the underlying
	 * data source.
	 *
	 * @param is
	 *            InputStream to read from.
	 */
	public ChunkedMessageBodyInputStream(InputStream is) {
		this.is = is;
		this.state = STATE_START;
	}

	/**
	 * Returns the number of available bytes to read.
	 *
	 * @return the number of available bytes to read.
	 * @throws IOException
	 *             if the <code>ChunkedMessageBodyInputStream</code> is already closed.
	 */
	@Override
	public int available() throws IOException {
		if (this.closed) {
			throw new IOException();
		}
		return Math.min(this.is.available(), this.remainingBytes);
	}

	/**
	 * Check if the end-of-file has been reached on underlying input stream at an unexpected moment. If an EOF is
	 * reached, this stream is closed and an {@link IOException} is thrown.
	 *
	 * @param i
	 *            the result of a read operation on the underlying input stream.
	 * @throws IOException
	 *             when I/O Error occurs
	 */
	private void checkPrematureEOF(int i) throws IOException {
		if (i == -1) {
			this.closed = true;
			throw new IOException();
		}
	}

	/**
	 * Reads all remaining message body data and closes this input stream. This method DOES NOT close the underlying
	 * stream (i.e. the TCP connection stream). It is the responsibility of the HTTPSession to close the underlying
	 * stream.
	 *
	 * @throws IOException
	 *             when an error occurs while closing the stream
	 */
	@Override
	public void close() throws IOException {
		if (this.state != STATE_END) {
			// read remaining data
			byte[] buf = new byte[BUFFER_SIZE];
			while (initChunk()) {
				int result = this.is.read(buf, 0, Math.min(BUFFER_SIZE, this.remainingBytes));
				checkPrematureEOF(result);
				this.remainingBytes -= result;
			}
		}
		this.closed = true;
	}

	/**
	 * Consume start of chunk and update {@link #remainingBytes}.
	 *
	 * @return false when message body is ended.
	 * @throws IOException
	 *             if the connection is lost.
	 */
	private boolean initChunk() throws IOException {
		// Grammar:
		// See 3.6.1 Chunked Transfer Coding
		// ----------------------------------------------------------
		// Chunked-Body = *chunk last-chunk trailer CRLF
		// chunk = chunk-size [ chunk-extension ] CRLF chunk-data CRLF
		// chunk-size = 1*HEX
		// last-chunk = 1*("0") [ chunk-extension ] CRLF
		// chunk-extension= *( ";" chunk-ext-name [ "=" chunk-ext-val ] )
		// chunk-ext-name = token
		// chunk-ext-val = token | quoted-string
		// chunk-data = chunk-size(OCTET)
		// trailer = *(entity-header CRLF)
		// ----------------------------------------------------------
		if (this.state == STATE_END) {
			return false;
		}

		if (this.remainingBytes == 0) {
			// Start a new chunk
			int b;

			if (this.state == STATE_CONSUME_CHUNK) {
				// consume CRLF (only if not the first chunk)
				b = this.is.read();
				checkPrematureEOF(b);
				b = this.is.read();
				checkPrematureEOF(b);
			}

			// read chunk-size
			int size = 0;
			boolean hasExtension = false;
			while (true) {
				b = this.is.read();
				checkPrematureEOF(b);
				if (b == ';') {
					hasExtension = true;
					break;
				} else if (b == CARRIAGE_RETURN_CHAR) {
					break;
				}
				size = (size << 4) + Character.digit((char) b, 16);
			}

			if (hasExtension) {
				// currently skip it TODO
				while (true) {
					b = this.is.read();
					checkPrematureEOF(b);
					if (b == CARRIAGE_RETURN_CHAR) {
						break;
					}
				}
			}

			// consume newline
			b = this.is.read();
			checkPrematureEOF(b);

			// here, there are <size> bytes of raw data available to be read
			this.remainingBytes = size;

			if (size == 0) {
				// last chunk
				// consume optional trailer
				while (true) {
					b = this.is.read();
					checkPrematureEOF(b);
					if (b == CARRIAGE_RETURN_CHAR) {
						// found an empty line : end of body
						this.state = STATE_END;
						return false;
					} else {
						// found a trailer, consume it
						do {
							b = this.is.read();
							checkPrematureEOF(b);

							// TODO handle entity headers. Currently ignored
							// HTTPHeaderField[] headers =
							// HTTPRequest.parseHeader(is);
						} while (b != CARRIAGE_RETURN_CHAR);

						// end of trailer, consume LF
						b = this.is.read();
						checkPrematureEOF(b);
					}
				}
			} else {
				this.state = STATE_CONSUME_CHUNK;
			}
		}
		return true;
	}

	/**
	 * Reads the next byte from the InputStream used in the constructor.
	 *
	 * @return the next byte value (0-255) or -1 if the end of the InputStream has been reached.
	 * @throws IOException
	 *             when the InputStream has already been closed.
	 */
	@Override
	public int read() throws IOException {
		if (this.closed) {
			throw new IOException();
		}
		if (!initChunk()) {
			return -1;
		}

		int result = this.is.read();
		checkPrematureEOF(result);

		--this.remainingBytes;
		return result;
	}

	/**
	 * Reads up to <code>length</code> bytes to the byte array <code>data</code> starting with <code>offset</code>. The
	 * method tries to read <code>length</code> bytes (if there are at least <code>length</code> bytes in the
	 * InputStream used in the constructor. Otherwise reads just the available number of bytes).
	 *
	 * @param data
	 *            the byte array to store the bytes read from the underlying InputStream
	 * @param offset
	 *            the position in the byte array <code>data</code> to store the bytes read from the InputStream.
	 * @param length
	 *            number of bytes to store in the byte array <code>data</code> (if the number of bytes available in the
	 *            InputStream are at least <code>length</code> ) If less than <code>length</code> bytes are left in the
	 *            InputStream, only the remaining bytes are stored in the byte array <code>data</code>.
	 * @throws IOException
	 *             when the end of InputStream has already been reached.
	 * @return the number of bytes stored in the byte array <code>data</code>, or -1 if no bytes can be stored in the
	 *         byte array <code>data</code>.
	 */
	@Override
	public int read(byte[] data, int offset, int length) throws IOException {
		if (this.closed) {
			throw new IOException();
		}
		if (!initChunk()) {
			return -1;
		}

		int nbToRead = length <= this.remainingBytes ? length : this.remainingBytes;
		int result = this.is.read(data, offset, nbToRead);
		checkPrematureEOF(result);

		this.remainingBytes -= result;
		return result;
	}

}
