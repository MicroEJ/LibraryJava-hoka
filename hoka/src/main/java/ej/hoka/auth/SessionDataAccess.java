/*
 * Java
 *
 * Copyright 2019 MicroEJ Corp. All rights reserved.
 * This library is provided in source code for use, modification and test, subject to license terms.
 * Any modification of the source code will break MicroEJ Corp. warranties on the whole library.
 */
package ej.hoka.auth;

/**
 * The interface to the database of active sessions.
 */
public interface SessionDataAccess {

	/**
	 * Adds a new session.
	 *
	 * @param newSession
	 *            the new {@link Session}.
	 */
	void addSession(Session newSession);

	/**
	 * Retrieves the active session of the given user.
	 *
	 * @param userID
	 *            the identifier of the user.
	 * @return the active session, or <code>null</code> if none matching the user.
	 */
	Session getSessionByUser(String userID);

	/**
	 * Retrieves the active session with given session ID.
	 *
	 * @param sessionID
	 *            the identifier of the session.
	 * @return the active session, or <code>null</code> if none with that ID.
	 */
	Session getSessionByID(String sessionID);

	/**
	 * Refreshes the expiration date of the session.
	 *
	 * @param sessionID
	 *            the ID of the session to refresh.
	 * @param newExpiration
	 *            the new expiration date.
	 */
	void refreshSession(String sessionID, long newExpiration);

	/**
	 * Removes a session.
	 *
	 * @param newSession
	 *            the {@link Session} to remove.
	 */
	void removeSession(Session newSession);

}
