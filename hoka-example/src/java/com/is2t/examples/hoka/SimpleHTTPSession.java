/*
* Java
*
* Copyright 2015-2018 IS2T. All rights reserved.
* For demonstration purpose only.
* IS2T PROPRIETARY. Use is subject to license terms.
*/
package com.is2t.examples.hoka;

import java.io.InputStream;

import ej.hoka.http.DefaultHTTPSession;
import ej.hoka.http.HTTPConstants;
import ej.hoka.http.HTTPRequest;
import ej.hoka.http.HTTPResponse;
import ej.hoka.http.HTTPServer;
import ej.hoka.http.support.MIMEUtils;

/*
 * Adding the default resource behaviour for the root of the HTTP server 
 */
public class SimpleHTTPSession extends DefaultHTTPSession {
	
	private static final String DEFAULT_ROOT_RESOURCE = "/html/index.html";

	public SimpleHTTPSession(HTTPServer server) {
		super(server);
	}
	
	public HTTPResponse answer(HTTPRequest request) {
		
		String uri = request.getURI();

		//when asking for the root of the server
		//serve the "/html/index.html" resource instead
		if(uri.equals("/")){
			
			uri = DEFAULT_ROOT_RESOURCE;
			
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
			
		} else {
			return super.answer(request);
		}
		
	}

}
