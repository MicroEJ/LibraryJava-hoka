/*
 * Java
 *
 * Copyright 2009-2019 MicroEJ Corp. All rights reserved.
 * This library is provided in source code for use, modification and test, subject to license terms.
 * Any modification of the source code will break MicroEJ Corp. warranties on the whole library.
 */
package ej.hoka.http.encoding;

import java.io.UnsupportedEncodingException;

import ej.hoka.http.HTTPConstants;

/**
 * <p>
 * This exception is thrown when {@link HTTPConstants#FIELD_TRANSFER_ENCODING} or
 * {@link HTTPConstants#FIELD_CONTENT_ENCODING} cannot be handled by the server.
 * </p>
 */
public class UnsupportedHTTPEncodingException extends UnsupportedEncodingException {

	private static final long serialVersionUID = 5909681010475316898L;

	/**
	 * The colon character.
	 */
	private static final String RESPONSE_COLON = ": "; //$NON-NLS-1$

	/**
	 * <p>
	 * The HTTP Header field causing the error.
	 * </p>
	 */
	private final String field;

	/**
	 * <p>
	 * The encoding which is not supported.
	 * </p>
	 */
	private final String encoding;

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

	@Override
	public String getMessage() {
		return this.field + RESPONSE_COLON + this.encoding;
	}

}
