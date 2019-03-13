/*
 * Java
 *
 * Copyright 2009-2019 MicroEJ Corp. All rights reserved.
 * This library is provided in source code for use, modification and test, subject to license terms.
 * Any modification of the source code will break MicroEJ Corp. warranties on the whole library.
 */
package ej.hoka.http;

import java.io.InputStream;

import ej.hoka.http.support.MIMEUtils;

/**
 * <p>
 * Default HTTP Session implementation.
 * </p>
 * <p>
 * Retrieves the URI of the request and tries to find a matching resource.
 * </p>
 * <p>
 * Example:
 * </p>
 *
 * <p>
 * Given the URI <code>http://192.168.1.1/my/wonderful/resource.html</code>, the Default HTTP Session will try to find
 * the resource <code>/my/wonderful/resource.html</code> in the application's classpath (using
 * {@link Class#getResourceAsStream(String)}).
 * </p>
 */
public class DefaultHTTPSession extends HTTPSession {

	/**
	 * <p>
	 * Default constructor.
	 * </p>
	 *
	 * @param server
	 *            the {@link HTTPServer} to associate with this session.
	 */
	public DefaultHTTPSession(HTTPServer server) {
		super(server);
	}

	/**
	 * <p>
	 * The generic behaviour of this session implementation is to find a resource matching the given URI in the
	 * classpath. The resource is included in the HTTP Response with the proper MIME-Type and HTTP Status (200 OK).
	 * </p>
	 * <p>
	 * If no resource found, a HTTP 404 error response is returned.
	 * </p>
	 *
	 * @param request
	 *            the {@link HTTPRequest}
	 * @return the {@link HTTPResponse} containing the resource
	 */
	@Override
	public HTTPResponse answer(HTTPRequest request) {
		String uri = request.getURI();

		InputStream resourceStream = getClass().getResourceAsStream(uri);
		if (resourceStream == null) {
			HTTPResponse response = new HTTPResponse();
			response.setStatus(HTTPConstants.HTTP_STATUS_NOTFOUND);
			return response;
		}

		HTTPResponse response = new HTTPResponse(resourceStream);

		// Set content type
		response.setMimeType(MIMEUtils.getMIMEType(uri));

		// Set HTTP status
		response.setStatus(HTTPConstants.HTTP_STATUS_OK); // Status is "200 OK"

		return response;
	}

}
