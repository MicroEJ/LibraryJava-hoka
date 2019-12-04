/*
 * Java
 *
 * Copyright 2009-2019 MicroEJ Corp. All rights reserved.
 * This library is provided in source code for use, modification and test, subject to license terms.
 * Any modification of the source code will break MicroEJ Corp. warranties on the whole library.
 */
package ej.hoka.http;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import ej.hoka.http.encoding.HTTPEncodingRegistry;
import ej.hoka.http.encoding.IHTTPEncodingHandler;
import ej.hoka.http.support.MIMEUtils;
import ej.hoka.log.Messages;
import ej.util.message.Level;

/**
 * Represents a HTTP Response.
 */
public class HTTPResponse {

	/**
	 * The colon character.
	 */
	private static final String RESPONSE_COLON = ": "; //$NON-NLS-1$

	/**
	 * The HTTP/1.1 version String.
	 */
	private static final String RESPONSE_HTTP11 = "HTTP/1.1 "; //$NON-NLS-1$

	/**
	 * The Content-Type: String.
	 */
	private static final String RESPONSE_CONTENTTYPE = HTTPConstants.FIELD_CONTENT_TYPE + RESPONSE_COLON;

	/**
	 * The status.
	 */
	private String status;

	/**
	 * The mime type.
	 */
	private String mimeType;

	/**
	 * Unique field to store the data object to be used, it can be either a byte[] or an InputStream.
	 */
	private Object data;

	/**
	 * Do not update this value by hand, use {@link #setLength(long)} to maintain HTTP header.
	 */
	private long length = -1; // -1 means unknown

	/**
	 * HTTP Response headers.
	 */
	private final HashMap<String, String> header = new HashMap<>(5);

	/**
	 * Creates an empty {@link HTTPResponse}.
	 */
	public HTTPResponse() {
		this(new byte[] {});
	}

	/**
	 * Creates a new {@link HTTPResponse} using the given byte array as response data.
	 *
	 * @param data
	 *            the data to send through response (as a raw byte array).
	 */
	public HTTPResponse(byte[] data) {
		setData(data);
	}

	/**
	 * Creates a new {@link HTTPResponse} using the given {@link InputStream} as the response data.
	 *
	 * @param data
	 *            the data to send through response (as a stream), the stream will be closed automatically when the
	 *            response is sent.
	 */
	public HTTPResponse(InputStream data) {
		setData(data);
	}

	/**
	 * Creates a new {@link HTTPResponse} using the given {@link InputStream} as the response data.
	 *
	 * @param data
	 *            the data to send through response (as a stream), the stream will be closed automatically when the
	 *            response is sent.
	 * @param length
	 *            the length of the response.
	 */
	public HTTPResponse(InputStream data, int length) {
		setData(data);
		setLength(length);
	}

	/**
	 * Creates a new {@link HTTPResponse} using the given {@link String} as response data. The <code>data</code> is
	 * transformed into bytes using the <code>ISO-8859-1</code> encoding.
	 *
	 * @param data
	 *            the data to send through response (as a raw string)
	 */
	public HTTPResponse(String data) {
		this(data == null ? new byte[] {} : data.getBytes());
	}

	/**
	 * Creates a new {@link HTTPResponse} using the {@link String} <code>data</code> as response data and the
	 * <code>encoding</code>.
	 *
	 * @param data
	 *            the {@link String} to be used as response body.
	 * @param encoding
	 *            the encoding used to transform the {@link String} <code>data</code> to bytes. The following encodings
	 *            can be used:
	 *            <ul>
	 *            <li><code>ISO-8859-1</code> ISO-8859-1 encoding, always supported by the platform
	 *            <li><code>UTF-8</code> UTF-8 encoding, only supported if the "Embed UTF-8 encoding" option is enabled
	 *            in the Run Configurations. If this option is not set, an {@link UnsupportedEncodingException} is
	 *            thrown.
	 *            <li><code>US-ASCII</code> US-ASCII encoding
	 *            </ul>
	 * @throws UnsupportedEncodingException
	 *             when the specified encoding is not supported.
	 */
	public HTTPResponse(String data, String encoding) throws UnsupportedEncodingException {
		this(data == null ? new byte[] {} : data.getBytes(encoding));
	}

