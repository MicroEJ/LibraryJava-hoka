/*
 * Java
 *
 * Copyright 2019 MicroEJ Corp. All rights reserved.
 * This library is provided in source code for use, modification and test, subject to license terms.
 * Any modification of the source code will break MicroEJ Corp. warranties on the whole library.
 */
package ej.hoka.http.requesthandler;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import ej.hoka.http.HTTPRequest;
import ej.hoka.http.HTTPResponse;

/**
 * <p>
 * A handler that delegates the process of the request to a list of {@link RequestHandler}.
 * </p>
 * <p>
 * The result of the process from an {@link RequestHandlerComposite} is the result of the first {@link RequestHandler}
 * matching the {@link HTTPRequest} in the list.
 * </p>
 */
public class RequestHandlerComposite implements RequestHandler {

	private final List<RequestHandler> requestHandlers;

	/**
	 * Constructs a {@link RequestHandlerComposite} with an empty list.
	 */
	public RequestHandlerComposite() {
		this.requestHandlers = new ArrayList<>(5);
	}

	@Override
	public HTTPResponse process(HTTPRequest request, Map<String, String> attributes) {
		for (RequestHandler handler : this.requestHandlers) {
			HTTPResponse response = handler.process(request, attributes);
			if (response != null) {
				return response;
			}
		}
		return null;
	}

	/**
	 * Adds a {@link RequestHandler} in the list.
	 *
	 * @param handler
	 *            the {@link RequestHandler} to add.
	 */
	public void addRequestHandler(RequestHandler handler) {
		if (handler == null) {
			throw new IllegalArgumentException();
		}
		this.requestHandlers.add(handler);
	}

}
