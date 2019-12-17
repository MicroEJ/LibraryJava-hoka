/*
 * Java
 *
 * Copyright 2019 MicroEJ Corp. All rights reserved.
 * For demonstration purpose only.
 * MicroEJ Corp. PROPRIETARY. Use is subject to license terms.
 */
package com.microej.example.hoka.rest.acl;

import java.util.Map;

import ej.authz.acl.AccessControlList;
import ej.hoka.http.HTTPConstants;
import ej.hoka.http.HTTPRequest;
import ej.hoka.http.HTTPResponse;
import ej.hoka.http.support.MIMEUtils;
import ej.hoka.rest.RestEndpoint;

public class HelloEndpoint extends RestEndpoint {

	private final static HTTPResponse ACCESS_FORBIDDEN = HTTPResponse
			.createResponseFromStatus(HTTPConstants.HTTP_STATUS_FORBIDDEN);

	private final AccessControlList acl;

	public HelloEndpoint(String uri, AccessControlList acl) {
		super(uri);

		this.acl = acl;
	}

	@Override
	public HTTPResponse get(HTTPRequest request, Map<String, String> attributes) {
		String username = attributes.get("username");

		if (!acl.isAuthorized(username, "get", this)) {
			return ACCESS_FORBIDDEN;
		}

		HTTPResponse response = new HTTPResponse("Hello " + username + " !");
		response.setMimeType(MIMEUtils.MIME_PLAINTEXT);
		response.setStatus(HTTPConstants.HTTP_STATUS_OK);
		return response;
	}

}
