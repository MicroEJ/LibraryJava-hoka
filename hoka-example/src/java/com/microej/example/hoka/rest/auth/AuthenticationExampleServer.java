/*
 * Java
 *
 * Copyright 2019 MicroEJ Corp. All rights reserved.
 * For demonstration purpose only.
 * MicroEJ Corp. PROPRIETARY. Use is subject to license terms.
 */
package com.microej.example.hoka.rest.auth;

import java.io.IOException;
import java.util.Map;

import ej.hoka.auth.SessionAuthenticator;
import ej.hoka.auth.session.LoginEndpoint;
import ej.hoka.auth.session.LogoutEndpoint;
import ej.hoka.auth.session.RestAuthenticatedRequestHandler;
import ej.hoka.http.HTTPConstants;
import ej.hoka.http.HTTPRequest;
import ej.hoka.http.HTTPResponse;
import ej.hoka.http.HTTPServer;
import ej.hoka.http.requesthandler.RequestHandlerComposite;
import ej.hoka.http.support.MIMEUtils;
import ej.hoka.rest.RestEndpoint;
import ej.hoka.rest.RestRequestHandler;

public class AuthenticationExampleServer {

	public static void main(String[] args) throws IOException {
		SessionAuthenticator sessionAuthenticator = new SessionAuthenticator();

		// Create a request handler responsible of handling request to public endpoints

		RestRequestHandler publicHandler = new RestRequestHandler();
		publicHandler.addEndpoint(createLoginEndpoint("/api/login", sessionAuthenticator));
		publicHandler.addEndpoint(createLogoutEndpoint("/api/logout", sessionAuthenticator));

		// Create a request handler responsible of handling request to "private"
		// endpoints

		RestAuthenticatedRequestHandler privateHandler = new RestAuthenticatedRequestHandler(sessionAuthenticator,
				"/api/private/");
		privateHandler.addEndpoint(new HelloEndpoint("/api/private/hello"));

		// Start the server with the two REST request handlers

		RequestHandlerComposite handler = new RequestHandlerComposite();
		handler.addRequestHandler(publicHandler);
		handler.addRequestHandler(privateHandler);

		HTTPServer server = new HTTPServer(8080, 10, 3, handler);
		server.start();
	}

	private static RestEndpoint createLoginEndpoint(String uri, SessionAuthenticator sessionAuthenticator) {
		return new LoginEndpoint("/api/login", sessionAuthenticator) {
			@Override
			protected String login(HTTPRequest request, Map<String, String> attributes) {
				// The username is located in the parameters of the request.
				// No password required.
				return request.getParameters().get("username");
			}

			@Override
			protected HTTPResponse successfulLoginResponse(HTTPRequest request, Map<String, String> attributes) {
				HTTPResponse response = new HTTPResponse("Welcome " + attributes.get("username") + " !");
				response.setStatus(HTTPConstants.HTTP_STATUS_OK);
				response.setMimeType(MIMEUtils.MIME_PLAINTEXT);
				return response;
			}

			@Override
			protected HTTPResponse failedLoginResponse(HTTPRequest request, Map<String, String> attributes) {
				StringBuilder sb = new StringBuilder();
				sb.append("<body>");
				sb.append("<form action=\"" + getURI() + "\" method=\"get\">");
				sb.append("Username : <input type=\"text\" name=\"username\" />");
				sb.append("<input type=\"submit\" value=\"Login\" />");
				sb.append("</form>");
				sb.append("</body>");
				HTTPResponse response = new HTTPResponse(sb.toString());
				response.setStatus(HTTPConstants.HTTP_STATUS_OK);
				response.setMimeType(MIMEUtils.MIME_HTML);
				return response;
			}
		};
	}

	private static RestEndpoint createLogoutEndpoint(String uri, SessionAuthenticator sessionAuthenticator) {
		return new LogoutEndpoint(uri, sessionAuthenticator) {
			@Override
			protected HTTPResponse successfulLogoutResponse(HTTPRequest request, Map<String, String> attributes) {
				HTTPResponse response = new HTTPResponse("Successfully logged out.");

				response.setStatus(HTTPConstants.HTTP_STATUS_OK);
				response.setMimeType(MIMEUtils.MIME_PLAINTEXT);

				return response;
			}

			@Override
			protected HTTPResponse failedLogoutResponse(HTTPRequest request, Map<String, String> attributes) {
				return HTTPResponse.createError(HTTPConstants.HTTP_STATUS_BADREQUEST, "");
			}
		};
	}

}
