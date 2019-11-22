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
import java.util.Hashtable;
import java.util.Map;
import java.util.Map.Entry;

import ej.hoka.http.encoding.HTTPEncodingRegister;
import ej.hoka.http.encoding.IHTTPEncodingHandler;
import ej.hoka.http.encoding.IHTTPTransferCodingHandler;

/**
 * <p>
 * Represents a HTTP Response.
 * </p>
 */
public class HTTPResponse {

	/**
	 * An empty HTTP response with status code 200.
	 */
	public static final HTTPResponse RESPONSE_OK = createResponseFromStatus(HTTPConstants.HTTP_STATUS_OK);
	/**
	 * An empty HTTP response with status code 301.
	 */
	public static final HTTPResponse RESPONSE_MOVED_PERMANENTLY = createResponseFromStatus(
			HTTPConstants.HTTP_STATUS_REDIRECT);
	/**
	 * An empty HTTP response with status code 304.
	 */
	public static final HTTPResponse RESPONSE_NOT_MODIFIED = createResponseFromStatus(
			HTTPConstants.HTTP_STATUS_NOTMODIFIED);
	/**
	 * An empty HTTP response with status code 400.
	 */
	public static final HTTPResponse RESPONSE_BAD_REQUEST = createResponseFromStatus(
			HTTPConstants.HTTP_STATUS_BADREQUEST);
	/**
	 * An empty HTTP response with status code 401.
	 */
	public static final HTTPResponse RESPONSE_UNAUTHORIZED = createResponseFromStatus(
			HTTPConstants.HTTP_STATUS_UNAUTHORIZED);
	/**
	 * An empty HTTP response with status code 403.
	 */
	public static final HTTPResponse RESPONSE_FORBIDDEN = createResponseFromStatus(HTTPConstants.HTTP_STATUS_FORBIDDEN);
	/**
	 * An empty HTTP response with status code 404.
	 */
	public static final HTTPResponse RESPONSE_NOT_FOUND = createResponseFromStatus(HTTPConstants.HTTP_STATUS_NOTFOUND);
	/**
	 * An empty HTTP response with status code 405.
	 */
	public static final HTTPResponse RESPONSE_METHOD_NOT_ALLOWED = createResponseFromStatus(
			HTTPConstants.HTTP_STATUS_METHOD);
	/**
	 * An empty HTTP response with status code 406.
	 */
	public static final HTTPResponse RESPONSE_NOT_ACCEPTABLE = createResponseFromStatus(
			HTTPConstants.HTTP_STATUS_NOTACCEPTABLE);
	/**
	 * An empty HTTP response with status code 408.
	 */
	public static final HTTPResponse RESPONSE_REQUESTTIMEOUT = createResponseFromStatus(
			HTTPConstants.HTTP_STATUS_REQUESTTIMEOUT);
	/**
	 * An empty HTTP response with status code 415.
	 */
	public static final HTTPResponse RESPONSE_UNSUPPORTED_MEDIA_TYPE = createResponseFromStatus(
			HTTPConstants.HTTP_STATUS_MEDIA_TYPE);
	/**
	 * An empty HTTP response with status code 500.
	 */
	public static final HTTPResponse RESPONSE_INTERNAL_ERROR = createResponseFromStatus(
			HTTPConstants.HTTP_STATUS_INTERNALERROR);
	/**
	 * An empty HTTP response with status code 501.
	 */
	public static final HTTPResponse RESPONSE_NOT_IMPLEMENTED = createResponseFromStatus(
			HTTPConstants.HTTP_STATUS_NOTIMPLEMENTED);

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

	private String status;

	/**
	 * The mime type.
	 */
	private String mimeType;

	/**
	 * Unique field to store the data object to be used, it can be both byte[] or InputStream.
	 */
	private Object data;

	/**
	 * <p>
	 * Do not update this value by hand, use {@link #setLength(long)} to maintain HTTP header.
	 * </p>
	 */
	private long length = -1; // -1 means unknown

	/**
	 * HTTP Response headers.
	 */
	private final HashMap<String, String> header = new HashMap<>(5);

	/**
	 * true if the {@link OutputStream} is closed.
	 *
	 * @see #setDataStreamClosed()
	 * @see #close()
	 */
	private boolean dataStreamClosed = false;

	/**
	 * <p>
	 * Creates an empty {@link HTTPResponse}.
	 * </p>
	 */
	public HTTPResponse() {
		this(new byte[] {});
	}

	/**
	 * <p>
	 * Creates a new {@link HTTPResponse} using the given byte array as response data.
	 * </p>
	 *
	 * @param data
	 *            the data to send through response (as a raw byte array)
	 */
	public HTTPResponse(byte[] data) {
		setData(data);
	}

	/**
	 * <p>
	 * Creates a new {@link HTTPResponse} using the given {@link InputStream} as the response data.
	 * </p>
	 *
	 * @param data
	 *            the data to send through response (as a stream)
	 */
	public HTTPResponse(InputStream data) {
		setData(data);
	}

	/**
	 * <p>
	 * Creates a new {@link HTTPResponse} using the given {@link String} as response data. The <code>data</code> is
	 * transformed into bytes using the <code>ISO-8859-1</code> encoding.
	 * </p>
	 *
	 * @param data
	 *            the data to send through response (as a raw string)
	 */
	public HTTPResponse(String data) {
		this(data == null ? new byte[] {} : data.getBytes());
	}

