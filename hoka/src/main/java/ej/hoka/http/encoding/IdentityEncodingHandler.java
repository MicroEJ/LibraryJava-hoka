/*
 * Java
 *
 * Copyright 2009-2019 MicroEJ Corp. All rights reserved.
 * This library is provided in source code for use, modification and test, subject to license terms.
 * Any modification of the source code will break MicroEJ Corp. warranties on the whole library.
 */
package ej.hoka.http.encoding;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * HTTP-1.1 Identity encoding handler.
 */
public final class IdentityEncodingHandler implements IHTTPEncodingHandler {

	/**
	 * instance to use in factory method.
	 */
	private static IdentityEncodingHandler instance;

	/**
	 * Private constructor to avoid direct instantiation.
	 */
	private IdentityEncodingHandler() {
		// private constructor, because of singleton behaviour
	}

	/**
	 * Returns an instance of {@link IdentityEncodingHandler}.
	 *
	 * @return an instance of {@link IdentityEncodingHandler}.
	 */
	public static IdentityEncodingHandler getInstance() {
		if (instance == null) {
			instance = new IdentityEncodingHandler();
		}
		return instance;
	}

	/**
	 * Returns the internal ID of the {@link IdentityEncodingHandler}.
	 *
	 * @return the string "identity".
	 */
	@Override
	public String getId() {
		return "identity"; //$NON-NLS-1$
	}

	/**
	 * Returns the <code>original</code> {@link InputStream}.
	 *
	 * @param original
	 *            the {@link InputStream} to return.
	 * @return the <code>original</code> {@link InputStream}.
	 */
	@Override
	public InputStream open(final InputStream original) {
		return original;
	}

	/**
	 * Returns the <code>original</code> {@link OutputStream}.
	 *
	 * @param original
	 *            the {@link OutputStream} to return.
	 * @return the <code>original</code> {@link OutputStream}.
	 */
	@Override
	public OutputStream open(final OutputStream original) {
		return original;
	}

}
