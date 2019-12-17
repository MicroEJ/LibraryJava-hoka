/*
 * Java
 *
 * Copyright 2009-2019 MicroEJ Corp. All rights reserved.
 * This library is provided in source code for use, modification and test, subject to license terms.
 * Any modification of the source code will break MicroEJ Corp. warranties on the whole library.
 */
package ej.hoka.http.support;

/**
 * This class handles the HTTP Content-Type header field.<br>
 * See: RFC HTTP/1.1 RFC2616 14.1 Content-Type
 */
public class ContentType extends ParameterizedArgument {
	/**
	 * The string "charset".
	 */
	public static final String KEY_CHARSET = "charset"; //$NON-NLS-1$

	/**
	 * Optional charset. May be null.
	 */
	private String charset;

	/**
	 * Empty Constructor.
	 */
	public ContentType() {
	}

	/**
	 * Constructor with MIME type parameter.
	 *
	 * @param mimeType
	 *            the MIME type to use for content type
	 */
	public ContentType(String mimeType) {
		this.argument = mimeType;
	}

	/**
	 * Constructor with MIME type and character set parameters.
	 *
	 * @param mimeType
	 *            the MIME type to use as content type
	 * @param charset
	 *            the character set to use
	 */
	public ContentType(String mimeType, String charset) {
		this(mimeType);
		this.charset = charset;
	}

	/**
	 * IS2T-API Canonises the MIME type.
	 * <p>
	 * At the end of the parsing, converts the MIME type to lower case and interns it.
	 */
	@Override
	public void endParse() {
		// canonise MIME type
		this.argument = this.argument.toLowerCase().intern();
	}

	/**
	 * Appends the current character set to the {@link StringBuffer} <code>sb</code> in the following form:
	 * <code>;charset=&lt;character-set-string&gt;</code>.
	 *
	 * @param sb
	 *            the {@link StringBuffer}
	 * @return the {@link StringBuffer} <code>sb</code>.
	 */
	@Override
	public StringBuilder generate(StringBuilder sb) {
		super.generate(sb);
		if (this.charset != null) {
			sb.append(PARAMETER_SEP).append(KEY_CHARSET).append(TOKEN_SEP).append(this.charset);
		}
		return sb;
	}

	/**
	 * Returns the optional charset. Could be null.
	 *
	 * @return the optional charset.
	 */
	public String getCharset() {
		return this.charset;
	}

	/**
	 * Returns the canonized MIME type (converted to lower case).
	 *
	 * @return the canonized MIME type (converted to lower case)
	 */
	public String getMIMEType() {
		return this.argument;
	}

	/**
	 * Does nothing.
	 *
	 * @param nbParameters
	 *            not used.
	 */
	@Override
	protected void initializeNbParameters(int nbParameters) {
		// nothing to do
	}

	/**
	 * Checks if the key value (from index startKey to index stopKey) equals to "charset" and updates the internally
	 * stored charset to the value extracted from index startValue to index stopValue from the
	 * {@link CharacterSeparatedList#currentString}.
	 *
	 * @param id
	 *            not used
	 * @param startKey
	 *            start index of key
	 * @param stopKey
	 *            end index of key
	 * @param startValue
	 *            start index of character set substring
	 * @param stopValue
	 *            end index of character set substring
	 */
	@Override
	protected void newParameter(int id, int startKey, int stopKey, int startValue, int stopValue) {
		String key = this.currentString.substring(startKey, stopKey).intern();
		String value = this.currentString.substring(startValue, stopValue);
		if (key == KEY_CHARSET) {
			this.charset = value;
		}
	}

}
