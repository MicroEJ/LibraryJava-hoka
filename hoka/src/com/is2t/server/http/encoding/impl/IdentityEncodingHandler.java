/**
 * Java
 *
 * Copyright 2009-2015 IS2T. All rights reserved.
 * IS2T PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.is2t.server.http.encoding.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.is2t.server.http.encoding.IHTTPEncodingHandler;

/**
 * IS2T-API
 * <p>
 * HTTP-1.1 Identity encoding handler.
 * </p>
 */
public final class IdentityEncodingHandler implements IHTTPEncodingHandler {

	/**
	 * IS2T-API
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
	 * IS2T-API
	 * <p>
	 * Returns the internal ID of the {@link IdentityEncodingHandler}.
	 * </p>
	 * 
	 * @return the string "identity".
	 */
	public String getId() {
		return "identity"; // $NON-NLS
	}

	/**
	 * IS2T-API
	 * <p>
	 * Returns the <code>original</code> {@link InputStream}.
	 * </p>
	 * 
	 * @param original
	 *            the {@link InputStream} to return
	 * @return the <code>original</code> {@link InputStream}
	 * @throws IOException not thrown
	 */
	public InputStream open(final InputStream original) throws IOException {
		return original;
	}

	/**
	 * IS2T-API
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
	public OutputStream open(final OutputStream original) throws IOException {
		return original;
	}

	/*************************************************************************
	 * NOT IN API
	 ************************************************************************/

	/**
	 * Instance to use in factory method.
	 */
	private static IdentityEncodingHandler Instance;

	/**
	 * Private constructor to avoid direct instantiation.
	 */
	private IdentityEncodingHandler() {
		// private constructor, because of singleton behaviour
	}

}
