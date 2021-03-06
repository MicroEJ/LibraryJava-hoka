/*
 * Java
 *
 * Copyright 2009-2019 MicroEJ Corp. All rights reserved.
 * This library is provided in source code for use, modification and test, subject to license terms.
 * Any modification of the source code will break MicroEJ Corp. warranties on the whole library.
 */
package ej.hoka.http.support;

/**
 * Utility class for parsing Accept Encoding header in HTTP requests.<br>
 * <i>See: (RFC HTTP/1.1 RFC2616 14.3 Accept Encoding)</i>
 */
public class AcceptEncoding extends CharacterSeparatedList {

	/**
	 * Array of {@link QualityArgument}s.
	 */
	protected QualityArgument[] encodings;

	/**
	 * Creates a new instance of {@link AcceptEncoding} with a default separator (comma character ",").
	 */
	public AcceptEncoding() {
		super(',');
	}

	/**
	 * Not implemented.
	 *
	 * @param sb
	 *            the {@link StringBuffer}
	 * @return throws a {@link RuntimeException} to signal that the function is not implemented.
	 */
	@Override
	public StringBuilder generate(StringBuilder sb) {
		throw new RuntimeException();
	}

	/**
	 * Returns the previously parsed array of {@link QualityArgument}s.
	 *
	 * @return the encodings previously parsed.
	 */
	public QualityArgument[] getEncodings() {
		return this.encodings;
	}

	/**
	 * Sets the size of the array for storing parsed {@link QualityArgument}s.
	 *
	 * @param nbTokens
	 *            the size of the array for storing the parsed {@link QualityArgument}s.
	 */
	@Override
	public void initializeNbTokens(int nbTokens) {
		this.encodings = new QualityArgument[nbTokens];
	}

	/**
	 * Parses the <code>index</code>th {@link QualityArgument} from the index <code>start</code> to the index
	 * <code>stop</code>.
	 *
	 * @param index
	 *            the index of the {@link QualityArgument} to be parsed
	 * @param start
	 *            the start index in the current string to parse from
	 * @param stop
	 *            the end index in the current string to parse from
	 */
	@Override
	public void newToken(int index, int start, int stop) {
		QualityArgument qualityArgument = this.encodings[index] = new QualityArgument();
		qualityArgument.parse(this.currentString, start, stop);
	}

}
