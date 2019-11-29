/*
 * Java
 *
 * Copyright 2019 MicroEJ Corp. All rights reserved.
 * This library is provided in source code for use, modification and test, subject to license terms.
 * Any modification of the source code will break MicroEJ Corp. warranties on the whole library.
 */
package ej.hoka.auth.session;

import java.util.Map;

import ej.hoka.auth.SessionAuthenticator;
import ej.hoka.http.HTTPRequest;
import ej.hoka.http.HTTPResponse;
import ej.hoka.rest.RestEndpoint;

/**
 * RestEndpoint that logs out users using cookie-based sessions.
 *
 * @see LoginEndpoint
 * @see RestAuthenticatedRequestHandler
 */
public abstract class LogoutEndpoint extends RestEndpoint {

	private final SessionAuthenticator sessionAuthenticator;

	/**
	 * Constructs the endpoint at given URI.
	 *
	 * @param uri
	 *            the URI of the {@link RestEndpoint}.
	 * @param sessionAuthenticator
	 *            the {@link SessionAuthenticator} used to logout users.
	 */
	public LogoutEndpoint(String uri, SessionAuthenticator sessionAuthenticator) {
		super(uri);
		this.sessionAuthenticator = sessionAuthenticator;
	}

	@Override
	public HTTPResponse get(HTTPRequest request, Map<String, String> attributes) {
		String sessionID = getSessionID(request, attributes);

		if (sessionID == null) {
			return failedLogoutResponse(request, attributes);
		}

		if (!this.sessionAuthenticator.logout(sessionID)) {
			return setLoggedOut(failedLogoutResponse(request, attributes));
		}

		return setLoggedOut(successfulLogoutResponse(request, attributes));
	}

	/**
	 * Retrieves the session ID from the cookies of the request.
	 *
	 * @param request
	 *            the {@link HTTPRequest}.
	 * @param attributes
	 *            the attributes populated by the request processing.
	 * @return the session ID.
	 */
	protected String getSessionID(HTTPRequest request, Map<String, String> attributes) {
		return request.getCookie(CookieBasedSessionConfiguration.COOKIE_NAME);
	}

	/**
	 * Constructs the response upon successful logout.
	 *
	 * @param request
	 *            the {@link HTTPRequest}.
	 * @param attributes
	 *            the attributes populated by the request processing.
	 * @return the response to send.
	 */
	protected abstract HTTPResponse successfulLogoutResponse(HTTPRequest request, Map<String, String> attributes);

	/**
	 * Constructs the response upon failed logout.
	 *
	 * @param request
	 *            the {@link HTTPRequest}.
	 * @param attributes
	 *            the attributes populated by the request processing.
	 * @return the response to send.
	 */
	protected abstract HTTPResponse failedLogoutResponse(HTTPRequest request, Map<String, String> attributes);

	/**
	 * Updates the <code>response</code> to delete the session ID cookie.
	 *
	 * @param response
	 *            the response to send.
	 * @return the response with the expired "Set-Cookie".
	 */
	protected HTTPResponse setLoggedOut(HTTPResponse response) {
		response.addHeaderField("Set-Cookie", //$NON-NLS-1$
				CookieBasedSessionConfiguration.COOKIE_NAME + "=; expires=Thu, 01 Jan 1970 00:00:00 GMT"); //$NON-NLS-1$

		return response;
	}

}