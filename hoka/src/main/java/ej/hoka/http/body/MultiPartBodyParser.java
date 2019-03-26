/*
 * Java
 *
 * Copyright 2018-2019 MicroEJ Corp. All rights reserved.
 * This library is provided in source code for use, modification and test, subject to license terms.
 * Any modification of the source code will break MicroEJ Corp. warranties on the whole library.
 */
package ej.hoka.http.body;

import java.io.IOException;
import java.io.InputStream;

import ej.hoka.http.HTTPConstants;
import ej.hoka.http.HTTPRequest;
import ej.hoka.http.support.MIMEUtils;

/**
 * A body parser for MIME_MULTIPART_FORM_ENCODED_DATA.
 */
public class MultiPartBodyParser implements BodyParser {

	private static final int SIZE = 512;
	/**
	 * The boundary delimiter;
	 */
	public static final String BOUNDARY = "boundary="; //$NON-NLS-1$

	private MultiPartBuffer buffer;

	/**
	 * Initialize the parsing, must be called before {@link #nextPart()}. This function does not consume the request.
	 */
	@Override
	public void parseBody(HTTPRequest httpRequest) throws IOException {
		String contentType = httpRequest.getHeaderField(HTTPConstants.FIELD_CONTENT_TYPE);
		// 3. the body contains a multipart form encoded
		if ((contentType != null) && contentType.startsWith(MIMEUtils.MIME_MULTIPART_FORM_ENCODED_DATA)) {
			String boundary = contentType.substring(contentType.indexOf(';') + 1);
			this.buffer = new MultiPartBuffer();
			this.buffer.boundary = (HTTPConstants.END_OF_LINE + "--" //$NON-NLS-1$
					+ boundary.substring(boundary.indexOf(BOUNDARY) + BOUNDARY.length())).getBytes();
			this.buffer.stream = httpRequest.getStream();
			this.buffer.buffer = new byte[SIZE];
			/**
			 * The first boundary does not have the line jump, artificially add it to have a generic behaviour.
			 */
			this.buffer.buffer[0] = '\r';
			this.buffer.buffer[1] = '\n';
			this.buffer.lengthAvailable = 2;
		}
	}

	/**
	 * Consume the input stream from the request to get the next {@link HTTPPart} available.
	 *
	 * @return the next {@link HTTPPart} initialized, <code>null</code> if not found.
	 * @throws IOException
	 *             if an {@link IOException} occurs during the parsing of the headers.
	 */
	public HTTPPart nextPart() throws IOException {
		if (this.buffer != null && this.buffer.hasData()) {
			HTTPPart httpPart = new HTTPPart(this.buffer);
			if (httpPart.parseHeaders().size() == 0 && !this.buffer.hasData()) {
				httpPart.close();
				httpPart = null;
			}
			return httpPart;
		}
		return null;
	}

	/**
	 * A shared buffer used by the multipart.
	 */
	static class MultiPartBuffer {

		/**
		 * The input stream.
		 */
		private InputStream stream;
		/**
		 * The boundaries bytes.
		 */
		private byte[] boundary;
		/**
		 * The buffer of the data read.
		 */
		private byte[] buffer;
		/**
		 * the offset position within the buffer.
		 */
		private int offset;
		/**
		 * the number of bytes available within the buffer.
		 */
		private int lengthAvailable;

		/**
		 * Checks whether more data is available in the input stream.
		 *
		 * @return <code>true</code> if more data is available in the input stream.
		 * @throws IOException
		 *             if an {@link IOException} happens.
		 */
		public boolean hasData() throws IOException {
			return this.buffer != null && this.boundary != null
					&& (this.lengthAvailable > 0 || (this.stream != null && this.stream.available() > 0));
		}

		/**
		 * Reads some bytes from the buffer.
		 *
		 * @param b
		 *            the target buffer.
		 * @param off
		 *            the offset.
		 * @param len
		 *            the lenght.
		 * @return the number of bytes read.
		 * @throws IOException
		 *             if an {@link IOException} happens.
		 */
		public int read(byte[] b, int off, int len) throws IOException {
			buffer(len);
			int read = -1;
			int toRead = len;

			int boundaryIndex = this.getBoundaryIndex(toRead);
			if (boundaryIndex != -1) {
				toRead = boundaryIndex;
			} else {
				toRead = Math.min(toRead, this.lengthAvailable - this.boundary.length);
			}
			if (toRead > 0) {
				System.arraycopy(this.buffer, this.offset, b, off, toRead);
				this.offset += toRead;
				this.lengthAvailable -= toRead;
				read = toRead;
			}
			return read;
		}

		/**
		 * Reads a byte in the buffer.
		 *
		 * @return the byte read, -1 if not found.
		 * @throws IOException
		 *             if an {@link IOException} occures.
		 */
		public int read() throws IOException {
			this.buffer(1);
			int read;
			read = this.buffer[this.offset] & 0xFF;
			this.offset++;
			this.lengthAvailable--;
			return read;
		}

		/**
		 * Consume the data up to after the next boundary.
		 *
		 * @throws IOException
		 *             if an {@link IOException} occurs
		 */
		public void skipToBoundary() throws IOException {
			this.buffer(1);
			int boundaryIndex = getBoundaryIndex(this.lengthAvailable);
			int boundaryLength = this.boundary.length;
			while (boundaryIndex == -1
					&& (this.lengthAvailable > 0 || (this.stream != null && this.stream.available() > 0))) {
				System.arraycopy(this.buffer, this.offset + this.lengthAvailable - boundaryLength, this.buffer, 0,
						boundaryLength);
				this.lengthAvailable = boundaryLength;
				this.offset = 0;
				boundaryIndex = getBoundaryIndex(this.lengthAvailable);
			}
			if (boundaryIndex != -1) {
				int offset = boundaryIndex + boundaryLength + 2;
				this.offset += offset;
				this.lengthAvailable -= offset;
			}
		}

		/**
		 * Gets the next boundary index within the length specified.
		 *
		 * @param length
		 *            the length to check.
		 * @return the next boundary index, or -1 if not found.
		 * @throws IOException
		 *             if an {@link IOException} occurs.
		 */
		public int getBoundaryIndex(int length) throws IOException {
			this.buffer(1);
			int boundary = -1;
			int boundaryLength = this.boundary.length;
			for (int boundaryStart = 0; boundaryStart < Math.min(length,
					this.lengthAvailable - boundaryLength - 1); boundaryStart++) {
				int i = 0;
				while (i < boundaryLength) {
					if (this.buffer[this.offset + i + boundaryStart] != this.boundary[i]) {
						break;
					}
					i++;
				}
				if (i == boundaryLength) {
					boundary = boundaryStart;
					break;
				}
			}
			return boundary;
		}

		/**
		 * Buffer the data if needed.
		 *
		 * @param length
		 *            the size of the data that will be required.
		 * @throws IOException
		 *             if the read is not possible.
		 */
		private void buffer(int length) throws IOException {
			int lengthAvailable = this.lengthAvailable;
			if (this.stream != null && lengthAvailable - this.boundary.length - 1 < length) {
				int offset = this.offset;
				int bufferLength = this.buffer.length;
				if (offset != 0) {
					System.arraycopy(this.buffer, offset, this.buffer, 0, lengthAvailable);
				}
				int read = this.stream.read(this.buffer, lengthAvailable, bufferLength - lengthAvailable);

				if (read > 0) {
					this.lengthAvailable += read;
				}
				this.offset = 0;
			}
		}
	}
}
