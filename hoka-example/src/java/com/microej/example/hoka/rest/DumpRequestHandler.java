/*
 * Java
 *
 * Copyright 2017-2019 MicroEJ Corp. All rights reserved.
 * For demonstration purpose only.
 * MicroEJ Corp. PROPRIETARY. Use is subject to license terms.
 */
package com.microej.example.hoka.rest;

import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;

import ej.hoka.http.HTTPConstants;
import ej.hoka.http.HTTPRequest;
import ej.hoka.http.HTTPResponse;
import ej.hoka.http.body.StringBodyParser;
import ej.hoka.http.requesthandler.RequestHandler;
import ej.hoka.http.support.MIMEUtils;

public class DumpRequestHandler implements RequestHandler {

	@Override
	public HTTPResponse process(HTTPRequest request, Map<String, String> attributes) {
		StringBuilder responseBuilder = new StringBuilder();
		responseBuilder.append(" ****** HTTP Request ******").append('\n');
		responseBuilder.append(" * URI : ").append(request.getURI()).append('\n');
		responseBuilder.append(" * VERSION : ").append(request.getVersion()).append('\n');
		if (request.getParameters().size() > 0) {
			responseBuilder.append(" * PARAMS : ").append('\n');
			for (Entry<String, String> entry : request.getParameters().entrySet()) {
				responseBuilder.append("      * ").append(entry.getKey()).append(" : ").append(entry.getValue())
				.append('\n');
			}
		}
		if (request.getHeader().size() > 0) {
			responseBuilder.append(" * HEADERS : ").append('\n');
			for (Entry<String, String> entry : request.getHeader().entrySet()) {
				responseBuilder.append("      * ").append(entry.getKey()).append(" : ").append(entry.getValue())
				.append('\n');
			}
		}
		responseBuilder.append(" * BODY : ").append('\n');
		try {
			String body = request.parseBody(new StringBodyParser());
			responseBuilder.append(body).append('\n');
		} catch (IOException e) {
			e.printStackTrace();
		}
		responseBuilder.append(" **************************").append('\n');

		return new HTTPResponse(HTTPConstants.HTTP_STATUS_OK, MIMEUtils.MIME_PLAINTEXT, responseBuilder.toString());
	}
}
