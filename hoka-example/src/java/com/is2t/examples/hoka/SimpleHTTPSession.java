/*
* Java
*
* Copyright 2015 IS2T. All rights reserved.
* Use of this source code is governed by a BSD-style license that can be found at http://www.is2t.com/open-source-bsd-license/.
*/
package com.is2t.examples.hoka;

import java.io.InputStream;

import com.is2t.server.http.DefaultHTTPSession;
import com.is2t.server.http.HTTPConstants;
import com.is2t.server.http.HTTPRequest;
import com.is2t.server.http.HTTPResponse;
import com.is2t.server.http.HTTPServer;
import com.is2t.server.http.support.MIMEUtils;

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
