/**
 * Java
 *
 * Copyright 2009-2015 IS2T. All rights reserved.
 * IS2T PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.is2t.server.http.support;

/**
 * IS2T-API
 * <p>
 * Abstract class for implementing parsers for character separated lists.
 * </p>
 */
public abstract class CharacterSeparatedList {

	/**
	 * IS2T-API
	 * <p>
	 * Creates a new instance of {@link CharacterSeparatedList} using the
	 * character <code>separator</code> as the separator character.
	 * </p>
	 * 
	 * @param separator
	 *            the character to treat as characters separator for this list
	 */
	public CharacterSeparatedList(final char separator) {
		this.separator = separator;
	}

	/**
	 * IS2T-API
	 * <p>
	 * Parses the given {@link String}.
	 * </p>
	 * 
	 * @param str
	 *            the {@link String} to parse
	 */
	public void parse(final String str) {
		parse(str, 0, str.length());
	}

	/**
	 * IS2T-API
	 * <p>
	 * Parses the {@link String} <code>str</code> from the <code>start</code> index to the
	 * <code>stop</code> index.
	 * </p>
	 * 
	 * @param str
	 *            the {@link String} to parse
	 * @param start
	 *            the start index
	 * @param stop
	 *            the stop index
	 */
	public void parse(final String str, final int start, final int stop) {
		this.currentString = str;

		// 1) Get the number of declared comma separated tokens
		int nbTokens = 1; // at least one encoding
		int ptr = start;
		while (true) {
			int next = str.indexOf(separator, ptr);
			if (next == -1 || next > stop) {
				break;
			}
			++nbTokens;
			ptr = next + 1;
		}
		initializeNbTokens(nbTokens);

		// 2) extract tokens
		ptr = start;
		for (int i = -1; ++i < nbTokens;) {
			// find the first occurence of the separator
			int endEncoding = str.indexOf(separator, ptr);
			// if the separator is not found, the end index is the initial end
			// index
			if (endEncoding == -1) {
				endEncoding = stop;
			}
			// does not allow the end index to be after the initial end index
			if (endEncoding > stop) {
				endEncoding = stop;
			}
			// trim LWS
			int tokenStart = ptr;
			while (isLWS(str.charAt(tokenStart))) {
				++tokenStart;
			}

			int tokenStop = endEncoding;
			while (isLWS(str.charAt(tokenStop - 1))) {
				--tokenStop;
			}

			newToken(i, tokenStart, tokenStop);
			ptr = endEncoding + 1;
		}

		endParse();
	}

	/*************************************************************************
	 * NOT IN API
	 ************************************************************************/

	/**
	 * Carriage Return character.
	 */
	protected static final char CR = 13;

	/**
	 * Horizontal TAB character.
	 */
	protected static final char HT = 9;

	/**
	 * Line feed character.
	 */
	protected static final char LF = 10;

	/**
	 * Space character.
	 */
	protected static final char SP = 32;

	/**
	 * Current string being parsed.
	 */
	protected String currentString;

	/**
	 * Characters separator.
	 */
	protected char separator;

	/**
	 * Returns true if the character <code>c</code> is a white space. See RFC
	 * HTTP/1.1 RFC2616 2.2.
	 * 
	 * @param c
	 *            the character to check
	 * @return true if the character <code>c</code> is a white space.
	 */
	private static boolean isLWS(char c) {
		return c == SP || c == CR || c == LF || c == HT;
	}

	/**
	 * <p>
	 * End instructions of the string parsing.
	 * </p>
	 */
	protected void endParse() {
	}

	/**
	 * <p>
	 * Generates a String representation.
	 * </p>
	 * 
	 * @param sb
	 *            the {@link StringBuffer}
	 * @return {@link StringBuffer}
	 */
	protected abstract StringBuffer generate(StringBuffer sb);

	/**
	 * <p>
	 * Set the number of tokens to read.
	 * </p>
	 * 
	 * @param nbTokens
	 *            number of tokens
	 */
	protected abstract void initializeNbTokens(int nbTokens);

	/**
	 * <p>
	 * Read a new token between the given start and stop index and set its id.
	 * </p>
	 * 
	 * @param id
	 *            for the token
	 * @param start
	 *            index of the token begin in the string
	 * @param stop
	 *            index of the token end in the string
	 */
	protected abstract void newToken(int id, int start, int stop);

}
