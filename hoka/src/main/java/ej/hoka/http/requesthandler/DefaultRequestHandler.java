/*
 * Java
 *
 * Copyright 2009-2019 MicroEJ Corp. All rights reserved.
 * This library is provided in source code for use, modification and test, subject to license terms.
 * Any modification of the source code will break MicroEJ Corp. warranties on the whole library.
 */
package ej.hoka.http.requesthandler;

import java.io.InputStream;

import ej.hoka.http.HTTPConstants;
import ej.hoka.http.HTTPRequest;
import ej.hoka.http.HTTPResponse;
import ej.hoka.http.support.MIMEUtils;

/**
 * <p>
 * Default Request Handler implementation.
 * </p>
 * <p>
 * Retrieves the URI of the request and tries to find a matching resource.
 * </p>
 * <p>
 * Example:
 * </p>
 *
 * <p>
 * Given the URI <code>http://192.168.1.1/my/wonderful/resource.html</code>, the Default Request Handler will try to
 * find the resource <code>/my/wonderful/resource.html</code> in the application's classpath (using
 * {@link Class#getResourceAsStream(String)}).
 * </p>
 */
public class DefaultRequestHandler implements RequestHandler {

	/**
	 * <p>
	 * The generic behaviour of this request handler implementation is to find a resource matching the given URI in the
	 * classpath. The resource is included in the HTTP Response with the proper MIME-Type and HTTP Status (200 OK).
	 * </p>
	 *
	 * @param request
	 *            the {@link HTTPRequest}
	 * @return the {@link HTTPResponse} containing the resource
	 */
	@Override
	public HTTPResponse process(HTTPRequest request) {
		String uri = request.getURI();

		InputStream resourceStream = getClass().getResourceAsStream(uri);

		if (resourceStream == null) {
			// Resource not found
			return null;
		}

		HTTPResponse response = new HTTPResponse(resourceStream);

		response.setMimeType(MIMEUtils.getMIMEType(uri));
		response.setStatus(HTTPConstants.HTTP_STATUS_OK);

		return response;
	}

}
