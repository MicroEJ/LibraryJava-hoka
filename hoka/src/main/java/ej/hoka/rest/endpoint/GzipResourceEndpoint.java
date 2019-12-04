/*
 * Java
 *
 * Copyright 2017-2019 MicroEJ Corp. All rights reserved.
 * This library is provided in source code for use, modification and test, subject to license terms.
 * Any modification of the source code will break MicroEJ Corp. warranties on the whole library.
 */
package ej.hoka.rest.endpoint;

import java.io.InputStream;

import ej.hoka.http.HTTPConstants;
import ej.hoka.http.HTTPResponse;
import ej.hoka.http.support.MIMEUtils;

/**
 * A static resource end-point to serve gzip files.
 */
public class GzipResourceEndpoint extends ResourceRestEndpoint {

	/**
	 * Gzip file extension.
	 */
	public static final String GZIP_FILE_EXTENSION = ".gz"; //$NON-NLS-1$

	private static final String CONTENT_ENCODING_GZIP = "gzip"; //$NON-NLS-1$

	/**
	 * Creates a static gzip resource end-point that responds to given URI and serves given resource.
	 *
	 * @param uri
	 *            the end-point URI, cannot be <code>null</code>.
	 * @param resource
	 *            the GZip to serve, cannot be <code>null</code>.
	 */
	public GzipResourceEndpoint(String uri, String resource) {
		super(uri, resource);
	}

	/**
	 * Creates a static gzip resource end-point that responds to given URI and serves given resource.
	 *
	 * @param uri
	 *            the end-point URI, cannot be <code>null</code>.
	 * @param resource
	 *            the GZip to serve, cannot be <code>null</code>.
	 * @param mimetype
	 *            the mime type of the resource, if <code>null</code>, the mimetype will be computed.
	 * @see MIMEUtils#getMIMEType(String)
	 */
	public GzipResourceEndpoint(String uri, String resource, String mimetype) {
		super(uri, resource, mimetype);
	}

	@Override
	protected HTTPResponse getResourceResponse() {
		InputStream resourceAsStream = this.getResourceAsStream();
		if (resourceAsStream == null) {
			return null;
		}

		String mimeType = MIMEUtils
				.getMIMEType(this.resource.substring(0, this.resource.length() - GZIP_FILE_EXTENSION.length()));
		if (mimeType == null) {
			mimeType = MIMEUtils.MIME_DEFAULT_BINARY;
		}

		HTTPResponse response = new HTTPResponse(HTTPConstants.HTTP_STATUS_OK, mimeType, resourceAsStream);
		response.addHeaderField(HTTPConstants.FIELD_CONTENT_ENCODING, CONTENT_ENCODING_GZIP);
		return response;
	}

}
