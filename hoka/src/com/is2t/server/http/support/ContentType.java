/**
 * Java
 *
 * Copyright 2009-2013 IS2T. All rights reserved.
 * IS2T PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.is2t.server.http.support;

/**
 * <p>
 * This class handles the HTTP Content-Type header field.<br>
 * See: RFC HTTP/1.1 RFC2616 14.1 Content-Type
 * </p>
 */
public class ContentType extends ParameterizedArgument {
	/**
	 * IS2T-API
	 * <p>
	 * Empty Constructor.
	 * </p>
	 */
	public ContentType() {
	}

	/**
	 * IS2T-API
	 * <p>
	 * Constructor with MIME type parameter.
	 * </p>
	 * 
	 * @param mimeType
	 *            the MIME type to use for content type
	 */
	public ContentType(String mimeType) {
		this.argument = mimeType;
	}

	/**
	 * IS2T-API
	 * <p>
	 * Constructor with MIME type and character set parameters.
	 * </p>
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
	 * At the end of the parsing, converts the MIME type to lower case and
	 * interns it.
	 * </p>
	 */
	public void endParse() {
		// canonise MIME type
		argument = argument.toLowerCase().intern();
	}

	/**
	 * IS2T-API
	 * <p>
	 * Appends the current character set to the {@link StringBuffer}
	 * <code>sb</code> in the following form:<code>;charset=&lt;character-set-string&gt;</code>
	 * </p>
	 * 
	 * @param sb
	 *            the {@link StringBuffer}
	 * @return the {@link StringBuffer} <code>sb</code>
	 */
	public StringBuffer generate(StringBuffer sb) {
		super.generate(sb);
		if (charset != null) {
			sb.append(PARAMETER_SEP).append(KEY_CHARSET).append(TOKEN_SEP)
					.append(charset);
		}
		return sb;
	}

	/**
	 * IS2T-API
	 * <p>
	 * Returns the canonized MIME type (converted to lower case).
	 * </p>
	 * 
	 * @return the canonized MIME type (converted to lower case)
	 */
	public String getMIMEType() {
		return argument;
	}

	/**
	 * IS2T-API
	 * <p>
	 * Returns the optional charset. Could be null.
	 * </p>
	 * 
	 * @return the optional charset.
	 */
	public String getCharset() {
		return charset;
	}

	/**
	 * IS2T-API
	 * <p>
	 * The string "charset".
	 * </p>
	 */
	public static final String KEY_CHARSET = "charset"; // $NON-NLS

	/*************************************************************************
	 * NOT IN API
	 ************************************************************************/

	/**
	 * <p>
	 * Optional charset. May be null.
	 * </p>
	 */
	private String charset;

	/**
	 * Does nothing.
	 * 
	 * @param nbParameters
	 *            not used.
	 */
	protected void initializeNbParameters(int nbParameters) {

	}

	/**
	 * Checks if the key value (from index startKey to index stopKey) equals to
	 * "charset" and updates the internally stored charset to the value
	 * extracted from index startValue to index stopValue from the
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
	protected void newParameter(int id, int startKey, int stopKey,
			int startValue, int stopValue) {
		String key = currentString.substring(startKey, stopKey).intern();
		String value = currentString.substring(startValue, stopValue);
		if (key == KEY_CHARSET) {
			charset = value;
		}
	}

}
