/*
 * Java
 *
 * Copyright 2009-2019 MicroEJ Corp. All rights reserved.
 * This library is provided in source code for use, modification and test, subject to license terms.
 * Any modification of the source code will break MicroEJ Corp. warranties on the whole library.
 */
package ej.hoka.http.requesthandler;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import ej.hoka.http.HTTPConstants;
import ej.hoka.http.HTTPRequest;
import ej.hoka.http.HTTPResponse;
import ej.hoka.http.support.MIMEUtils;

/**
 * Resource Request Handler implementation.
 * <p>
 * Retrieves the URI of the request and tries to find a matching resource.
 * <p>
 * Example:
 * <p>
 * Given the URI <code>http://192.168.1.1/my/wonderful/resource.html</code>, the Resource Request Handler, with root
 * directory <code>/my/package/</code> will try to find the resource <code>/my/package/my/wonderful/resource.html</code>
 * in the application's classpath (using {@link Class#getResourceAsStream(String)}).
 */
public class ResourceRequestHandler implements RequestHandler {

	private static final String SLASH = "/"; //$NON-NLS-1$

	private static final String DEFAULT_INDEX = "index.html"; //$NON-NLS-1$

	private final String root;
	private final String index;

	/**
	 * Constructs a resource request handler with given root directory path.
	 * <p>
	 * In case the requested resource is a directory, the <code>"index.html"</code> resource in this directory, if it
	 * exists, is sent.
	 *
	 * @param rootDirectory
	 *            the path of the root directory for resources to serve.
	 */
	public ResourceRequestHandler(String rootDirectory) {
		this(rootDirectory, DEFAULT_INDEX);
	}

	/**
	 * Constructs a resource request handler with given root directory path.
	 * <p>
	 * In case the requested resource is a directory, the <code>index</code> resource in this directory, if it exists,
	 * is sent.
	 *
	 * @param rootDirectory
	 *            the path of the root directory for resources to serve.
	 * @param index
	 *            the directory index file name to serve in case a directory is requested.
	 */
	public ResourceRequestHandler(String rootDirectory, String index) {
		if (rootDirectory.endsWith(SLASH)) {
			rootDirectory = rootDirectory.substring(0, rootDirectory.length() - 1);
		}

		this.root = rootDirectory;
		this.index = index;
	}

	/**
	 * The generic behavior of this request handler implementation is to find a resource matching the given URI in the
	 * classpath. The resource is included in the HTTP Response with the proper MIME-Type and HTTP Status (200 OK).
	 *
	 * @param request
	 *            the {@link HTTPRequest}
	 * @return the {@link HTTPResponse} containing the resource, or <code>null</code> if not found.
	 */
	@Override
	public HTTPResponse process(HTTPRequest request, Map<String, String> attributes) {
		String uri = this.root + request.getURI();

		if (uri.endsWith(SLASH)) {
			uri += this.index;
		}

		InputStream resourceStream = getClass().getResourceAsStream(uri);

		if (resourceStream == null) {
			// Resource not found
			return null;
		}

		HTTPResponse response;
		try {
			response = new HTTPResponse(resourceStream, resourceStream.available());
			// We can assume resourceStream.available() is equal to the length of resourceStream when accessed by
			// Class.getResourceAsStream().
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		response.setMimeType(MIMEUtils.getMIMEType(uri));
		response.setStatus(HTTPConstants.HTTP_STATUS_OK);

		return response;
	}

}
