/*
 * Java
 *
 * Copyright 2019 MicroEJ Corp. All rights reserved.
 * This library is provided in source code for use, modification and test, subject to license terms.
 * Any modification of the source code will break MicroEJ Corp. warranties on the whole library.
 */
package ej.hoka.auth;

/**
 * Class representing a session.
 *
 * @see SessionAuthenticator
 */
public class Session {

	/* package */ final String userID;
	/* package */ final String sessionID;
	/* package */ final long sessionExpiration;

	/**
	 * Constructs a {@link Session}.
	 *
	 * @param userID
	 *            the identifier of the user.
	 * @param sessionID
	 *            the identifier of the session.
	 * @param expiration
	 *            the expiration date of the session.
	 */
	public Session(String userID, String sessionID, long expiration) {
		this.userID = userID;
		this.sessionID = sessionID;
		this.sessionExpiration = expiration;
	}

	/**
	 * Checks whether the session has expired or not.
	 *
	 * @return <code>true</code> if the session has expired, <code>false</code> otherwise.
	 */
	public boolean isExpired() {
		return 1000L * this.sessionExpiration < System.currentTimeMillis();
	}

}