	/**
	 * <p>
	 * Creates a new {@link HTTPResponse} using the {@link String} <code>data</code> as response data and the
	 * <code>encoding</code>.
	 * </p>
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

	private static HTTPResponse createResponseFromStatus(String status) {
		HTTPResponse response = new HTTPResponse();
		response.setStatus(status);
		response.addHeaderField(HTTPConstants.FIELD_CONNECTION, HTTPConstants.CONNECTION_FIELD_VALUE_CLOSE);
		return response;
	}

	/**
	 * <p>
	 * Adds a response header field.
	 * </p>
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
	 * <p>
	 * Close the data stream if the field {@link #data} is non null and the value of {@link #dataStreamClosed} is false.
	 * </p>
	 */
	protected void close() {
		InputStream data = getData();
		if ((data != null) && !this.dataStreamClosed) {
			try {
				data.close();
			} catch (IOException e) {
				// ignore
			}
		}
	}

	/**
	 * Returns the {@link InputStream} from which response data can be read.
	 *
	 * @return an {@link InputStream} from which response data can be read. May be <code>null</code>.
	 */
	protected InputStream getData() {
		// data can be both byte[] or InputStream according to uses but not both
		// so we used only one field
		// The privileged way to set data is to use a Stream, prefer catching
		// ClassClassException instead of instanceof
		try {
			return (InputStream) this.data;
		} catch (ClassCastException e) {
			return null;
		}
	}

	/**
	 * <p>
	 * Returns the response header.
	 * </p>
	 *
	 * @return a {@link Hashtable} of (String,String) representing the HTTP Header Fields (may be empty).
	 */
	public Map<String, String> getHeader() {
		return (Map<String, String>) this.header.clone();
	}

	/**
	 *
	 * <p>
	 * Returns the header field value associated to the given header field <code>key</code>.
	 * </p>
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
	 * <p>
	 * Returns the MIME-TYPE of the response.
	 * </p>
	 *
	 * @return the response MIME-TYPE.
	 */
	public String getMimeType() {
		return this.mimeType;
	}

	/**
	 * Returns the byte array from which response data can be read.
	 *
	 * @return a byte array from which response data can be read. May be <code>null</code>.
	 */
	protected byte[] getRawData() {
		// data can be both byte[] or InputStream according to uses but not both
		// so we used only one field
		// The privileged way to set data is to use a Stream, prefer an
		// instanceof check instead of catching ClassClassException
		if (this.data instanceof byte[]) {
			return (byte[]) this.data;
		}
		return null;
	}

	/**
	 * <p>
	 * Returns the response status.
	 * </p>
	 *
	 * @return the response status.
	 */
	public String getStatus() {
		return this.status;
	}

	/**
	 * Set the data contained by this response.<br>
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
	 *
	 * This method should be used only if response data length is not known in advance. If the length is known by
	 * advance the {@link #setData(InputStream, long)} should be used instead of this one. When response data is
	 * specified with this method, the response must be sent using the chunked transfer-coding which increase the
	 * response message size.
	 *
	 * @param dataStream
	 *            the {@link InputStream} from which the response data can be read.
	 */
	private void setData(InputStream dataStream) {
		setData(dataStream, -1);
	}

	/**
	 * Sets the {@link InputStream} from which the response data can be read.
	 *
	 * This method should be used when response data length is known in advance. It allows to transfer response body
	 * without using the chunked transfer-coding. This reduces response message size.
	 *
	 * @param dataStream
	 *            the {@link InputStream} from which the response data can be read.
	 * @param length
	 *            the number of byte to be read from the {@link InputStream}.
	 */
	private void setData(InputStream dataStream, long length) {
		this.data = dataStream;
		setLength(length);
	}

	/**
	 * Used in HTTPSession to indicate that response dataStream has been successfully read and closed.
	 */
	protected void setDataStreamClosed() {
		this.dataStreamClosed = true;
	}

	/**
	 * Sets the length of the response.
	 *
	 * @param length
	 *            the length of the response
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
	 * <p>
	 * Set the response MIME-TYPE.
	 * </p>
	 *
	 * @param mimeType
	 *            the response MIME-TYPE to set.
	 */
	public void setMimeType(String mimeType) {
		this.mimeType = mimeType;
	}

	/**
	 * <p>
	 * Set the response status.
	 * </p>
	 *
	 * @param status
	 *            the response status to set. Should be one of the <code>HTTP_STATUS_*</code> constants defined in
	 *            {@link HTTPConstants}
	 */
	public void setStatus(String status) {
		this.status = status;
	}

	/**
	 * Send the {@link HTTPResponse} to the given {@link OutputStream} using the given {@link IHTTPEncodingHandler}.
	 *
	 * @param output
	 *            {@link OutputStream} used to write the response to
	 * @param encodingHandler
	 *            the {@link IHTTPEncodingHandler} to encode the response. If <code>null</code>, the
	 *            {@link IHTTPTransferCodingHandler} is used.
	 * @throws IOException
	 *             if the connection has been lost
	 */
	protected void writeResponse(OutputStream output, IHTTPEncodingHandler encodingHandler,
			HTTPEncodingRegister encodingRegister, int bufferSize) throws IOException {
	}

	/**
	 * Writes the HTTP Header using the {@link OutputStream} <code>output</code>.
	 *
	 * @param output
	 *            {@link OutputStream}
	 * @throws IOException
	 *             when the connection is lost
	 */
	void writeHTTPHeader(OutputStream output) throws IOException {
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

}