	/**
	 * Creates a new {@link HTTPResponse} using the {@link InputStream} <code>body</code> as response data.
	 *
	 * @param status
	 *            the status of the response.
	 * @param mimeType
	 *            the mime type of the response.
	 * @param body
	 *            the {@link InputStream} to be used as response data, the stream will be closed automatically when the
	 *            response is sent..
	 */
	public HTTPResponse(String status, String mimeType, InputStream body) {
		this(body);
		setStatus(status);
		setMimeType(mimeType);
	}

	/**
	 * Creates a new {@link HTTPResponse} using the {@link String} <code>body</code> as response data.
	 *
	 * @param status
	 *            the status of the response.
	 * @param mimeType
	 *            the mime type of the response.
	 * @param body
	 *            the {@link String} to be used as response data.
	 */
	public HTTPResponse(String status, String mimeType, String body) {
		this(body);
		setStatus(status);
		setMimeType(mimeType);
	}

	/**
	 * Creates a {@link HTTPResponse} with given status and empty body.
	 *
	 * @param status
	 *            the status of the response.
	 * @return the empty response with given status.
	 */
	public static HTTPResponse createResponseFromStatus(String status) {
		return new HTTPResponse(status, null, ""); //$NON-NLS-1$
	}

	/**
	 * Adds a response header field.
	 *
	 * @param name
	 *            name of the header field to set.
	 * @param value
	 *            value of the header filed.
	 */
	public void addHeaderField(String name, String value) {
		this.header.put(name, value);
	}

	/**
	 * Returns the response header.
	 *
	 * @return a {@link Map} of (String,String) representing the HTTP header fields (may be empty).
	 */
	public Map<String, String> getHeader() {
		return (Map<String, String>) this.header.clone();
	}

	/**
	 * Returns the header field value associated to the given header field <code>key</code>.
	 *
	 * @param key
	 *            a header field name (if <code>null</code>, <code>null</code> is returned).
	 * @return the replied header field value, <code>null</code> if the header field is not found.
	 */
	public String getHeaderField(String key) {
		if (key == null) {
			return null;
		}
		return this.header.get(key.toLowerCase());
	}

	/**
	 * Returns the length (in bytes) of the response data or <code>-1</code> if the length is unknown.
	 *
	 * @return the length (in bytes) of the response data.
	 */
	protected long getLength() {
		return this.length;
	}

	/**
	 * Returns the MIME-TYPE of the response.
	 *
	 * @return the response MIME-TYPE.
	 */
	public String getMimeType() {
		return this.mimeType;
	}

	/**
	 * Returns the response status.
	 *
	 * @return the response status.
	 */
	public String getStatus() {
		return this.status;
	}

	/**
	 * Set the data contained by this response.
	 *
	 * @param data
	 *            the response data as a byte array (set to empty if <code>null</code> is given)
	 */
	private void setData(byte[] data) {
		byte[] result;
		if (data == null) {
			result = new byte[] {};
		} else {
			result = data;
		}
		this.data = result;
		setLength(result.length);
	}

	/**
	 * Sets the {@link InputStream} from which the response data can be read.
	 * <p>
	 * This method should be used only if response data length is not known in advance. If the length is known by
	 * advance the {@link #setData(InputStream, long)} should be used instead of this one. When response data is
	 * specified with this method, the response must be sent using the chunked transfer-coding which increase the
	 * response message size.
	 *
	 * @param dataStream
	 *            the {@link InputStream} from which the response data can be read, the stream will be closed
	 *            automatically when the response is sent.
	 */
	private void setData(InputStream dataStream) {
		setData(dataStream, -1);
	}

