/*
 * Java
 *
 * Copyright 2019-2020 MicroEJ Corp. All rights reserved.
 * This library is provided in source code for use, modification and test, subject to license terms.
 * Any modification of the source code will break MicroEJ Corp. warranties on the whole library.
 */
package ej.hoka.auth;

import java.util.Base64;
import java.util.Random;

/**
 * An implementation of {@link Authenticator} that stores active sessions in a database and authenticate a request using
 * a session ID generated at login.
 */
public class SessionAuthenticator implements Authenticator {

	private static final long ONE_SECOND = 1000L;
	private static final long DEFAULT_SESSION_LIFETIME = 3600L; // 1 hour

	private static final int TOKEN_SIZE = 128;

	private final Random randomNumberGenerator;
	private final long sessionLifetime; // in seconds

	private final SessionDataAccess database;

	/**
	 * Constructs a {@link SessionAuthenticator} with 1-hour-long sessions and using an in-memory database.
	 *
	 * @see InMemorySessionDataAccess
	 * @deprecated Uses an insecure implementation of {@link Random}.
	 */
	@Deprecated
	public SessionAuthenticator() {
		this(DEFAULT_SESSION_LIFETIME);
	}

	/**
	 * Constructs a {@link SessionAuthenticator} with 1-hour-long sessions and using an in-memory database.
	 *
	 * Use a secure {@link Random} implementation (see java.security.SecureRandom).
	 *
	 * @param random
	 *            the random number generator used to create session IDs.
	 *
	 * @see InMemorySessionDataAccess
	 */
	public SessionAuthenticator(Random random) {
		this(random, DEFAULT_SESSION_LIFETIME);
	}

	/**
	 * Constructs a {@link SessionAuthenticator} using an in-memory database.
	 *
	 * @param sessionLifetime
	 *            the time before a session is considered invalid.
	 *
	 * @see InMemorySessionDataAccess
	 * @deprecated Uses an insecure implementation of {@link Random}.
	 */
	@Deprecated
	public SessionAuthenticator(long sessionLifetime) {
		this(sessionLifetime, new InMemorySessionDataAccess());
	}

	/**
	 * Constructs a {@link SessionAuthenticator} with 1-hour-long sessions and using an in-memory database.
	 *
	 * Use a secure {@link Random} implementation (see java.security.SecureRandom).
	 *
	 * @param random
	 *            the random number generator used to create session IDs.
	 * @param sessionLifetime
	 *            the time before a session is considered invalid.
	 *
	 * @see InMemorySessionDataAccess
	 */
	public SessionAuthenticator(Random random, long sessionLifetime) {
		this(random, sessionLifetime, new InMemorySessionDataAccess());
	}

	/**
	 * Constructs a {@link SessionAuthenticator}.
	 *
	 * @param sessionLifetime
	 *            the time before a session is considered invalid.
	 * @param database
	 *            the database to store active sessions.
	 *
	 * @deprecated Uses an insecure implementation of {@link Random}.
	 */
	@Deprecated
	public SessionAuthenticator(long sessionLifetime, SessionDataAccess database) {
		this(new Random(), sessionLifetime, database);
	}

	/**
	 * Constructs a {@link SessionAuthenticator}.
	 *
	 * Use a secure {@link Random} implementation (see java.security.SecureRandom).
	 *
	 * @param random
	 *            the random number generator used to create session IDs.
	 * @param sessionLifetime
	 *            the time before a session is considered invalid.
	 * @param database
	 *            the database to store active sessions.
	 */
	public SessionAuthenticator(Random random, long sessionLifetime, SessionDataAccess database) {
		this.randomNumberGenerator = random;
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
	 * @return {@code false} if no sessions are referenced by <code>sessionID</code>, {@code true} otherwise
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
		this.randomNumberGenerator.nextBytes(sidBytes);

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
		return System.currentTimeMillis() / ONE_SECOND + this.sessionLifetime;
	}

}
