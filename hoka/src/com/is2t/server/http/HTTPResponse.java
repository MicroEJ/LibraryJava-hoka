/**
 * Java
 *
 * Copyright 2009-2013 IS2T. All rights reserved.
 * IS2T PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.is2t.server.http;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Hashtable;

/**
 * IS2T-API
 * <p>
 * Represents a HTTP Response.
 * </p>
 */
public class HTTPResponse {

	/**
	 * IS2T-API
	 * <p>
	 * Creates an empty {@link HTTPResponse}.
	 * </p>
	 */
	public HTTPResponse() {
		this(new byte[] {});
	}

	/**
	 * IS2T-API
	 * <p>
	 * Creates a new {@link HTTPResponse} using the given {@link InputStream} as
	 * the response data.
	 * </p>
	 * 
	 * @param data
	 *            the data to send through response (as a stream)
	 */
	public HTTPResponse(InputStream data) {
		setData(data);
	}

	/**
	 * IS2T-API
	 * <p>
	 * Creates a new {@link HTTPResponse} using the given {@link String} as
	 * response data. The <code>data</code> is transformed into bytes using the
	 * <code>ISO-8859-1</code> encoding.
	 * </p>
	 * 
	 * @param data
	 *            the data to send through response (as a raw string)
	 */
	public HTTPResponse(String data) {
		this(data == null ? new byte[] {} : data.getBytes());
	}

	/**
	 * IS2T-API
	 * <p>
	 * Creates a new {@link HTTPResponse} using the {@link String}
	 * <code>data</code> as response data and the <code>encoding</code>.
	 * </p>
	 * 
	 * @param data
	 *            the {@link String} to be used as response body.
	 * @param encoding
	 *            the encoding used to transform the {@link String}
	 *            <code>data</code> to bytes. The following encodings can be
	 *            used:
	 *            <ul>
	 *            <li><code>ISO-8859-1</code> ISO-8859-1 encoding, always
	 *            supported by the platform
	 *            <li><code>UTF-8</code> UTF-8 encoding, only supported if the
	 *            "Embed UTF-8 encoding" option is enabled in the Run
	 *            Configurations. If this option is not set, an
	 *            {@link UnsupportedEncodingException} is thrown.
	 *            <li><code>US-ASCII</code> US-ASCII encoding
	 *            </ul>
	 * @throws UnsupportedEncodingException
	 *             when the specified encoding is not supported.
	 */
	public HTTPResponse(String data, String encoding)
			throws UnsupportedEncodingException {
		this(data == null ? new byte[] {} : data.getBytes(encoding));
	}

	/**
	 * IS2T-API
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
	 * IS2T-API
	 * <p>
	 * Returns the response status.
	 * </p>
	 * 
	 * @return the response status.
	 */
	public String getStatus() {
		return status;
	}

	/**
	 * IS2T-API
	 * <p>
	 * Set the response status.
	 * </p>
	 * 
	 * @param status
	 *            the response status to set. Should be one of the
	 *            <code>HTTP_STATUS_*</code> constants defined in
	 *            {@link HTTPConstants}
	 */
	public void setStatus(String status) {
		this.status = status;
	}

	/**
	 * IS2T-API
	 * <p>
	 * Returns the MIME-TYPE of the response.
	 * </p>
	 * 
	 * @return the response MIME-TYPE.
	 */
	public String getMimeType() {
		return mimeType;
	}

	/**
	 * IS2T-API
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
	 * IS2T-API
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
		header.put(name, value);
	}

	/**
	 * IS2T-API
	 * <p>
	 * Returns the response header.
	 * </p>
	 * 
	 * @return a {@link Hashtable} of (String,String) representing the HTTP
	 *         Header Fields (may be empty).
	 */
	public Hashtable getHeader() {
		return header;
	}

	/*************************************************************************
	 * NOT IN API
	 ************************************************************************/

	private String status;
	/**
	 * The mime type.
	 */
	private String mimeType;

