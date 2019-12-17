/*
 * Java
 *
 * Copyright 2019 MicroEJ Corp. All rights reserved.
 * For demonstration purpose only.
 * MicroEJ Corp. PROPRIETARY. Use is subject to license terms.
 */
package com.microej.example.hoka.slownetwork;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;

import ej.hoka.http.HTTPConstants;
import ej.hoka.http.HTTPRequest;
import ej.hoka.http.HTTPResponse;
import ej.hoka.http.requesthandler.RequestHandler;
import ej.hoka.http.support.MIMEUtils;
import ej.hoka.log.Messages;
import ej.util.message.Level;

public class FileSystemView implements RequestHandler {

	private static final String SLASH = "/"; //$NON-NLS-1$

	private static final String DEFAULT_INDEX = "index.html"; //$NON-NLS-1$

	private static final String DIRECTORY_TRAVERSAL_SEQUENCE = ".."; //$NON-NLS-1$

	private final String uri;
	private final File directory;
	private final String index;

	public FileSystemView(String rootURI, String rootPath) throws IOException {
		this.uri = rootURI;

		File rootFile = new File(rootPath);
		if (!rootFile.exists() || !rootFile.isDirectory()) {
			throw new IllegalArgumentException();
		}
		this.directory = rootFile;

		this.index = DEFAULT_INDEX;
	}

	@Override
	public HTTPResponse process(HTTPRequest request, Map<String, String> attributes) {
		String path = request.getURI();

		if (!path.startsWith(this.uri)) {
			return null;
		}

		if (path.contains(DIRECTORY_TRAVERSAL_SEQUENCE)) {
			// For security reasons, do not handle request to URI with a directory traversal
			// sequence.
			Messages.LOGGER.log(Level.INFO, Messages.CATEGORY_HOKA, Messages.DIRECTORY_TRAVERSAL_URI);
			return null;
		}

		if (path.endsWith(SLASH)) {
			path += this.index;
		}

		File file = null;

		// Try to find the compressed version of the requested resource if the browser
		// supports it.
		if (request.getHeaderField(HTTPConstants.FIELD_ACCEPT_ENCODING).contains("gzip")) {
			file = new File(directory, path.substring(this.uri.length()) + ".gz");

			if (!file.exists() || !file.isFile()) {
				file = null;
			}
		}

		boolean isCompressed = file != null;

		if (!isCompressed) {
			file = new File(directory, path.substring(this.uri.length()));

			if (!file.exists() || !file.isFile()) {
				return null;
			}
		}

		HTTPResponse response;
		try {
			response = new HTTPResponse(HTTPConstants.HTTP_STATUS_OK, MIMEUtils.getMIMEType(path),
					new FileInputStream(file)); // The file connection is closed by the HTTPResponse.
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		}

		if (isCompressed) {
			response.addHeaderField(HTTPConstants.FIELD_CONTENT_ENCODING, "gzip");
		}

		return response;
	}

}
