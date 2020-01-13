/*
 * Java
 *
 * Copyright 2018-2020 MicroEJ Corp. All rights reserved.
 * This library is provided in source code for use, modification and test, subject to license terms.
 * Any modification of the source code will break MicroEJ Corp. warranties on the whole library.
 */
package ej.hoka.http.body;

import java.io.IOException;
import java.io.InputStream;

import ej.hoka.http.HTTPConstants;
import ej.hoka.http.body.MultiPartBodyParser.MultiPartBody;
import ej.hoka.http.support.MIMEUtils;

/**
 * A body parser for {@link MIMEUtils#MIME_MULTIPART_FORM_ENCODED_DATA}.
 */
public class MultiPartBodyParser implements BodyParser<MultiPartBody> {

	/**
	 * This size is used for the buffer size.
	 */
	private static final int BUFFER_SIZE = 4096;

	/**
	 * Property to set a custom buffer size.
	 */
	private static final String BUFFER_SIZE_PROPERTY = "hoka.buffer.size"; //$NON-NLS-1$
	/**
	 * The boundary delimiter.
	 */
	public static final String BOUNDARY = "boundary="; //$NON-NLS-1$

	/**
	 * Initialize the parsing, must be called before {@link MultiPartBody#nextPart()}. This function does not consume
	 * the <code>inputStream</code>.
	 */
	@Override
	public MultiPartBody parseBody(InputStream inputStream, String contentType) throws IOException {
		MultiPartBuffer buffer = null;
		if ((contentType != null) && contentType.startsWith(MIMEUtils.MIME_MULTIPART_FORM_ENCODED_DATA)) {
			String boundary = contentType.substring(contentType.indexOf(';') + 1);
			buffer = new MultiPartBuffer();
			buffer.boundary = HTTPConstants.END_OF_LINE + "--" //$NON-NLS-1$
					+ boundary.substring(boundary.indexOf(BOUNDARY) + BOUNDARY.length());
			buffer.stream = inputStream;
			buffer.buffer = new byte[Integer.getInteger(BUFFER_SIZE_PROPERTY, BUFFER_SIZE).intValue()];
			/**
			 * The first boundary does not have the line jump, artificially add it to have a generic behaviour.
			 */
			buffer.buffer[0] = '\r';
			buffer.buffer[1] = '\n';
			buffer.lengthAvailable = 2;
		}
		return new MultiPartBody(buffer);
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
		 * The boundaries pattern.
		 */
		private String boundary;
		/**
		 * The buffer of the data read.
		 */
		private byte[] buffer;
		/**
		 * The offset position within the buffer.
		 */
		private int offset;
		/**
		 * The number of bytes available within the buffer.
		 */
		private int lengthAvailable;

		/**
		 * Checks whether more data is available in the input stream.
		 *
		 * @return {@code true} if more data is available in the input stream.
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
		 *            the length.
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
				toRead = Math.min(toRead, this.lengthAvailable - this.boundary.length());
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
		 * Consumes the data up to after the next boundary.
		 *
		 * @throws IOException
		 *             if an {@link IOException} occurs
		 */
		public void skipToBoundary() throws IOException {
			this.buffer(1);
			int boundaryIndex = getBoundaryIndex(this.lengthAvailable);
			while (boundaryIndex == -1
					&& (this.lengthAvailable > 0 || (this.stream != null && this.stream.available() > 0))) {
				System.arraycopy(this.buffer, this.offset + this.lengthAvailable - this.boundary.length(), this.buffer,
						0, this.boundary.length());
				this.lengthAvailable = this.boundary.length();
				this.offset = 0;
				boundaryIndex = getBoundaryIndex(this.lengthAvailable);
			}
			if (boundaryIndex != -1) {
				int offset = boundaryIndex + this.boundary.length() + 2;
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

			byte[] buffer = this.buffer;
			String bufferAsString = new String(buffer, "US-ASCII");

			int max = Math.min(length, this.lengthAvailable - 1 - this.boundary.length());

			int index = this.offset - 1;
			while ((index = bufferAsString.indexOf(this.boundary.charAt(0), index + 1)) != -1 && index < max) {
				if (bufferAsString.startsWith(this.boundary, index)) {
					return index - this.offset;
				}
			}

			return -1;
		}

		/**
		 * Buffers the data if needed.
		 *
		 * @param length
		 *            the size of the data that will be required.
		 * @throws IOException
		 *             if the read is not possible.
		 */
		private void buffer(int length) throws IOException {
			int lengthAvailable = this.lengthAvailable;
			if (this.stream != null && lengthAvailable - this.boundary.length() - 1 < length) {
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

	/**
	 * Class representing the collection of {@link HTTPPart} in the request.
	 */
	public static class MultiPartBody {

		private final MultiPartBuffer buffer;

		/**
		 * Constructs the {@link MultiPartBody} with the specified <code>buffer</code>.
		 *
		 * @param buffer
		 *            the {@link MultiPartBuffer} to user.
		 */
		private MultiPartBody(MultiPartBuffer buffer) {
			this.buffer = buffer;
		}

		/**
		 * Consumes the input stream from the request to get the next {@link HTTPPart} available.
		 *
		 * @return the next {@link HTTPPart} initialized, <code>null</code> if not found.
		 * @throws IOException
		 *             if an {@link IOException} occurs during the parsing of the headers.
		 */
		public HTTPPart nextPart() throws IOException {
			if (this.buffer != null && this.buffer.hasData()) {
				HTTPPart httpPart = new HTTPPart(this.buffer); // NOSONAR
				if (httpPart.parseHeaders().size() == 0 && !this.buffer.hasData()) {
					httpPart.close();
					httpPart = null;
				}
				return httpPart;
			}
			return null;
		}

	}

}
