/*
 * Java
 *
 * Copyright 2019 MicroEJ Corp. All rights reserved.
 * This library is provided in source code for use, modification and test, subject to license terms.
 * Any modification of the source code will break MicroEJ Corp. warranties on the whole library.
 */
package ej.hoka.http.encoding;

import ej.basictool.map.PackedMap;
import ej.hoka.http.support.AcceptEncoding;
import ej.hoka.http.support.QualityArgument;

/**
 * Class that stores a register of available encoding and transfer coding handlers.
 */
public class HTTPEncodingRegistry {

	private final PackedMap<String, IHTTPEncodingHandler> encodingHandlers;
	private final PackedMap<String, IHTTPTransferCodingHandler> transferCodingHandlers;

	/**
	 * Constructs the {@link HTTPEncodingRegistry} with {@link IdentityEncodingHandler},
	 * {@link IdentityTransferCodingHandler} and {@link ChunkedTransferCodingHandler} registered.
	 */
	public HTTPEncodingRegistry() {
		this.encodingHandlers = new PackedMap<>();

		this.transferCodingHandlers = new PackedMap<>();

		IHTTPTransferCodingHandler chunkedTransferCodingHandler = ChunkedTransferCodingHandler.getInstance();
		this.transferCodingHandlers.put(chunkedTransferCodingHandler.getId(), chunkedTransferCodingHandler);
	}

	/**
	 * Return the {@link IHTTPEncodingHandler} corresponding to identity transfer coding (i.e. no transfer coding).
	 *
	 * @return Return the {@link IHTTPEncodingHandler} corresponding to identity transfer coding (i.e. no transfer
	 *         coding)
	 */
	public IHTTPTransferCodingHandler getIdentityTransferCodingHandler() {
		return IdentityTransferCodingHandler.getInstance();
	}

	/**
	 * Return the {@link IHTTPEncodingHandler} corresponding to chunked transfer coding.
	 *
	 * @return Return the {@link IHTTPEncodingHandler} corresponding to chunked transfer coding.
	 */
	public IHTTPTransferCodingHandler getChunkedTransferCodingHandler() {
		return ChunkedTransferCodingHandler.getInstance();
	}

	/**
	 * Return the {@link IHTTPEncodingHandler} corresponding to the given encoding.
	 *
	 * @param encoding
	 *            case insensitive (See RFC2616, 3.5).
	 * @return null if no handler has been registered to match this encoding.
	 */
	public IHTTPEncodingHandler getEncodingHandler(String encoding) {
		if (encoding == null) {
			return IdentityEncodingHandler.getInstance();
		}

		PackedMap<String, IHTTPEncodingHandler> encodingHandlersMap = this.encodingHandlers;

		for (String key : encodingHandlersMap.keySet()) {
			IHTTPEncodingHandler handler = encodingHandlersMap.get(key);
			if (encoding.equalsIgnoreCase(handler.getId())) {
				return handler;
			}
		}

		return null;
	}

	/**
	 * Return the {@link IHTTPEncodingHandler} corresponding to the given encoding.
	 *
	 * @param encoding
	 *            case insensitive (See RFC2616, 3.5).
	 * @return null if no handler has been registered to match this encoding.
	 */
	public IHTTPTransferCodingHandler getTransferCodingHandler(String encoding) {
		if (encoding == null) {
			return IdentityTransferCodingHandler.getInstance();
		}

		PackedMap<String, IHTTPTransferCodingHandler> transferCodingHandlersMap = this.transferCodingHandlers;

		for (String key : transferCodingHandlersMap.keySet()) {
			IHTTPTransferCodingHandler handler = transferCodingHandlersMap.get(key);
			if (encoding.equalsIgnoreCase(handler.getId())) {
				return handler;
			}
		}

		return null;
	}

	/**
	 * Registers a new HTTP content encoding handler.
	 *
	 * @param handler
	 *            the {@link IHTTPEncodingHandler} to register.
	 */
	public void registerEncodingHandler(IHTTPEncodingHandler handler) {
		this.encodingHandlers.put(handler.getId(), handler);
	}

	/**
	 * Registers a new HTTP transfer coding handler.
	 *
	 * @param handler
	 *            the {@link IHTTPTransferCodingHandler} to register.
	 */
	public void registerTransferCodingHandler(IHTTPTransferCodingHandler handler) {
		this.transferCodingHandlers.put(handler.getId(), handler);
	}

	/**
	 * Returns the most suitable {@link IHTTPEncodingHandler} to match the encodings described in
	 * <code>Accept-Encoding</code> header.
	 *
	 * @param encoding
	 *            is on the form <code>gzip, identity</code> or <code>gzip; q=0.8, identity; q=0.2</code>.
	 * @return the {@link IHTTPEncodingHandler}, or <code>null</code> if no suitable handler can be found.
	 */
	public IHTTPEncodingHandler getAcceptEncodingHandler(String encoding) {
		if (encoding == null) {
			return null;
		}

		AcceptEncoding acceptEncoding = new AcceptEncoding();
		acceptEncoding.parse(encoding);

		// Try to return the most acceptable handler
		QualityArgument[] encodings = acceptEncoding.getEncodings();
		int nbEncodings = encodings.length;
		boolean[] processed = new boolean[nbEncodings];
		for (int pass = nbEncodings - 1; pass >= 0; pass--) { // maximum number of passes
			float localMax = 0;
			int ptrMax = -1;
			for (int i = nbEncodings - 1; i >= 0; i--) {
				if (processed[i]) {
					continue;
				}
				QualityArgument arg = encodings[i];
				float qvalue = arg.getQuality();
				if (qvalue > localMax) {
					localMax = qvalue;
					ptrMax = i;
				}
			}
			processed[ptrMax] = true;

			// Try to get the handler
			IHTTPEncodingHandler handler = getEncodingHandler(encodings[ptrMax].getArgument());
			if (handler != null) {
				return handler;
			}
		}

		return null;
	}

}
