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
import java.util.Map;

import ej.hoka.http.HTTPConstants;
import ej.hoka.http.HTTPRequest;
import ej.hoka.http.HTTPResponse;
import ej.hoka.http.body.StringBodyParser;
import ej.hoka.http.requesthandler.DefaultRequestHandler;
import ej.hoka.http.support.MIMEUtils;

/*
 * Adding the default resource behaviour for the root of the HTTP server
 */
public class SimpleRequestHandler extends DefaultRequestHandler {

	private static final String DEFAULT_ROOT_RESOURCE = "/html/index.html";

	@Override
	public HTTPResponse process(HTTPRequest request, Map<String, String> attributes) {
		HTTPResponse response = null;
		String uri = request.getURI();

		//when asking for the root of the server
		//serve the "/html/index.html" resource instead

		if (uri.equals("/")) {
			uri = DEFAULT_ROOT_RESOURCE;
		}
		try (InputStream resourceStream = SimpleRequestHandler.class.getResourceAsStream(uri)) {
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
			try {
				System.out.println("Not found " + uri);
				String body = request.parseBody(new StringBodyParser());
				if (body != null && !body.isEmpty()) {
					System.out.println("\tBody: " + body);
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			response = super.process(request, attributes);
		}
		return response;
	}

}
