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
 * RestEndpoint that logs in users using cookie-based sessions.
 *
 * @see LogoutEndpoint
 * @see AuthenticatedRequestHandler
 */
public abstract class LoginEndpoint extends RestEndpoint {

	private final SessionAuthenticator authenticator;

	/**
	 * Constructs the endpoint at given URI.
	 *
	 * @param uri
	 *            the URI of the {@link RestEndpoint}.
	 * @param authenticator
	 *            the {@link SessionAuthenticator} used to login users.
	 */
	public LoginEndpoint(String uri, SessionAuthenticator authenticator) {
		super(uri);
		this.authenticator = authenticator;
	}

	@Override
	public HTTPResponse get(HTTPRequest request, Map<String, String> attributes) {
		return process(request, attributes);
	}

	@Override
	public HTTPResponse put(HTTPRequest request, Map<String, String> attributes) {
		return process(request, attributes);
	}

	/**
	 * Checks the credentials in the request.
	 *
	 * @param request
	 *            the {@link HTTPRequest}.
	 * @param attributes
	 *            the attributes populated by the request processing.
	 * @return the username of the logged in user, or <code>null</code> if login failed.
	 */
	protected abstract String login(HTTPRequest request, Map<String, String> attributes);

	/**
	 * Constructs the response upon successful login.
	 *
	 * @param request
	 *            the {@link HTTPRequest}.
	 * @param attributes
	 *            the attributes populated by the request processing.
	 * @return the response to send.
	 */
	protected abstract HTTPResponse successfulLoginResponse(HTTPRequest request, Map<String, String> attributes);

	/**
	 * Constructs the response upon failed login.
	 *
	 * @param request
	 *            the {@link HTTPRequest}.
	 * @param attributes
	 *            the attributes populated by the request processing.
	 * @return the response to send.
	 */
	protected abstract HTTPResponse failedLoginResponse(HTTPRequest request, Map<String, String> attributes);

	/**
	 * Handles the login and send the appropriate response.
	 *
	 * @param request
	 *            the {@link HTTPRequest}.
	 * @param attributes
	 *            the attributes populated by the request processing.
	 * @param username
	 *            the username of the logged in user.
	 * @param sessionID
	 *            the sessionID of the new session.
	 * @return the response to send.
	 */
	protected HTTPResponse onSuccessfulLogin(HTTPRequest request, Map<String, String> attributes, String username,
			String sessionID) {
		attributes.put("username", username); //$NON-NLS-1$

		HTTPResponse response = successfulLoginResponse(request, attributes);

		response.addHeaderField("Set-Cookie", //$NON-NLS-1$
				CookieBasedSessionConfiguration.COOKIE_NAME + "=" + sessionID + "; HTTPOnly"); //$NON-NLS-1$ //$NON-NLS-2$

		return response;
	}

	private HTTPResponse process(HTTPRequest request, Map<String, String> attributes) {
		String username = login(request, attributes);

		if (username == null) {
			return failedLoginResponse(request, attributes);
		}

		String sessionID = this.authenticator.login(username);

		return onSuccessfulLogin(request, attributes, username, sessionID);
	}

}