/*
 * Java
 *
 * Copyright 2019 MicroEJ Corp. All rights reserved.
 * This library is provided in source code for use, modification and test, subject to license terms.
 * Any modification of the source code will break MicroEJ Corp. warranties on the whole library.
 */
package ej.hoka.http.support;

import java.io.IOException;
import java.io.InputStream;

/**
 * <p>
 * Utilities for decoding percent encoded characters.
 * </p>
 */
public class URLDecoder {

	/**
	 * Hexadecimal base (16).
	 */
	private static final int HEXA = 16;

	/**
	 * Returns the character from the stream, which encode as "%ab" (single byte UTF-8) or "%ab%cd" (two byte ). The
	 * initial % mark has been already reed. The character is represented as %ab where "a" and "b" is a hexa character
	 * (0-9, A-F) if the value of (a * 16 + b) &gt; 127 the next 3 bytes will be read as the second byte of a two-byte
	 * UTF-8 char. When a character encoding problem is encountered, returns -1 (ffff). Since there is no unicode
	 * character with this code, this does not cause problems.
	 *
	 * When a percentage encoded UTF-16 Surrogate Pair is encountered (integer value above 0xFFFF) this method
	 * calculates the head and trail surrogate code point for the Unicode character. The head code point is inserted
	 * into the {@link StringBuilder} and the tail surrogate is returned. If the code doesn't denote a surrogate pair
	 * (value is less than 0xFFFF) simply return it.
	 *
	 * @param is
	 *            the {@link InputStream}.
	 * @param sb
	 *            the {@link StringBuilder}.
	 * @return the original code (if code's value less than 0xFFFF) or the tail surrogate code point of the surrogate
	 *         pair.
	 * @throws IOException
	 *             if an I/O error occurred reading <code>is</code>.
	 */
	public static int decode(InputStream is, StringBuilder sb) throws IOException {
		return URLDecoder.handleSurrogatePair(URLDecoder.decodePercentage(is), sb);
	}

	/**
	 * Returns the character from the stream, which encode as "%ab" (single byte UTF-8) or "%ab%cd" (two byte ). The
	 * initial % mark has been already reed. The character is represented as %ab where "a" and "b" is a hexa character
	 * (0-9, A-F) if the value of (a * 16 + b) &gt; 127 the next 3 bytes will be read as the second byte of a two-byte
	 * UTF-8 char. When a character encoding problem is encountered, returns -1 (ffff). Since there is no unicode
	 * character with this code, this does not cause problems. Unicode characters above code point ffff are not handled
	 * (Suplementary characters).
	 *
	 * @param is
	 *            the Input Stream
	 * @return the character as integer
	 * @throws IOException
	 *             when an I/O error occured reading the Stream
	 */
	private static int decodePercentage(InputStream is) throws IOException {

		// temporary variables for storing encoded character values
		int x, y, z, u;

		// the % is already consumed by the caller method, so skip it
		x = readEncodedCharacter(is, false);

		// how much byte we should decode?
		//
		// one:____0xxxxxxx
		// two:____110xxxxx|10xxxxxx
		// three:__1110xxxx|10xxxxxx|10xxxxxx
		// four:___11110xxx|10xxxxxx|10xxxxxx|10xxxxxx

		boolean oneByte = (x >>> 7) == 0x00;

		if (oneByte) {
			return x;
		}

		boolean twoByte = (x >>> 5) == 0x06;
		boolean threeByte = (x >>> 4) == 0x0E;
		boolean fourByte = (x >>> 3) == 0x1E;
		/*
		 * boolean fiveByte = (x >>> 2) == 0x3E; boolean sixByte = (x >>> 1) == 0x7E;
		 */

		// validity check
		if (!(twoByte || threeByte || fourByte /* || fiveByte || sixByte */)) {
			throw new IllegalArgumentException();
		}

		y = readEncodedCharacter(is, true);

		// validity check
		if ((y >>> 6) != 0x02) {
			throw new IllegalArgumentException();
		}

		if (twoByte) {
			// two byte
			// 110xxxxx|10xxxxxx
			// x|y
			y = y & 0x3F;
			x = (x & 0x1F) << 6;

			return y | x;
		}

		z = readEncodedCharacter(is, true);
		// validity check
		if ((z >>> 6) != 0x02) {
			throw new IllegalArgumentException();
		}

		if (threeByte) {
			// three byte
			// 1110xxxx 10xxxxxx 10xxxxxx
			// x|y|z
			z = (z & 0x3F);
			y = (y & 0x3F) << 6;
			x = (x & 0x0F) << 12;
			return z | y | x;

		}

		u = readEncodedCharacter(is, true);
		// validity check
		if ((u >>> 6) != 0x02) {
			throw new IllegalArgumentException();
		}

		if (fourByte) {
			// four byte
			// 11110xxx|10xxxxxx|10xxxxxx|10xxxxxx
			// x|y|z|u
			u = (u & 0x3F);
			z = (z & 0x3F) << 6;
			y = (y & 0x3F) << 12;
			x = (x & 0x07) << 18;

			return u | z | y | x;
		}

		// unexpected error
		throw new IllegalArgumentException();
	}

