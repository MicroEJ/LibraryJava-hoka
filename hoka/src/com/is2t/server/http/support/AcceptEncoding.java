/**
 * Java
 *
 * Copyright 2009-2013 IS2T. All rights reserved.
 * IS2T PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.is2t.server.http.support;

/**
 * IS2T-API
 * <p>
 * Utility class for parsing Accept Encoding header in HTTP requests.<br>
 * <i>See: (RFC HTTP/1.1 RFC2616 14.3 Accept Encoding)</i>
 * </p>
 */
public class AcceptEncoding extends CharacterSeparatedList {
	/**
	 * IS2T-API
	 * <p>
	 * Creates a new instance of {@link AcceptEncoding} with a default separator
	 * (comma character ",").
	 * </p>
	 */
	public AcceptEncoding() {
		super(',');
	}

	/**
	 * IS2T-API
	 * <p>
	 * Not implemented.
	 * </p>
	 * 
	 * @param sb
	 *            the {@link StringBuffer}
	 * @return throws a {@link RuntimeException} to signal that
	 *         the function is not implemented.
	 */
	public StringBuffer generate(StringBuffer sb) {
		throw new RuntimeException(); // TODO if necessary
	}

	/**
	 * IS2T-API
	 * <p>
	 * Returns the previously parsed array of {@link QualityArgument}s.
	 * </p>
	 * 
	 * @return the encodings previously parsed.
	 */
	public QualityArgument[] getEncodings() {
		return encodings;
	}

	/**
	 * IS2T-API
	 * <p>
	 * Sets the size of the array for storing parsed {@link QualityArgument}s.
	 * </p>
	 * 
	 * @param nbTokens
	 *            the size of the array for storing the parsed {@link QualityArgument}s.
	 */
	public void initializeNbTokens(int nbTokens) {
		encodings = new QualityArgument[nbTokens];
	}

	/**
	 * IS2T-API
	 * <p>
	 * Parses the <code>index</code>th {@link QualityArgument} from the index
	 * <code>start</code> to the index <code>stop</code>.
	 * </p>
	 * 
	 * @param index
	 *            the index of the {@link QualityArgument} to be parsed
	 * @param start
	 *            the start index in the current string to parse from
	 * @param stop
	 *            the end index in the current string to parse from
	 */
	public void newToken(int index, int start, int stop) {
		QualityArgument qualityArgument = encodings[index] = new QualityArgument();
		qualityArgument.parse(currentString, start, stop);
	}

	/*************************************************************************
	 * NOT IN API
	 ************************************************************************/

	/**
	 * array of {@link QualityArgument}s.
	 */
	protected QualityArgument[] encodings;
}
