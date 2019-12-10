/*
 * Java
 *
 * Copyright 2018-2019 MicroEJ Corp. All rights reserved.
 * This library is provided in source code for use, modification and test, subject to license terms.
 * Any modification of the source code will break MicroEJ Corp. warranties on the whole library.
 */
package ej.hoka.rest.endpoint;

import java.util.Map;

import ej.hoka.http.HTTPRequest;
import ej.hoka.http.HTTPResponse;
import ej.hoka.rest.RestEndpoint;

/**
 * An endpoint forwarding its requests to another {@link RestEndpoint}.
 */
public class AliasEndpoint extends RestEndpoint {

	private RestEndpoint endpoint;

	/**
	 * Instantiates a {@link AliasEndpoint}.
	 *
	 * @param uri
	 *            the uri, cannot be <code>null</code>.
	 * @param endpoint
	 *            the end point to forward the request to, cannot be <code>null</code>.
	 * @throws IllegalArgumentException
	 *             if URI is empty
	 */
	public AliasEndpoint(String uri, RestEndpoint endpoint) {
		super(uri);
		if (endpoint == null) {
			throw new NullPointerException();
		}
		this.endpoint = endpoint;
	}

	/**
	 * Gets the endpoint.
	 *
	 * @return the endpoint.
	 */
	public RestEndpoint getEndpoint() {
		return this.endpoint;
	}

	/**
	 * Sets the endpoint.
	 *
	 * @param endpoint
	 *            the endpoint to set, cannot be <code>null</code>.
	 */
	public void setEndpoint(RestEndpoint endpoint) {
		if (endpoint == null) {
			throw new NullPointerException();
		}
		this.endpoint = endpoint;
	}

	@Override
	public HTTPResponse get(HTTPRequest request, Map<String, String> attributes) {
		return this.endpoint.get(request, attributes);
	}

	@Override
	public HTTPResponse post(HTTPRequest request, Map<String, String> attributes) {
		return this.endpoint.post(request, attributes);
	}

	@Override
	public HTTPResponse put(HTTPRequest request, Map<String, String> attributes) {
		return this.endpoint.put(request, attributes);
	}

	@Override
	public HTTPResponse delete(HTTPRequest request, Map<String, String> attributes) {
		return this.endpoint.delete(request, attributes);
	}

}