	/**
	 * Sets the {@link InputStream} from which the response data can be read.
	 * <p>
	 * This method should be used when response data length is known in advance. It allows to transfer response body
	 * without using the chunked transfer-coding. This reduces response message size.
	 *
	 * @param dataStream
	 *            the {@link InputStream} from which the response data can be read, the stream will be closed
	 *            automatically when the response is sent.
	 * @param length
	 *            the number of byte to be read from the {@link InputStream}.
	 */
	private void setData(InputStream dataStream, long length) {
		this.data = dataStream;
		setLength(length);
	}

	/**
	 * Sets the length of the response.
	 *
	 * @param length
	 *            the length of the response, if negative, the "content-length" field is removed.
	 */
	private void setLength(long length) {
		if (length < 0) {
			this.header.remove(HTTPConstants.FIELD_CONTENT_LENGTH);
		} else {
			this.header.put(HTTPConstants.FIELD_CONTENT_LENGTH, Long.toString(length));
		}
		this.length = length;
	}

	/**
	 * Set the response MIME-TYPE.
	 *
	 * @param mimeType
	 *            the response MIME-TYPE to set.
	 */
	public void setMimeType(String mimeType) {
		this.mimeType = mimeType;
	}

	/**
	 * Set the response status.
	 *
	 * @param status
	 *            the response status to set. Should be one of the <code>HTTP_STATUS_*</code> constants defined in
	 *            {@link HTTPConstants}
	 */
	public void setStatus(String status) {
		this.status = status;
	}

	/**
	 * Sends the {@link HTTPResponse} to the {@link OutputStream}.
	 * <p>
	 * If the data of this response is an {@link InputStream}, closes it.
	 *
	 * @throws IOException
	 *
	 */
	/* default */ void sendResponse(OutputStream outputStream, IHTTPEncodingHandler encodingHandler,
			HTTPEncodingRegistry encodingRegistry, int bufferSize) throws IOException {
		if (encodingHandler != null) {
			addHeaderField(HTTPConstants.FIELD_CONTENT_ENCODING, encodingHandler.getId());
		}

		long length = getLength();

		if (length < 0) {
			// data will be transmitted using chunked transfer coding
			// only when dataStream is used, the size is known otherwise
			addHeaderField(HTTPConstants.FIELD_TRANSFER_ENCODING,
					encodingRegistry.getChunkedTransferCodingHandler().getId());
		} // else the length is already defined in a header by the response

		writeHTTPHeader(outputStream);

		Object data = this.data;
		// only one of the next data can be defined.
		// A better way may be to specialize HTTPResponse for Raw String and
		// InputStream
		// and makes theses classes "visitable" by a HTTPWriter which is able to
		// visit both Raw String and InputStream HTTP Response
		// we keep this implementation to avoid new hierarchy for performance
		// but if the specialization evolves to a more and more
		// specific way, do it!
		if (data instanceof byte[]) {
			byte[] dataArray = (byte[]) data;
			sendRawDataResponse(dataArray, outputStream, encodingHandler, encodingRegistry);
		} else if (data != null) {
			try (InputStream dataStream = (InputStream) data) {
				sendInputStreamResponse(dataStream, outputStream, encodingHandler, encodingRegistry, bufferSize);
			}
		}

		outputStream.flush();
	}

	private void sendRawDataResponse(byte[] rawData, OutputStream outputStream, IHTTPEncodingHandler encodingHandler,
			HTTPEncodingRegistry encodingRegistry) throws IOException {
		try (OutputStream dataOutput = encodingRegistry.getIdentityTransferCodingHandler().open(this, outputStream)) {
			if (encodingHandler != null) {
				try (OutputStream encodedDataOutput = encodingHandler.open(dataOutput)) {
					writeAndFlush(rawData, encodedDataOutput);
				}
			} else {
				writeAndFlush(rawData, dataOutput);
			}
		}
	}

