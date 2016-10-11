/*
 * Java
 *
 * Copyright 2009-2016 IS2T. All rights reserved.
 * IS2T PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package ej.hoka.http;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * <p>
 * HTTP-1.1 Identity encoding handler.
 * </p>
 */
public final class IdentityEncodingHandler implements IHTTPEncodingHandler {

	/**
	 * Instance to use in factory method.
	 */
	private static IdentityEncodingHandler Instance;

	/**
	 * <p>
	 * Returns an instance of {@link IdentityEncodingHandler}.
	 * </p>
	 *
	 * @return an instance of {@link IdentityEncodingHandler}
	 */
	public static IdentityEncodingHandler getInstance() {
		if (Instance == null) {
			Instance = new IdentityEncodingHandler();
		}
		return Instance;
	}

	/**
	 * Private constructor to avoid direct instantiation.
	 */
	private IdentityEncodingHandler() {
		// private constructor, because of singleton behaviour
	}

	/**
	 * <p>
	 * Returns the internal ID of the {@link IdentityEncodingHandler}.
	 * </p>
	 *
	 * @return the string "identity".
	 */
	@Override
	public String getId() {
		return "identity"; //$NON-NLS-1$
	}

	/**
	 * <p>
	 * Returns the <code>original</code> {@link InputStream}.
	 * </p>
	 *
	 * @param original
	 *            the {@link InputStream} to return
	 * @return the <code>original</code> {@link InputStream}
	 * @throws IOException
	 *             not thrown
	 */
	@Override
	public InputStream open(final InputStream original) throws IOException {
		return original;
	}

	/**
	 * <p>
	 * Returns the <code>original</code> {@link OutputStream}.
	 * </p>
	 *
	 * @param original
	 *            the {@link OutputStream} to return
	 * @return the <code>original</code> {@link OutputStream}
	 * @throws IOException
	 *             not thrown
	 */
	@Override
	public OutputStream open(final OutputStream original) throws IOException {
		return original;
	}

}
