/*
 * Java
 *
 * Copyright 2019 MicroEJ Corp. All rights reserved.
 * This library is provided in source code for use, modification and test, subject to license terms.
 * Any modification of the source code will break MicroEJ Corp. warranties on the whole library.
 */
package ej.hoka.http;

import java.util.ArrayList;
import java.util.List;

public class RequestHandlerComposite implements RequestHandler {

	private final List<RequestHandler> requestHandlers;

	public RequestHandlerComposite() {
		this.requestHandlers = new ArrayList<>(5);
	}

	@Override
	public HTTPResponse process(HTTPRequest request) {
		for (RequestHandler handler : this.requestHandlers) {
			HTTPResponse response = handler.process(request);
			if (response != null) {
				return response;
			}
		}
		return null;
	}

	public void addChild(RequestHandler handler) {
		if (handler == null) {
			throw new IllegalArgumentException();
		}
		this.requestHandlers.add(handler);
	}

}
