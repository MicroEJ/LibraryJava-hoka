/*
 * Java
 *
 * Copyright 2009-2016 IS2T. All rights reserved.
 * IS2T PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package ej.hoka.http;

/**
 * <p>
 * Configuration for {@link HTTPSession}.
 * </p>
 */
public class CalibrationConstants {

	/**
	 * <p>
	 * if <code>true</code> the server should send a {@link HTTPConstants#HTTP_STATUS_NOTACCEPTABLE} if there is no
	 * {@link IHTTPEncodingHandler} registered for to handle the encoding specified in the HTTP request.
	 * </p>
	 *
	 * @see HTTPServer#registerEncodingHandler(ej.hoka.http.IHTTPEncodingHandler)
	 */
	public static final boolean STRICT_ACCEPT_ENCODING_COMPLIANCE = false;
}
