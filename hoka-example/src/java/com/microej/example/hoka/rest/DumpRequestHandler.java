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

import ej.hoka.http.HTTPRequest;
import ej.hoka.http.HTTPResponse;
import ej.hoka.http.body.StringBodyParser;
import ej.hoka.http.requesthandler.RequestHandler;

public class DumpRequestHandler implements RequestHandler {

	@Override
	public HTTPResponse process(HTTPRequest request, Map<String, String> attributes) {
		dumpMessage(request);
		return HTTPResponse.RESPONSE_OK;
	}

	private void dumpMessage(HTTPRequest request) {
		System.out.println(" ****** HTTP Request ******");
		System.out.println(" * URI : " + request.getURI());
		System.out.println(" * VERSION : " + request.getVersion());
		if (request.getParameters().size() > 0) {
			System.out.println(" * PARAMS : ");
			for (Entry<String, String> entry : request.getParameters().entrySet()) {
				System.out.println("      * " + entry.getKey() + " : " + entry.getValue());
			}
		}
		if (request.getHeader().size() > 0) {
			System.out.println(" * HEADERS : ");
			for (Entry<String, String> entry : request.getHeader().entrySet()) {
				System.out.println("      * " + entry.getKey() + " : " + entry.getValue());
			}
		}
		System.out.println(" * BODY : ");
		try {
			String body = request.parseBody(new StringBodyParser());
			System.out.println(body);
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println(" ****** ");
	}
}