	/**
	 * Reads a percentage encoded character from the input stream "%ab", where 'a' and 'b' is a hexadecimal digit.
	 *
	 * @param is
	 *            the InputStream
	 * @param readPercentageCharacter
	 *            if true, the percentage character '%' is first read from the stream. Returns -1 if not found.
	 * @return the value of the percentage encoded character in the range 0-255, or -1 if any error occurred.
	 * @throws IOException
	 *             if I/O error occurred
	 */
	private static int readEncodedCharacter(InputStream is, boolean readPercentageCharacter) throws IOException {
		int i;
		if (readPercentageCharacter) {
			char percent = (char) (i = is.read());
			if (i == -1) {
				throw new IOException();
			}
			if (percent != '%') {
				throw new IllegalArgumentException();
			}
		}

		// first character
		char c1 = (char) (i = is.read());
		if (i == -1) {
			throw new IOException();
		}
		char c2 = (char) (i = is.read());
		if (i == -1) {
			throw new IOException();
		}

		int x;
		try {
			x = (Character.digit(c1, HEXA) * HEXA) + Character.digit(c2, HEXA);
		} catch (NumberFormatException e) {
			throw new IllegalArgumentException();
		}
		return x;
	}

	/**
	 * When a percentage encoded UTF-16 Surrogate Pair is encountered (integer value above 0xFFFF) this method
	 * calculates the head and trail surrogate code point for the Unicode character. The head code point is inserted
	 * into the {@link StringBuilder} and the tail surrogate is returned. If the code doesn't denote a surrogate pair
	 * (value is less than 0xFFFF) simply return it.
	 *
	 * @param code
	 *            the Unicode character value in the range 0x0-0x10FFFF)
	 * @param sb
	 *            The {@link StringBuilder}
	 * @return the original code (if code's value less than 0xFFFF) or the tail surrogate code point of the surrogate
	 *         pair.
	 */
	private static int handleSurrogatePair(int code, StringBuilder sb) {
		if (code > 0xffff) {
			/**
			 * 1. 0x10000 is subtracted from the code point, leaving a 20 bit number in the range 0..0xFFFFF. 2. The top
			 * ten bits (a number in the range 0..0x3FF) are added to 0xD800 to give the first code unit or lead
			 * surrogate, which will be in the range 0xD800..0xDBFF 3. The low ten bits (also in the range 0..0x3FF) are
			 * added to 0xDC00 to give the second code unit or trail surrogate, which will be in the range
			 * 0xDC00..0xDFFF
			 */
			code = code - 0x10000;
			int h = (code >>> 10) + 0xD800;
			int l = (code & 0x3ff) + 0xDC00;
			// the lead surrogate is added to the buffer
			sb.append((char) h);
			// the trail surrogate will be added to the buffer
			code = l;
			return code;
		} else {
			// no surrogate pair, return original UTF-16 code
			return code;
		}
	}

	private URLDecoder() {
		// Forbid instantiation.
	}

}
