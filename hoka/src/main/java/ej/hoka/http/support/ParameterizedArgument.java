/*
 * Java
 *
 * Copyright 2009-2019 MicroEJ Corp. All rights reserved.
 * This library is provided in source code for use, modification and test, subject to license terms.
 * Any modification of the source code will break MicroEJ Corp. warranties on the whole library.
 */
package ej.hoka.http.support;

/**
 * Generic parser for parameterized arguments.
 * <p>
 * Example:
 *
 * <pre>
 * text/xml ; charset=toto
 * text/xml; q=0.2
 * gzip; q=0.2
 * </pre>
 */
public abstract class ParameterizedArgument extends CharacterSeparatedList {

	/**
	 * Separator for the parameters: ';'.
	 */
	public static final char PARAMETER_SEP = ';';
	/**
	 * Separator for the tokens: '='.
	 */
	public static final char TOKEN_SEP = '=';

	/**
	 * Current argument.
	 */
	protected String argument;

	/**
	 * Creates a new instance of {@link ParameterizedArgument} with the default separator character of semicolon (";").
	 */
	public ParameterizedArgument() {
		super(PARAMETER_SEP);
	}

	/**
	 * Appends the current argument to the {@link StringBuffer} <code>sb</code>.
	 *
	 * @return the {@link StringBuffer} with the appended argument.
	 * @param sb
	 *            the {@link StringBuffer} to append the argument
	 */
	@Override
	public StringBuilder generate(StringBuilder sb) {
		return sb.append(this.argument);
	}

	/**
	 * Returns current argument.
	 *
	 * @return current argument
	 */
	public String getArgument() {
		return this.argument;
	}

	/**
	 * Initialize the number of parameters to read for this argument. Subclasses should override this abstract method.
	 *
	 * @param nbParameters
	 *            the number of parameters
	 */
	protected abstract void initializeNbParameters(int nbParameters);

	/**
	 * Initialize the number of tokens.
	 *
	 * @param nbTokens
	 *            the number of tokens this instance can handle.
	 */
	@Override
	protected void initializeNbTokens(int nbTokens) {
		initializeNbParameters(nbTokens - 1);
	}

	/**
	 * Reads a new parameter. A key-value pair is parsed from the current string argument. The key part will be a
	 * substring from index startKey to index stopKey. The value part will be the substring from index startValue to
	 * index stopValue.
	 *
	 * @param id
	 *            id for this parameter
	 * @param startKey
	 *            the start index of searching the key substring in current argument
	 * @param stopKey
	 *            the stop index of searching the key substring in current argument
	 * @param startValue
	 *            start index of searching the value substring in current argument
	 * @param stopValue
	 *            stop index of searching the value substring in current argument
	 * @see ContentType#newParameter(int, int, int, int, int)
	 * @see QualityArgument#newParameter(int, int, int, int, int)
	 */
	protected abstract void newParameter(int id, int startKey, int stopKey, int startValue, int stopValue);

	/**
	 * Adds a new token to the array of tokens. The search will begin from the index start to the index stop.
	 *
	 * @param id
	 *            the ID of the token
	 * @param start
	 *            start index of parameter search in the current string
	 * @param stop
	 *            end index of parameter search in the current string
	 */
	@Override
	protected void newToken(int id, int start, int stop) {
		if (id == 0) {
			// argument
			this.argument = this.currentString.substring(start, stop);
		} else {
			int equalPtr = this.currentString.indexOf(TOKEN_SEP, start);
			if (equalPtr > stop) {
				equalPtr = -1;
			}
			int startValue, stopValue;
			if (equalPtr == -1) {
				// empty value
				startValue = stopValue = stop;
			} else {
				startValue = equalPtr + 1;
				stopValue = stop;
			}
			newParameter(id - 1, start, equalPtr, startValue, stopValue);
		}
	}

}
