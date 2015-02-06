/**
 * Java
 *
 * Copyright 2009-2013 IS2T. All rights reserved.
 * IS2T PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.is2t.server.http.encoding;

import java.io.UnsupportedEncodingException;

import com.is2t.server.http.HTTPConstants;

/**
 * IS2T-API
 * <p>
 * This exception is thrown when {@link HTTPConstants#FIELD_TRANSFER_ENCODING} or
 * {@link HTTPConstants#FIELD_CONTENT_ENCODING} cannot be handled by the server.
 * </p>
 */
public class UnsupportedHTTPEncodingException extends
		UnsupportedEncodingException {

	/**
	 * IS2T-API
	 * <p>
	 * The HTTP Header field causing the error.
	 * </p>
	 */
	public final String field;

	/**
	 * IS2T-API
	 * <p>
	 * The encoding which is not supported.
	 * </p>
	 */
	public final String encoding;

	/**
	 * IS2T-API
	 * <p>
	 * Creates a new {@link UnsupportedHTTPEncodingException} with the given
	 * parameters.
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
