/*
 * Java
 *
 * Copyright 2019 MicroEJ Corp. All rights reserved.
 * This library is provided in source code for use, modification and test, subject to license terms.
 * Any modification of the source code will break MicroEJ Corp. warranties on the whole library.
 */
package ej.hoka.auth;

import java.security.SecureRandom;
import java.util.Base64;

/**
 * An implementation of {@link Authenticator} that stores active sessions in a database and authenticate a request using
 * a session ID generated at login.
 */
public class SessionAuthenticator implements Authenticator {

	private static final long DEFAULT_SESSION_LIFETIME = 3600; // 1 hour

	private static final SecureRandom secureRandomNumberGenerator = new SecureRandom();
	private static final int TOKEN_SIZE = 128;

	private final long sessionLifetime; // in seconds

	private final SessionDataAccess database;

	/**
	 * Constructs a {@link SessionAuthenticator} with 1-hour-long sessions and using an in-memory database.
	 *
	 * @see InMemorySessionDataAccess
	 */
	public SessionAuthenticator() {
		this(DEFAULT_SESSION_LIFETIME);
	}

	/**
	 * Constructs a {@link SessionAuthenticator} using an in-memory database.
	 *
	 * @param sessionLifetime
	 *            the time before a session is considered invalid.
	 *
	 * @see InMemorySessionDataAccess
	 */
	public SessionAuthenticator(long sessionLifetime) {
		this(sessionLifetime, new InMemorySessionDataAccess());
	}

	/**
	 * Constructs a {@link SessionAuthenticator}.
	 *
	 * @param sessionLifetime
	 *            the time before a session is considered invalid.
	 * @param database
	 *            the database to store active sessions.
	 */
	public SessionAuthenticator(long sessionLifetime, SessionDataAccess database) {
		this.database = database;
		this.sessionLifetime = sessionLifetime;
	}

	/**
	 * Creates a new session with the given user identifier.
	 *
	 * @param uid
	 *            the identifier of the logged in user.
	 * @return the generated session ID.
	 */
	public String login(String uid) {
		synchronized (this.database) {
			Session session = this.database.getSessionByUser(uid);

			if (session != null) {
				if (session.isExpired()) {
					this.database.removeSession(session);
				} else {
					return session.sessionID;
				}
			}

			String sessionId = generateSessionID();
			long expiration = generateExpiration();

			this.database.addSession(new Session(uid, sessionId, expiration));

			return sessionId;
		}
	}

	@Override
	public String authenticate(String sessionID) {
		synchronized (this.database) {
			Session session = this.database.getSessionByID(sessionID);

			if (session == null) {
				return null;
			}

			if (session.isExpired()) {
				this.database.removeSession(session);
				return null;
			}

			return session.userID;
		}
	}

	/**
	 * Refreshes the expiration date of the session identified by <code>sessionID</code>.
	 *
	 * @param sessionID
	 *            the identifier of the session.
	 */
	public void refresh(String sessionID) {
		synchronized (this.database) {
			if (this.database.getSessionByID(sessionID) != null) {
				this.database.refreshSession(sessionID, generateExpiration());
			}
		}
	}

	/**
	 * Removes the session identified by <code>sessionID</code> from the active sessions.
	 *
	 * @param sessionID
	 *            the identifier of the session.
	 * @return <code>false</code> if no sessions are referenced by <code>sessionID</code>, <code>true</code> otherwise
	 */
	public boolean logout(String sessionID) {
		synchronized (this.database) {
			Session session = this.database.getSessionByID(sessionID);

			if (session != null) {
				this.database.removeSession(session);
				return true;
			}

			return false;
		}
	}

	/**
	 * Generates a new session ID encoded in base64.
	 *
	 * @return the generated session ID.
	 */
	protected String generateSessionID() {
		byte[] sidBytes = new byte[TOKEN_SIZE];
		secureRandomNumberGenerator.nextBytes(sidBytes);

		return Base64.getEncoder().encodeToString(sidBytes);
	}

	/**
	 * Generates the expiration date using the current real time.
	 *
	 * @return the generated expiration date.
	 *
	 * @see System#currentTimeMillis()
	 */
	protected long generateExpiration() {
		return System.currentTimeMillis() / 1000L + this.sessionLifetime;
	}

}
