/*
 * Java
 *
 * Copyright 2009-2016 IS2T. All rights reserved.
 * IS2T PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package ej.hoka.http;

import java.io.UnsupportedEncodingException;

/**
 * <p>
 * This exception is thrown when {@link HTTPConstants#FIELD_TRANSFER_ENCODING} or
 * {@link HTTPConstants#FIELD_CONTENT_ENCODING} cannot be handled by the server.
 * </p>
 */
public class UnsupportedHTTPEncodingException extends UnsupportedEncodingException {

	private static final long serialVersionUID = 5909681010475316898L;

	/**
	 * <p>
	 * The HTTP Header field causing the error.
	 * </p>
	 */
	public final String field;

	/**
	 * <p>
	 * The encoding which is not supported.
	 * </p>
	 */
	public final String encoding;

	/**
	 * <p>
	 * Creates a new {@link UnsupportedHTTPEncodingException} with the given parameters.
	 * </p>
	 *
	 * @param field
	 *            the HTTP Header field causing the error.
	 * @param encoding
	 *            the encoding which is not supported.
	 */
	public UnsupportedHTTPEncodingException(String field, String encoding) {
		this.field = field;
		this.encoding = encoding;
	}

}
