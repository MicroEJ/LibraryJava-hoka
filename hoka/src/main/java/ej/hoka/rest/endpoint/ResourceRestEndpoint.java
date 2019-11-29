/*
 * Java
 *
 * Copyright 2017-2019 MicroEJ Corp. All rights reserved.
 * This library is provided in source code for use, modification and test, subject to license terms.
 * Any modification of the source code will break MicroEJ Corp. warranties on the whole library.
 */
package ej.hoka.rest.endpoint;

import java.io.InputStream;
import java.util.Map;

import ej.hoka.http.HTTPConstants;
import ej.hoka.http.HTTPRequest;
import ej.hoka.http.HTTPResponse;
import ej.hoka.http.support.MIMEUtils;
import ej.hoka.rest.RestEndpoint;

/**
 * A static resource end-point to serve all kind of files.
 */
public class ResourceRestEndpoint extends RestEndpoint {

	private String mimetype;
	/**
	 * Path to embedded resource to serve.
	 */
	protected String resource;

	/**
	 * Creates a static resource end-point that responds to given URI and serves given resource.
	 *
	 * @param uri
	 *            the end-point URI, cannot be <code>null</code>.
	 * @param resource
	 *            the resource to serve, cannot be <code>null</code>.
	 */
	public ResourceRestEndpoint(String uri, String resource) {
		this(uri, resource, null);
	}

	/**
	 * Creates a static resource end-point that responds to given URI and serves given resource.
	 *
	 * @param uri
	 *            the end-point URI, cannot be <code>null</code>.
	 * @param resource
	 *            the resource to serve, cannot be <code>null</code>.
	 * @param mimetype
	 *            the mime type of the resource, if <code>null</code>, the mimetype will be computed.
	 * @see MIMEUtils#getMIMEType(String)
	 */
	public ResourceRestEndpoint(String uri, String resource, String mimetype) {
		super(uri);
		this.mimetype = mimetype;
		if (resource == null) {
			throw new NullPointerException();
		}
		this.resource = resource;
	}

	/**
	 * Gets the resource to serve as an input stream.
	 *
	 * @return an input stream on the resource to serve, or {@code null} if resource is not found.
	 */
	protected InputStream getResourceAsStream() {
		return this.getClass().getResourceAsStream(this.resource);
	}

	/**
	 * Gets the resource to serve as an HTTP response. By default, it serves embedded resource with
	 * {@code application/octet-stream} content type.
	 *
	 * @return the HTTP response corresponding to the resource to serve.
	 *
	 * @see MIMEUtils#MIME_DEFAULT_BINARY
	 */
	protected HTTPResponse getResourceResponse() {
		InputStream resourceAsStream = this.getResourceAsStream();
		if (resourceAsStream == null) {
			return HTTPResponse.RESPONSE_NOT_FOUND;
		}

		String mimeType = this.mimetype;
		if (mimeType == null) {
			mimeType = MIMEUtils.getMIMEType(this.resource);
			if (mimeType == null) {
				mimeType = MIMEUtils.MIME_DEFAULT_BINARY;
			}
		}

		return new HTTPResponse(HTTPConstants.HTTP_STATUS_OK, mimeType, resourceAsStream);
	}

	@Override
	public HTTPResponse get(HTTPRequest request, Map<String, String> attributes) {
		return this.getResourceResponse();
	}

	/**
	 * Gets the mimetype.
	 *
	 * @return the mimetype, can be <code>null</code>.
	 */
	public String getMimetype() {
		return this.mimetype;
	}

	/**
	 * Sets the mimetype.
	 *
	 * @param mimetype
	 *            the mimetype to set, if <code>null</code>, the type will be computed.
	 * @see MIMEUtils#getMIMEType(String)
	 */
	public void setMimetype(String mimetype) {
		this.mimetype = mimetype;
	}

}
