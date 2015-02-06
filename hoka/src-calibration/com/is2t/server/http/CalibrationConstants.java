/**
 * Java
 *
 * Copyright 2009-2013 IS2T. All rights reserved.
 * IS2T PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.is2t.server.http;

import com.is2t.server.http.encoding.IHTTPEncodingHandler;

/**
 * IS2T-API
 * <p>
 * Configuration for {@link HTTPSession}.
 * </p>
 */
public class CalibrationConstants {

	/**
	 * IS2T-API
	 * <p>
	 * if <code>true</code> the server should send a
	 * {@link HTTPConstants#HTTP_STATUS_NOTACCEPTABLE} if there is no
	 * {@link IHTTPEncodingHandler} registered for to handle the encoding
	 * specified in the HTTP request.
	 * </p>
	 * 
	 * @see HTTPServer#registerEncodingHandler(com.is2t.server.http.encoding.IHTTPEncodingHandler)
	 */
	public static final boolean STRICT_ACCEPT_ENCODING_COMPLIANCE = false;
}
