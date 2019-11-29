/*
 * Java
 *
 * Copyright 2019 MicroEJ Corp. All rights reserved.
 * This library is provided in source code for use, modification and test, subject to license terms.
 * Any modification of the source code will break MicroEJ Corp. warranties on the whole library.
 */
package ej.hoka.auth.session;

import ej.hoka.auth.SessionAuthenticator;

/**
 * Configuration for the cookie-based session authentication.
 *
 * @see LoginEndpoint
 * @see LogoutEndpoint
 * @see RestAuthenticatedRequestHandler
 */
public class CookieBasedSessionConfiguration {

	/**
	 * The name of the cookie used to store session ID.
	 *
	 * @see SessionAuthenticator
	 */
	public static final String COOKIE_NAME = "token"; //$NON-NLS-1$

	private CookieBasedSessionConfiguration() {
		// Forbid instantiation
	}

}