	/**
	 * Unique field to store the data object to be used, it can be both byte[]
	 * or InputStream.
	 */
	private Object data;

	/**
	 * <p>
	 * Do not update this value by hand, use {@link #setLength(long)} to
	 * maintain HTTP header.
	 * </p>
	 */
	private long length = -1; // -1 means unknown

	/**
	 * HTTP Response headers.
	 */
	private Hashtable header = new Hashtable(5);

	/**
	 * true if the {@link OutputStream} is closed.
	 * 
	 * @see #setDataStreamClosed()
	 * @see #close()
	 */
	private boolean dataStreamClosed = false;

	/**
	 * Sets the {@link InputStream} from which the response data can be read.
	 * 
	 * This method should be used when response data length is known in advance.
	 * It allows to transfer response body without using the chunked
	 * transfer-coding. This reduces response message size.
	 * 
	 * @param dataStream
	 *            the {@link InputStream} from which the response data can be
	 *            read.
	 * @param length
	 *            the number of byte to be read from the {@link InputStream}.
	 */
	private void setData(InputStream dataStream, long length) {
		this.data = dataStream;
		setLength(length);
	}

	/**
	 * Sets the {@link InputStream} from which the response data can be read.
	 * 
	 * This method should be used only if response data length is not known in
	 * advance. If the length is known by advance the
	 * {@link #setData(InputStream, long)} should be used instead of this one.
	 * When response data is specified with this method, the response must be
	 * sent using the chunked transfer-coding which increase the response
	 * message size.
	 * 
	 * @param dataStream
	 *            the {@link InputStream} from which the response data can be
	 *            read.
	 */
	private void setData(InputStream dataStream) {
		setData(dataStream, -1);
	}

	/**
	 * Set the data contained by this response.<br>
	 * 
	 * @param data
	 *            the response data as a byte array (set to empty if
	 *            <code>null</code> is given)
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
	 * Sets the length of the response.
	 * 
	 * @param length
	 *            the length of the response
	 */
	private void setLength(long length) {
		if (length < 0) {
			header.remove(HTTPConstants.FIELD_CONTENT_LENGTH);
		} else {
			header.put(HTTPConstants.FIELD_CONTENT_LENGTH, Long
					.toString(length));
		}
		this.length = length;
	}

	/**
	 * Returns the {@link InputStream} from which response data can be read.
	 * 
	 * @return an {@link InputStream} from which response data can be read. May
	 *         be <code>null</code>.
	 */
	protected InputStream getData() {
		// data can be both byte[] or InputStream according to uses but not both
		// so we used only one field
		// The privileged way to set data is to use a Stream, prefer catching
		// ClassClassException instead of instanceof
		try {
			return (InputStream) data;
		} catch (ClassCastException e) {
			return null;
		}
	}

	/**
	 * Returns the length (in bytes) of the response data or <code>-1</code> if
	 * the length is unknown.
	 * 
	 * @return the length (in bytes) of the response data.
	 */
	protected long getLength() {
		return length;
	}

	/**
	 * Returns the byte array from which response data can be read.
	 * 
	 * @return a byte array from which response data can be read. May be
	 *         <code>null</code>.
	 */
	protected byte[] getRawData() {
		// data can be both byte[] or InputStream according to uses but not both
		// so we used only one field
		// The privileged way to set data is to use a Stream, prefer an
		// instanceof check instead of catching ClassClassException
		if (data instanceof byte[]) {
			return (byte[]) data;
		}
		return null;
	}

	/**
	 * Used in HTTPSession to indicate that response dataStream has been
	 * successfully read and closed.
	 */
	protected void setDataStreamClosed() {
		dataStreamClosed = true;
	}

	/**
	 * <p>
	 * Close the data stream if the field {@link #data} is non null and the
	 * value of {@link #dataStreamClosed} is false.
	 * </p>
	 */
	protected void close() {
		InputStream data = getData();
		if (data != null && !dataStreamClosed) {
			try {
				data.close();
			} catch (IOException e) {
				// ignore
			}
		}
	}
}