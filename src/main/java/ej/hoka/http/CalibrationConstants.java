/*
 * Java
 *
 * Copyright 2009-2019 MicroEJ Corp. All rights reserved.
 * This library is provided in source code for use, modification and test, subject to license terms.
 * Any modification of the source code will break MicroEJ Corp. warranties on the whole library.
 */
package ej.hoka.http;

import ej.hoka.http.encoding.HTTPEncodingRegistry;
import ej.hoka.http.encoding.IHTTPEncodingHandler;

/**
 * Configuration for {@link HTTPServer}.
 */
public final class CalibrationConstants {

	/**
	 * If {@code true}, the server should send a {@link HTTPConstants#HTTP_STATUS_NOTACCEPTABLE} in case there is
	 * no {@link IHTTPEncodingHandler} registered to handle the encoding specified in the HTTP request.
	 *
	 * @see HTTPEncodingRegistry#registerEncodingHandler(ej.hoka.http.encoding.IHTTPEncodingHandler)
	 */
	public static final boolean STRICT_ACCEPT_ENCODING_COMPLIANCE = false;

	private CalibrationConstants() {
		// Forbid instanciation.
	}

}
