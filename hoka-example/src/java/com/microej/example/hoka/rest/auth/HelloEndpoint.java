/*
 * Java
 *
 * Copyright 2019 MicroEJ Corp. All rights reserved.
 * For demonstration purpose only.
 * MicroEJ Corp. PROPRIETARY. Use is subject to license terms.
 */
package com.microej.example.hoka.rest.auth;

import java.util.Map;

import ej.hoka.http.HTTPConstants;
import ej.hoka.http.HTTPRequest;
import ej.hoka.http.HTTPResponse;
import ej.hoka.http.support.MIMEUtils;
import ej.hoka.rest.RestEndpoint;

public class HelloEndpoint extends RestEndpoint {

	public HelloEndpoint(String uri) {
		super(uri);
	}

	@Override
	public HTTPResponse get(HTTPRequest request, Map<String, String> attributes) {
		HTTPResponse response = new HTTPResponse("Hello " + attributes.get("username") + " !");
		response.setMimeType(MIMEUtils.MIME_PLAINTEXT);
		response.setStatus(HTTPConstants.HTTP_STATUS_OK);
		return response;
	}

}
