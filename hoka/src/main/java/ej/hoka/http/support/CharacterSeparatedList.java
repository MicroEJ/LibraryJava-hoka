/*
 * Java
 *
 * Copyright 2009-2019 MicroEJ Corp. All rights reserved.
 * This library is provided in source code for use, modification and test, subject to license terms.
 * Any modification of the source code will break MicroEJ Corp. warranties on the whole library.
 */
package ej.hoka.http.support;

/**
 * Abstract class for implementing parsers for character separated lists.
 */
public abstract class CharacterSeparatedList {

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
	 * Creates a new instance of {@link CharacterSeparatedList} using the character <code>separator</code> as the
	 * separator character.
	 *
	 * @param separator
	 *            the character to treat as characters separator for this list
	 */
	public CharacterSeparatedList(final char separator) {
		this.separator = separator;
	}

	/**
	 * Returns true if the character <code>c</code> is a white space. See RFC HTTP/1.1 RFC2616 2.2.
	 *
	 * @param c
	 *            the character to check
	 * @return true if the character <code>c</code> is a white space.
	 */
	private static boolean isLWS(char c) {
		return (c == SP) || (c == CR) || (c == LF) || (c == HT);
	}

	/**
	 * End instructions of the string parsing.
	 */
	protected void endParse() {
		// nothing to do
	}

	/**
	 * Generates a String representation.
	 *
	 * @param sb
	 *            the {@link StringBuffer}
	 * @return {@link StringBuffer}
	 */
	protected abstract StringBuilder generate(StringBuilder sb);

	/**
	 * Set the number of tokens to read.
	 *
	 * @param nbTokens
	 *            number of tokens
	 */
	protected abstract void initializeNbTokens(int nbTokens);

	/**
	 * Read a new token between the given start and stop index and set its id.
	 *
	 * @param id
	 *            for the token
	 * @param start
	 *            index of the token begin in the string
	 * @param stop
	 *            index of the token end in the string
	 */
	protected abstract void newToken(int id, int start, int stop);

	/**
	 * Parses the given {@link String}.
	 *
	 * @param str
	 *            the {@link String} to parse
	 */
	public void parse(final String str) {
		parse(str, 0, str.length());
	}

	/**
	 * Parses the {@link String} <code>str</code> from the <code>start</code> index to the <code>stop</code> index.
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
			int next = str.indexOf(this.separator, ptr);
			if ((next == -1) || (next > stop)) {
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
			int endEncoding = str.indexOf(this.separator, ptr);
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

}
