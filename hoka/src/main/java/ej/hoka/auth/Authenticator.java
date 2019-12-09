/*
 * Java
 *
 * Copyright 2019 MicroEJ Corp. All rights reserved.
 * This library is provided in source code for use, modification and test, subject to license terms.
 * Any modification of the source code will break MicroEJ Corp. warranties on the whole library.
 */
package ej.hoka.auth;

/**
 * Interface for authentication.
 */
public interface Authenticator {

	/**
	 * Authenticate using the given token.
	 *
	 * @param token
	 *            the token used for authentication.
	 * @return the user ID authenticated by <code>token</code>, or null if authentication failed.
	 */
	String authenticate(String token);

}
