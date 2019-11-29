/*
 * Java
 *
 * Copyright 2019 MicroEJ Corp. All rights reserved.
 * This library is provided in source code for use, modification and test, subject to license terms.
 * Any modification of the source code will break MicroEJ Corp. warranties on the whole library.
 */
package ej.hoka.auth;

import java.util.HashMap;

import ej.basictool.map.PackedMap;

/**
 * A {@link SessionDataAccess} implementation that uses {@link HashMap} to map user ID to session ID, session ID to user
 * ID and session ID to session expiration.
 * <p>
 * Sessions aren't automatically removed when expired.
 */
public class InMemorySessionDataAccess implements SessionDataAccess {

	private final PackedMap<String, String> activeSessionIDs = new PackedMap<>();
	private final PackedMap<String, String> activeUserIDs = new PackedMap<>();
	private final PackedMap<String, Long> activeSessionExp = new PackedMap<>();

	@Override
	public void addSession(Session session) {
		this.activeSessionIDs.put(session.userID, session.sessionID);
		this.activeUserIDs.put(session.sessionID, session.userID);
		this.activeSessionExp.put(session.sessionID, Long.valueOf(session.sessionExpiration));
	}

	@Override
	public Session getSessionByUser(String userID) {
		if (userID == null || !this.activeSessionIDs.containsKey(userID)) {
			return null;
		}
		String sessionID = this.activeSessionIDs.get(userID);
		Long sessionExp = this.activeSessionExp.get(sessionID);
		return new Session(userID, sessionID, sessionExp.longValue());
	}

	@Override
	public Session getSessionByID(String sessionID) {
		if (sessionID == null || !this.activeUserIDs.containsKey(sessionID)) {
			return null;
		}
		String userID = this.activeUserIDs.get(sessionID);
		Long sessionExp = this.activeSessionExp.get(sessionID);
		return new Session(userID, sessionID, sessionExp.longValue());
	}

	@Override
	public void refreshSession(String sessionID, long newExpiration) {
		this.activeSessionExp.put(sessionID, Long.valueOf(newExpiration));
	}

	@Override
	public void removeSession(Session session) {
		this.activeSessionIDs.remove(session.userID);
		this.activeUserIDs.remove(session.sessionID);
		this.activeSessionExp.remove(session.sessionID);
	}

}
