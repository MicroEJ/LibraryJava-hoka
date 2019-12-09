/*
 * Java
 *
 * Copyright 2018-2019 MicroEJ Corp. All rights reserved.
 * This library is provided in source code for use, modification and test, subject to license terms.
 * Any modification of the source code will break MicroEJ Corp. warranties on the whole library.
 */
package ej.hoka.http.body;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import ej.hoka.http.HTTPConstants;

/**
 * A part of a multipart HTTP request.
 */
public class HTTPPart extends InputStream {

	private static final String APPOSTROPHE = "\""; //$NON-NLS-1$
	private static final String VALUE_ESCAPE = ";\r"; //$NON-NLS-1$
	private static final String KEY_ESCAPE = ":="; //$NON-NLS-1$
	private static final String ESCAPE = '\n' + HTTPConstants.END_OF_LINE;
	private final Map<String, String> headers;
	private final MultiPartBodyParser.MultiPartBuffer multiPart;
	private boolean isFinished;
	private boolean headerParsed;
	private boolean isInit;

	/**
	 * Instantiates a {@link HTTPPart}.
	 *
	 * @param multiPart
	 *            the shared multipart buffer.
	 */
	/* default */ HTTPPart(MultiPartBodyParser.MultiPartBuffer multiPart) {
		this.multiPart = multiPart;
		this.isInit = false;
		this.isFinished = false;
		this.headers = new HashMap<>();
		this.headerParsed = false;
	}

	/**
	 * Gets the headers. The headers are parsed when the method parseHeaders is called.
	 *
	 * @return the headers.
	 */
	public Map<String, String> getHeaders() {
		return this.headers;
	}

	/**
	 * Parse the headers. This method must be called before the first read.
	 *
	 * @return the headers.
	 * @throws IOException
	 *             if an IOException occurs during read.
	 */
	/* default */ synchronized Map<String, String> parseHeaders() throws IOException {
		if (!this.headerParsed) {
			init();
			this.headerParsed = true;
			StringBuilder key = new StringBuilder();
			StringBuilder value = new StringBuilder();
			boolean isKey = true;
			int escapePos = 0;
			int doRead = 0;
			while (escapePos < ESCAPE.length() && this.multiPart.hasData()) {
				doRead = this.multiPart.read();
				if (doRead == ESCAPE.charAt(escapePos)) {
					escapePos++;
				} else {
					escapePos = 0;
					if (isKey) {
						if (KEY_ESCAPE.indexOf(doRead) != -1) {
							isKey = false;
						} else {
							key.append((char) (doRead & 0xFF));
						}
					} else {
						if (VALUE_ESCAPE.indexOf(doRead) != -1) {
							isKey = true;
							String valueString = value.toString().trim();
							if (valueString.startsWith(APPOSTROPHE) && valueString.endsWith(APPOSTROPHE)) {
								valueString = valueString.substring(1, valueString.length() - 1);
							}
							this.headers.put(key.toString().trim(), valueString.trim());
							key = new StringBuilder();
							value = new StringBuilder();
						} else {
							value.append((char) (doRead & 0xFF));
						}
					}
				}
			}
		}
		return this.headers;
	}

	@Override
	public int read() throws IOException {
		int read = -1;
		if (!this.isFinished && this.multiPart.hasData()) {
			init();
			if (this.multiPart.getBoundaryIndex(1) == -1) {
				read = this.multiPart.read();
			} else {
				checkEnd();
			}
		}
		return read;
	}

	@Override
	public int read(byte[] b, int off, int len) throws IOException {
		int read = -1;
		if (!this.isFinished && this.multiPart.hasData()) {
			init();
			read = this.multiPart.read(b, off, len);
			if (read == -1) {
				checkEnd();
			}
		}
		return read;
	}

	private void checkEnd() {
		this.isFinished = true;
	}

	private synchronized void init() throws IOException {
		if (!this.isInit) {
			this.multiPart.skipToBoundary();
			this.isInit = true;
		}
	}

}