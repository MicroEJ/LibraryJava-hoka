/*
 * Java
 *
 * Copyright 2019 MicroEJ Corp. All rights reserved.
 * This library is provided in source code for use, modification and test, subject to license terms.
 * Any modification of the source code will break MicroEJ Corp. warranties on the whole library.
 */
package ej.hoka.http.requesthandler;

import java.util.Map;

import ej.basictool.ArrayTools;
import ej.hoka.http.HTTPRequest;
import ej.hoka.http.HTTPResponse;

/**
 * A handler that delegates the process of the request to an array of {@link RequestHandler}.
 * <p>
 * The result of the process from a {@link RequestHandlerComposite} is the result of the first {@link RequestHandler}
 * matching the {@link HTTPRequest} in the array. The handlers are browsed in the order they have been added.
 */
public class RequestHandlerComposite implements RequestHandler {

	private RequestHandler[] requestHandlers;

	/**
	 * Constructs a {@link RequestHandlerComposite} with an empty list.
	 */
	public RequestHandlerComposite() {
		this.requestHandlers = new RequestHandler[0];
	}

	@Override
	public HTTPResponse process(HTTPRequest request, Map<String, String> attributes) {
		RequestHandler[] requestHandlersArray = this.requestHandlers;
		for (RequestHandler handler : requestHandlersArray) {
			HTTPResponse response = handler.process(request, attributes);
			if (response != null) {
				return response;
			}
		}
		return null;
	}

	/**
	 * Adds a {@link RequestHandler} in the list.
	 * <p>
	 * The handlers are browsed in the order they have been added.
	 *
	 * @param handler
	 *            the {@link RequestHandler} to add.
	 */
	public final synchronized void addRequestHandler(RequestHandler handler) {
		if (handler == null) {
			throw new IllegalArgumentException();
		}
		this.requestHandlers = ArrayTools.add(this.requestHandlers, handler);
	}

}
