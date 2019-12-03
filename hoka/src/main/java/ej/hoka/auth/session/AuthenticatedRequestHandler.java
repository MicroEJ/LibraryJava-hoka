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
import ej.hoka.http.HTTPConstants;
import ej.hoka.http.HTTPRequest;
import ej.hoka.http.HTTPResponse;
import ej.hoka.http.requesthandler.RequestHandlerComposite;

/**
 * A request handler that requires the user to be authenticated using cookie-based sessions to access the sub-handlers.
 *
 * @see LoginEndpoint
 * @see LogoutEndpoint
 */
public abstract class AuthenticatedRequestHandler extends RequestHandlerComposite {

	private final SessionAuthenticator authenticator;

	/**
	 * Constructs the request handler.
	 *
	 * @param authenticator
	 *            the {@link SessionAuthenticator} used to authenticate users.
	 */
	public AuthenticatedRequestHandler(SessionAuthenticator authenticator) {
		this.authenticator = authenticator;
	}

	@Override
	public HTTPResponse process(HTTPRequest request, Map<String, String> attributes) {
		if (!match(request)) {
			return null;
		}

		String sessionID = getSessionID(request, attributes);

		String username = this.authenticator.authenticate(sessionID);

		if (username == null) {
			return onFailedAuthentication(request, attributes);
		}

		return onSuccessfulAuthentication(request, attributes, username, sessionID);
	}

	/**
	 * Determines whether or not the request matches this handler.
	 *
	 * @param request
	 *            the request to match.
	 * @return <code>true</code> if the request matches this handler, <code>false</code> otherwise.
	 */
	protected abstract boolean match(HTTPRequest request);

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
	 * Constructs the response upon successful authentication.
	 *
	 * @param request
	 *            the {@link HTTPRequest}.
	 * @param attributes
	 *            the attributes populated by the request processing.
	 * @param username
	 *            the username of the authenticated user.
	 * @param sessionID
	 *            the ID of the session.
	 * @return the response to send.
	 */
	protected HTTPResponse onSuccessfulAuthentication(HTTPRequest request, Map<String, String> attributes,
			String username, String sessionID) {
		attributes.put("username", username); //$NON-NLS-1$

		HTTPResponse response = super.process(request, attributes);

		if (response == null) {
			return null;
		}

		response.addHeaderField("Set-Cookie", //$NON-NLS-1$
				CookieBasedSessionConfiguration.COOKIE_NAME + "=" + sessionID + "; HTTPOnly"); //$NON-NLS-1$ //$NON-NLS-2$

		return response;
	}

	/**
	 * Constructs the response upon failed authentication.
	 *
	 * @param request
	 *            the {@link HTTPRequest}.
	 * @param attributes
	 *            the attributes populated by the request processing.
	 * @return the response to send.
	 */
	protected HTTPResponse onFailedAuthentication(HTTPRequest request, Map<String, String> attributes) {
		return HTTPResponse.createError(HTTPConstants.HTTP_STATUS_UNAUTHORIZED, ""); //$NON-NLS-1$
	}

}