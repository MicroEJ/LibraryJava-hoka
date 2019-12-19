/*
 * Java
 *
 * Copyright 2019 MicroEJ Corp. All rights reserved.
 * This library is provided in source code for use, modification and test, subject to license terms.
 * Any modification of the source code will break MicroEJ Corp. warranties on the whole library.
 */
package ej.hoka.http.body;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import ej.hoka.http.support.MIMEUtils;
import ej.hoka.http.support.URLDecoder;

/**
 * A parser for {@link MIMEUtils#MIME_FORM_ENCODED_DATA} and parameters in URI.
 */
public class ParameterParser implements BodyParser<Map<String, String>> {

	/**
	 * Space character.
	 */
	private static final char SPACE_CHAR = ' ';

	/**
	 * Percentage character.
	 */
	private static final char PERCENTAGE_CHAR = '%';

	/**
	 * Newline character.
	 */
	private static final char NEWLINE_CHAR = '\n';

	/**
	 * Carriage return character.
	 */
	private static final char CARRIAGE_RETURN_CHAR = '\r';

	/**
	 * Tab character.
	 */
	private static final char TABULATION_CHAR = '\t';

	/**
	 * Ampersand character.
	 */
	private static final char AMPERSAND_CHAR = '&';

	/**
	 * Equals character.
	 */
	private static final char EQUAL_CHAR = '=';

	/**
	 * Plus character.
	 */
	private static final char PLUS_CHAR = '+';

	/**
	 * EOF marker (-1).
	 */
	private static final int END_OF_FILE = -1;

	@Override
	public Map<String, String> parseBody(InputStream inputStream, String contentType) throws IOException {
		Map<String, String> body = new HashMap<>();
		parseParameters(inputStream, body);
		return body;
	}

	/**
	 * Parses parameters.
	 *
	 * @param input
	 *            the input stream from which parameters should be parsed
	 * @param parameters
	 *            the map to populate with the parsed parameters.
	 * @throws IOException
	 *             if an error occurs while reading the input stream.
	 */
	public static void parseParameters(InputStream input, Map<String, String> parameters) throws IOException {
		boolean end = false;
		StringBuilder sbKey = new StringBuilder(16);
		StringBuilder sbValue = new StringBuilder(4);
		StringBuilder curBuffer = sbKey;
		// parameters is a hash table
		// the stream looks like
		// "foo=bar&zorg=baz<white space (space, newline, carriage return, tabulation>"
		loop: while (!end) {

			int i = input.read();
			switch (i) {
			case PERCENTAGE_CHAR:
				// if a special character is found then replace it by the real
				// ASCII value
				i = URLDecoder.decode(input, curBuffer);
				break;
			case PLUS_CHAR:
				// real '+' are encoded as %2b in HTTP headers, '+' char is a
				// space alias
				i = SPACE_CHAR;
				break;
			case END_OF_FILE:
				// save the last parameter
				if (sbKey.length() > 0) {
					parameters.put(sbKey.toString(), sbValue.toString());
				}
				break loop;
			case EQUAL_CHAR:
				// the key is found so decode the value know
				// just don't add the '=' char
				curBuffer = sbValue;
				continue loop;
			case SPACE_CHAR:
			case NEWLINE_CHAR:
			case CARRIAGE_RETURN_CHAR:
			case TABULATION_CHAR:
				end = true;
			case AMPERSAND_CHAR:
				// this is the start of a new key so, that means the value is
				// found and signal that there is no need to add the char '&'
				parameters.put(sbKey.toString(), sbValue.toString());
				sbValue.delete(0, sbValue.length()); // avoid object creation
				sbKey.delete(0, sbKey.length());
				curBuffer = sbKey;
				continue loop;
			}

			curBuffer.append((char) i);

		}
	}

}
