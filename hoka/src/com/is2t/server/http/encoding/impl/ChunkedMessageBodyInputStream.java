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
 * Input Stream for reading HTTP 1.1 Chunked transfer encoding data.
 * </p>
 * <p>
 * Each chunk starts with the number of octets of the data it embeds, expressed
 * as a hexadecimal number in ASCII, followed by optional parameters (chunk
 * extension) and a terminating CRLF sequence, followed by the chunk data. The
 * chunk is terminated by CRLF. If chunk extensions are provided, the chunk size
 * is terminated by a semicolon followed with the extension name and an optional
 * equal sign and value. (chunk extensions are skipped).
 * </p>
 */
public class ChunkedMessageBodyInputStream extends InputStream {

	/**
	 * IS2T-API
	 * <p>
	 * Creates a new <code>ChunkedMessageBodyInputStream</code> using the InputStream
	 * <code>is</code> as the underlying data source.
	 * </p>
	 * 
	 * @param is
	 *            InputStream to read from.
	 */
	public ChunkedMessageBodyInputStream(InputStream is) {
		this.is = is;
		state = STATE_START;
	}

	/**
	 * IS2T-API
	 * <p>
	 * Returns the number of available bytes to read.
	 * </p>
	 * 
	 * @return the number of available bytes to read.
	 * @throws IOException
	 *             if the <code>ChunkedMessageBodyInputStream</code> is already
	 *             closed.
	 */
	public int available() throws IOException {
		if (closed) {
			throw new IOException();
		}
		return Math.min(is.available(), remainingBytes);
	}

	/**
	 * IS2T-API
	 * <p>
	 * Reads all remaining message body data and closes this input stream.
	 * This method DOES NOT close the underlying stream (i.e. the TCP connection
	 * stream). It is the responsibility of the HTTPSession to close the
	 * underlying stream.
	 * </p>
	 * 
	 * @throws IOException
	 *             when an error occurs while closing the stream
	 */
	public void close() throws IOException {
		if (state != STATE_END) {
			// read remaining data
			byte[] buf = new byte[512];
			while (initChunk()) {
				int result = is.read(buf, 0, Math.min(512, remainingBytes));
				checkPrematureEOF(result);
				remainingBytes -= result;
			}
		}
		closed = true;
	}

	/**
	 * IS2T-API
	 * <p>
	 * Reads the next byte from the InputStream used in the constructor.
	 * </p>
	 * 
	 * @return the next byte value (0-255) or -1 if the end of the InputStream
	 *         has been reached.
	 * @throws IOException
	 *             when the InputStream has already been closed.
	 */
	public int read() throws IOException {
		if (closed) {
			throw new IOException();
		}
		if (!initChunk()) {
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
	 * Reads up to <code>length</code> bytes to the byte array <code>data</code> starting
	 * with <code>offset</code>. The method tries to read <code>length</code> bytes (if
	 * there are at least <code>length</code> bytes in the InputStream used in the
	 * constructor. Otherwise reads just the available number of bytes).
	 * </p>
	 * 
	 * @param data
	 *            the byte array to store the bytes read from the underlying InputStream
	 * @param offset
	 *            the position in the byte array <code>data</code> to store the
	 *            bytes read from the InputStream.
	 * @param length
	 *            number of bytes to store in the byte array <code>data</code> (if the
	 *            number of bytes available in the InputStream are at least
	 *            <code>length</code> ) If less than <code>length</code> bytes are left in
	 *            the InputStream, only the remaining bytes are stored in the
	 *            byte array <code>data</code>.
	 * @throws IOException
	 *             when the end of InputStream has already been reached.
	 * @return the number of bytes stored in the byte array <code>data</code>, or -1
	 *         if no bytes can be stored in the byte array <code>data</code>.
	 */
	public int read(byte[] data, int offset, int length) throws IOException {
		if (closed) {
			throw new IOException();
		}
		if (!initChunk()) {
			return -1;
		}

		int nbToRead = length <= remainingBytes ? length : remainingBytes;
		int result = is.read(data, offset, nbToRead);
		checkPrematureEOF(result);

		remainingBytes -= result;
		return result;
	}

	/*************************************************************************
	 * NOT IN API
	 ************************************************************************/
	
	/**
	 * If true, the chunked InputStream is considered to be closed and any further 
	 * read operation will result in an IOException.
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
	 * Check if the end-of-file has been reached on underlying input stream at
	 * an unexpected moment. If an EOF is reached, this stream is closed and an
	 * {@link IOException} is thrown.
	 * 
	 * @param i
	 *            the result of a read operation on the underlying input stream.
	 * @throws IOException when I/O Error occurs
	 */
	private void checkPrematureEOF(int i) throws IOException {
		if (i == -1) {
			closed = true;
			throw new IOException();
		}
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
		if (state == STATE_END) {
			return false;
		}

		if (remainingBytes == 0) {
			// Start a new chunk
			int b;

			if (state == STATE_CONSUME_CHUNK) {
				// consume CRLF (only if not the first chunk)
				b = is.read();
				checkPrematureEOF(b);
				b = is.read();
				checkPrematureEOF(b);
			}

			// read chunk-size
			int size = 0;
			boolean hasExtension = false;
			while (true) {
				b = is.read();
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
					b = is.read();
					checkPrematureEOF(b);
					if (b == CARRIAGE_RETURN_CHAR) {
						break;
					}
				}
			}

			// consume newline
			b = is.read();
			checkPrematureEOF(b);

			// here, there are <size> bytes of raw data available to be read
			remainingBytes = size;

			if (size == 0) {
				// last chunk
				// consume optional trailer
				while (true) {
					b = is.read();
					checkPrematureEOF(b);
					if (b == CARRIAGE_RETURN_CHAR) {
						// found an empty line : end of body
						state = STATE_END;
						return false;
					} else {
						// found a trailer, consume it
						do {
							b = is.read();
							checkPrematureEOF(b);

							// TODO handle entity headers. Currently ignored
							// HTTPHeaderField[] headers =
							// HTTPRequest.parseHeader(is);
						} while (b != CARRIAGE_RETURN_CHAR);

						// end of trailer, consume LF
						b = is.read();
						checkPrematureEOF(b);
					}
				}
			} else {
				state = STATE_CONSUME_CHUNK;
			}
		}
		return true;
	}

}
