/*
 * Java
 *
 * Copyright 2009-2019 MicroEJ Corp. All rights reserved.
 * This library is provided in source code for use, modification and test, subject to license terms.
 * Any modification of the source code will break MicroEJ Corp. warranties on the whole library.
 */
package ej.hoka.http;

/**
 * <p>
 * Configuration for {@link HTTPSession}.
 * </p>
 */
public final class CalibrationConstants {

	/**
	 * <p>
	 * if <code>true</code> the server should send a {@link HTTPConstants#HTTP_STATUS_NOTACCEPTABLE} if there is no
	 * {@link IHTTPEncodingHandler} registered for to handle the encoding specified in the HTTP request.
	 * </p>
	 *
	 * @see HTTPServer#registerEncodingHandler(ej.hoka.http.IHTTPEncodingHandler)
	 */
	public static final boolean STRICT_ACCEPT_ENCODING_COMPLIANCE = false;

	private CalibrationConstants() {
		// Forbid instanciation.
	}
}
