/*
 * Java
 *
 * Copyright 2015-2019 MicroEJ Corp. All rights reserved.
 * For demonstration purpose only.
 * MicroEJ Corp. PROPRIETARY. Use is subject to license terms.
 */
package com.microej.example.hoka;

import java.io.IOException;
import java.io.InputStream;

import ej.hoka.http.DefaultHTTPSession;
import ej.hoka.http.HTTPConstants;
import ej.hoka.http.HTTPRequest;
import ej.hoka.http.HTTPResponse;
import ej.hoka.http.HTTPServer;
import ej.hoka.http.HTTPSession;
import ej.hoka.http.body.BodyParser;
import ej.hoka.http.body.StringBodyParser;
import ej.hoka.http.support.MIMEUtils;

/*
 * Adding the default resource behaviour for the root of the HTTP server
 */
public class SimpleHTTPSession extends DefaultHTTPSession {

	private static final String DEFAULT_ROOT_RESOURCE = "/html/index.html";

	public SimpleHTTPSession(HTTPServer server) {
		super(server);
	}

	@Override
	public HTTPResponse answer(HTTPRequest request) {
		HTTPResponse response = null;
		String uri = request.getURI();

		//when asking for the root of the server
		//serve the "/html/index.html" resource instead

		if (uri.equals("/")) {
			uri = DEFAULT_ROOT_RESOURCE;
		}
		try (InputStream resourceStream = SimpleHTTPSession.class.getResourceAsStream(uri)) {
			if (resourceStream != null) {
				response = new HTTPResponse(resourceStream);

				// Set content type
				response.setMimeType(MIMEUtils.getMIMEType(uri));

				// Set HTTP status
				response.setStatus(HTTPConstants.HTTP_STATUS_OK); // Status is "200 OK"
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (response == null) {
			System.out.println("Not found " + uri);
			BodyParser bodyParser = request.getBodyParser();
			if (bodyParser instanceof StringBodyParser) {
				String body = ((StringBodyParser) bodyParser).getBody();
				if (body != null && !body.isEmpty()) {
					System.out.println("\tBody: " + body);
				}
			}

			response = super.answer(request);
		}
		return response;
	}

	public static class Factory implements HTTPSession.Factory {
		@Override
		public HTTPSession newHTTPSession(HTTPServer server) {
			return new SimpleHTTPSession(server);
		}
	}

}
