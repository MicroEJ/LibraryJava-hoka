/*
 * Java
 *
 * Copyright 2019 MicroEJ Corp. All rights reserved.
 * This library is provided in source code for use, modification and test, subject to license terms.
 * Any modification of the source code will break MicroEJ Corp. warranties on the whole library.
 */
package ej.hoka.http;

import java.util.HashMap;
import java.util.Map;

public class HTTPEncodingRegister {

	private final Map<String, IHTTPEncodingHandler> encodingHandlers;
	private final Map<String, IHTTPTransferCodingHandler> transferCodingHandlers;

	public HTTPEncodingRegister() {
		this.encodingHandlers = new HashMap<>(0);

		this.transferCodingHandlers = new HashMap<>(1);

		IHTTPTransferCodingHandler chunkedTransferCodingHandler = ChunkedTransferCodingHandler.getInstance();
		this.transferCodingHandlers.put(chunkedTransferCodingHandler.getId(), chunkedTransferCodingHandler);
	}

	/**
	 * Return the {@link IHTTPEncodingHandler} corresponding to identity transfer coding (i.e. no transfer coding)
	 *
	 * @return Return the {@link IHTTPEncodingHandler} corresponding to identity transfer coding (i.e. no transfer
	 *         coding)
	 */
	protected IHTTPTransferCodingHandler getIdentityTransferCodingHandler() {
		return IdentityTransferCodingHandler.getInstance();
	}

	/**
	 * Return the {@link IHTTPEncodingHandler} corresponding to chunked transfer coding.
	 *
	 * @return Return the {@link IHTTPEncodingHandler} corresponding to chunked transfer coding
	 */
	protected IHTTPTransferCodingHandler getChunkedTransferCodingHandler() {
		return ChunkedTransferCodingHandler.getInstance();
	}

	/**
	 * Return the {@link IHTTPEncodingHandler} corresponding to the given encoding.
	 *
	 * @param encoding
	 *            case insensitive (See RFC2616, 3.5)
	 * @return null if no handler has been registered to match this encoding
	 */
	protected IHTTPEncodingHandler getEncodingHandler(String encoding) {
		if (encoding == null) {
			return IdentityEncodingHandler.getInstance();
		}
		for (Map.Entry<String, IHTTPEncodingHandler> entry : this.encodingHandlers.entrySet()) {
			IHTTPEncodingHandler handler = entry.getValue();
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
	 *            case insensitive (See RFC2616, 3.5)
	 * @return null if no handler has been registered to match this encoding
	 */
	protected IHTTPTransferCodingHandler getTransferCodingHandler(String encoding) {
		if (encoding == null) {
			return IdentityTransferCodingHandler.getInstance();
		}
		for (Map.Entry<String, IHTTPTransferCodingHandler> entry : this.transferCodingHandlers.entrySet()) {
			IHTTPTransferCodingHandler handler = entry.getValue();
			if (encoding.equalsIgnoreCase(handler.getId())) {
				return handler;
			}
		}
		return null;
	}

	/**
	 * <p>
	 * Registers a new HTTP content encoding handler.
	 * </p>
	 *
	 * @param handler
	 *            the {@link IHTTPEncodingHandler} to register
	 */
	public void registerEncodingHandler(IHTTPEncodingHandler handler) {
		this.encodingHandlers.put(handler.getId(), handler);
	}

	/**
	 * <p>
	 * Registers a new HTTP transfer coding handler.
	 * </p>
	 *
	 * @param handler
	 *            the {@link IHTTPTransferCodingHandler} to register
	 */
	public void registerTransferCodingHandler(IHTTPTransferCodingHandler handler) {
		this.transferCodingHandlers.put(handler.getId(), handler);
	}
}