	private void sendInputStreamResponse(InputStream dataStream, OutputStream outputStream,
			IHTTPEncodingHandler encodingHandler, HTTPEncodingRegistry encodingRegistry, int bufferSize) {
		try (OutputStream dataOutput = (this.length == -1)
				? encodingRegistry.getChunkedTransferCodingHandler().open(this, outputStream)
				: encodingRegistry.getIdentityTransferCodingHandler().open(this, outputStream)) {
			try (OutputStream ecodedOutput = (encodingHandler != null) ? encodingHandler.open(dataOutput) : null) {
				OutputStream output = (ecodedOutput != null) ? ecodedOutput : dataOutput;
				final byte[] readBuffer = new byte[bufferSize];
				while (true) {
					int len = dataStream.read(readBuffer);

					if (len < 0) { // read until EOF is reached
						break;
					}
					// store read data
					output.write(readBuffer, 0, len);
					output.flush();
				}
			}
		} catch (Throwable t) {
			Messages.LOGGER.log(Level.SEVERE, Messages.CATEGORY_HOKA, Messages.ERROR_UNKNOWN, t);
		}
	}

	/**
	 * Writes the HTTP Header using the {@link OutputStream} <code>output</code>.
	 *
	 * @param output
	 *            {@link OutputStream}
	 * @throws IOException
	 *             when the connection is lost
	 */
	private void writeHTTPHeader(OutputStream output) throws IOException {
		final byte[] eofHeader = HTTPConstants.END_OF_LINE.getBytes();

		output.write(RESPONSE_HTTP11.getBytes());
		output.write(getStatus().getBytes());
		output.write(' ');
		output.write(eofHeader);

		if (this.mimeType != null) {
			output.write(RESPONSE_CONTENTTYPE.getBytes());
			output.write(this.mimeType.getBytes());
			output.write(eofHeader);
		}

		// add header parameters
		Map<String, String> header = getHeader();
		for (Entry<String, String> entry : header.entrySet()) {
			String key = entry.getKey();
			String value = entry.getValue();
			output.write(key.getBytes());
			output.write(RESPONSE_COLON.getBytes());
			output.write(value.getBytes());
			output.write(eofHeader);
		}

		output.write(eofHeader);
	}

	private static void writeAndFlush(byte[] data, OutputStream stream) throws IOException {
		stream.write(data);
		stream.flush();
		stream.close();
	}

	/**
	 * Create a {@link HTTPResponse} to write the <code>msg</code> for the given <code>status</code>.
	 *
	 * @param status
	 *            the error status. One of <code>HTTP_STATUS_*</code> constant of the {@link HTTPConstants} interface.
	 * @param msg
	 *            an optional error message to add in response.
	 * @return a {@link HTTPResponse} that represent the error.
	 * @see HTTPConstants#HTTP_STATUS_BADREQUEST
	 * @see HTTPConstants#HTTP_STATUS_FORBIDDEN
	 * @see HTTPConstants#HTTP_STATUS_INTERNALERROR
	 * @see HTTPConstants#HTTP_STATUS_MEDIA_TYPE
	 * @see HTTPConstants#HTTP_STATUS_METHOD
	 * @see HTTPConstants#HTTP_STATUS_NOTACCEPTABLE
	 * @see HTTPConstants#HTTP_STATUS_NOTFOUND
	 * @see HTTPConstants#HTTP_STATUS_NOTIMPLEMENTED
	 * @see HTTPConstants#HTTP_STATUS_NOTMODIFIED
	 * @see HTTPConstants#HTTP_STATUS_OK
	 * @see HTTPConstants#HTTP_STATUS_REDIRECT
	 */
	public static HTTPResponse createError(String status, String msg) {
		StringBuilder buffer = new StringBuilder();
		buffer.append("<html><head><title>"); //$NON-NLS-1$
		buffer.append(status);
		buffer.append("</title></head><body><h1>"); //$NON-NLS-1$
		buffer.append(status);
		buffer.append("</h1><p>"); //$NON-NLS-1$
		buffer.append(msg);
		buffer.append("</p></body></html>"); //$NON-NLS-1$

		HTTPResponse response = new HTTPResponse(buffer.toString());
		response.setMimeType(MIMEUtils.MIME_HTML);
		response.setStatus(status);
		return response;
	}

}