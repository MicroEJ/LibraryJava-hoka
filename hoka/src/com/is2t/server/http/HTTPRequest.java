/**
 * Java
 *
 * Copyright 2009-2015 IS2T. All rights reserved.
 * IS2T PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.is2t.server.http;

import java.io.IOException;
import java.io.InputStream;
import java.util.Hashtable;

import com.is2t.server.http.encoding.IHTTPEncodingHandler;
import com.is2t.server.http.encoding.IHTTPTransferCodingHandler;
import com.is2t.server.http.encoding.UnsupportedHTTPEncodingException;
import com.is2t.server.http.support.MIMEUtils;

/**
 * IS2T-API
 * <p>
 * Represents a HTTP Request.
 * </p>
 */
public class HTTPRequest {

	/**
	 * IS2T-API
	 * <p>
	 * Value returned by {@link #getMethod()} if the request method is
	 * <code>POST</code>.
	 * </p>
	 */
	public static final int POST = 1;

	/**
	 * IS2T-API
	 * <p>
	 * Value returned by {@link #getMethod()} if the request method is
	 * <code>GET</code>.
	 * </p>
	 */
	public static final int GET = 2;

	/**
	 * IS2T-API
	 * <p>
	 * Value returned by {@link #getMethod()} if the request method is
	 * <code>PUT</code>.
	 * </p>
	 */
	public static final int PUT = 3;

	/**
	 * IS2T-API
	 * <p>
	 * Value returned by {@link #getMethod()} if the request method is
	 * <code>DELETE</code>.
	 * </p>
	 */
	public static final int DELETE = 4;

	/**
	 * IS2T-API
	 * Is the request containing multipart form encoded.
	 */
	private boolean isMultipartFormEncoded = false;
	
	/**
	 * IS2T-API
	 * The multiparts.
	 */
	private String[] parts;
	
	/**
	 * IS2T-API
	 * <p>
	 * Returns the request method as an integer value which is one of
	 * {@link #POST}, {@link #GET}, {@link #PUT} or {@link #DELETE}.
	 * </p>
	 * 
	 * @return the request method (one of {@link #POST}, {@link #GET},
	 *         {@link #PUT} or {@link #DELETE}).
	 */
	public int getMethod() {
		return method;
	}

	/**
	 * IS2T-API
	 * <p>
	 * Returns the request URI.
	 * </p>
	 * 
	 * @return the request URI string.
	 */
	public String getURI() {
		return uri;
	}

	/**
	 * IS2T-API
	 * <p>
	 * Returns the HTTP version request.
	 * </p>
	 * 
	 * @return the HTTP version request string.
	 */
	public String getVersion() {
		return version;
	}

	/**
	 * IS2T-API
	 * <p>
	 * Returns the query parameters as {@link Hashtable}.
	 * </p>
	 * 
	 * @return a {@link Hashtable} of (String,String) representing the HTTP
	 *         Query Parameters.
	 */
	public Hashtable getParameters() {
		return parameters;
	}

	/**
	 * IS2T-API
	 * 
	 * <p>
	 * Returns the header field value associated to the given header field <code>key</code>.
	 * </p>
	 * 
	 * @param key
	 *            a header field name (if <code>null</code>, <code>null</code> is
	 *            returned).
	 * @return the requested header field value, <code>null</code> if
	 *         the header field is not found.
	 */
	public String getHeaderField(String key) {
		if (key == null) {
			return null;
		}
		return (String) header.get(key.toLowerCase());
	}

	/**
	 * IS2T-API
	 * <p>
	 * Returns all HTTP Header fields of the request.
	 * </p>
	 * 
	 * @return a {@link Hashtable} of (String,String) representing the HTTP
	 *         Header Fields (may be empty).
	 */
	public Hashtable getHeader() {
		return header;
	}

	/*************************************************************************
	 * NOT IN API
	 ************************************************************************/

	/*
	 * Commonly used characters Typically used when parsing URI
	 */
	
	/**
	 * Space character.
	 */
	private static final char SPACE_CHAR = ' ';
	
	/**
	 * Percentage character.
	 */
	private static final char PERCENTAGE_CHAR = '%';
	/**
	 * Colon character.
	 */
	private static final char COLON_CHAR = ':';
	
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
	 * Question mark character.
	 */
	private static final char QUESTION_MARK_CHAR = '?';
	
	/**
	 * Error Message Malformed HTTP Request
	 */
	private final static String MALFORMED_HTTP_REQUEST = "Malformed HTTP Request";
	
	/**
	 * EOF marker (-1).
	 */
	private static final int END_OF_FILE = -1;

	/**
	 * Hexadecimal base (16).
	 */
	private static final int HEXA = 16;

	/**
	 * Request method code.
	 * @see HTTPRequest#GET
	 * @see HTTPRequest#POST
	 * @see HTTPRequest#PUT
	 * @see HTTPRequest#DELETE
	 */
	private int method;
	
	/**
	 * Request URI.
	 */
	private String uri;
	
	/**
	 * HTTP Request version String.
	 */
	private String version;
	
	/**
	 * Parsed request parameters.
	 */
	private Hashtable/* <String,String> */parameters;
	
	/**
	 * Parsed request headers.
	 */
	private Hashtable/* <String,String> */header;

	/**
	 * The {@link InputStream} to use.
	 */
	private InputStream stream;

	/**
	 * The {@link HTTPServer} instance.
	 */
	private HTTPServer server;

	/**
	 * Constructs a new instance of HTTPRequest.
	 * 
	 * @param server
	 *            the {@link HTTPServer} instance
	 * @param inputStream
	 *            the input stream for the request
	 * @throws IOException
	 *             if connection is lost during processing the request
	 * @throws IllegalArgumentException
	 *             if parsing the request header or body failed
	 * @throws UnsupportedHTTPEncodingException
	 *             when an unsupported HTTP encoding encountered
	 */
	protected HTTPRequest(HTTPServer server, InputStream inputStream)
			throws IOException, IllegalArgumentException {
		this.server = server;

		this.parameters = new Hashtable(10); // reasonable size for HTTP
		// Parameters (50 in our default
		// Hashtable implementation)

		// FIXME WI 4419, when Persistence will be implemented, this way of
		// request computing may move in another place but quite in the same way

		// process the request entirely by reading:
		// 1. method (get/post/put/delete), uri (my/resource.html), query
		// strings (?a=b&c=d), and HTTP header fields
		if (!parseRequestHeader(inputStream)) {
			throw new IllegalArgumentException(MALFORMED_HTTP_REQUEST);
		}

		// the content-type may have been parsed by parseRequestHeader
		String contentType = getHeaderField(HTTPConstants.FIELD_CONTENT_TYPE);
		
		/*
		if (contentType == null) {
			// If HTTP 1.1 and no content type, it's not the exact HTTP 1.1
			// spec.
			// Spec says: Content-Type header field SHOULD be specified
		}
		*/

		// Modify InputStream in case header specifies content encoding
		stream = getContentEncodingStream(inputStream);

		// 2. the body (potentially containing additional parameters if
		// Content-Type is x-www-form-urlencoded)
		if (MIMEUtils.MIME_FORM_ENCODED_DATA.equalsIgnoreCase(contentType)) {
			if (!parseRequestBody(stream)) {
				throw new IllegalArgumentException(MALFORMED_HTTP_REQUEST);
			}
		}

		// 3. the body contains a multipart form encoded
		if (contentType != null
				&& contentType
						.startsWith(MIMEUtils.MIME_MULTIPART_FORM_ENCODED_DATA)) {

			String boundary = contentType
					.substring(contentType.indexOf(';') + 1);

			boundary = boundary.substring(boundary.indexOf("boundary=") + 9);

			StringBuffer multipartBodyBuffer = new StringBuffer(2048);

			int readLen = -1;
			byte[] buff = new byte[1024];
			InputStream stream = this.stream;
			while((readLen = stream.read(buff)) != -1){
				multipartBodyBuffer.append(new String(buff, 0, readLen));
			}
			
			String multipartBody = multipartBodyBuffer.toString();
			parts = split(multipartBody, boundary);
			
			isMultipartFormEncoded = true;
			

		} else {
			readBody(stream);
		}
	}
	
	private String[] split(String toSplit, String separator){
		
		int index = toSplit.indexOf(separator);
		int numberOfElements = 0;
		String[] parts = new String[50];
		
		while(index > -1){

			int indexEnd = toSplit.indexOf(separator, index + separator.length());
			
			if(indexEnd != -1){
				parts[numberOfElements] = toSplit.substring(index + separator.length() + 2, indexEnd - 4);
			}else{
				parts[numberOfElements] = toSplit.substring(index + separator.length() + 2, toSplit.length() - 2);
			}
			
			numberOfElements++;
			index = indexEnd;
			
		} 
		
		System.arraycopy(parts, 0, parts = new String[numberOfElements], 0, numberOfElements - 1);
		
		return parts;
		
	}

	/**
	 * Returns the content encoding input stream.
	 * 
	 * @param in
	 *            input stream which can be encoded with the given
	 *            Content-Encoding
	 * @return an InputStream which allows the decoding (may be the same as
	 *         given in input), or null if no handler has been found to manage
	 *         this encoding.
	 * @throws IOException when I/O Error occurs.
	 * @throws UnsupportedHTTPEncodingException
	 *             when no suitable {@link IHTTPEncodingHandler} is found
	 */
	private InputStream getContentEncodingStream(InputStream in)
			throws IOException {
		HTTPServer server = this.server;
		// 1) transfer encoding
		String transferEncoding = getHeaderField(HTTPConstants.FIELD_TRANSFER_ENCODING);
		IHTTPTransferCodingHandler transferCodingHandler = server
				.getTransferCodingHandler(transferEncoding == null ? null
						: transferEncoding);
		if (transferCodingHandler == null) {
			// unable to manage transfer encoding
			throw new UnsupportedHTTPEncodingException(
					HTTPConstants.FIELD_TRANSFER_ENCODING, transferEncoding);
		}
		in = transferCodingHandler.open(this, in);

		// 2) content encoding
		String contentEncoding = getHeaderField(HTTPConstants.FIELD_CONTENT_ENCODING);
		if (contentEncoding != null) {
			IHTTPEncodingHandler handler = server
					.getEncodingHandler(contentEncoding);
			if (handler == null) {
				throw new UnsupportedHTTPEncodingException(
						HTTPConstants.FIELD_CONTENT_ENCODING, contentEncoding);
			}
			in = handler.open(in);
		}

		return in;
	}

	/**
	 * First step is to extract the method. The HTTP server supports only
	 * the GET and POST method. It can be written upper case or lower case
	 * depending of the client.
	 * 
	 * @param input the {@link InputStream} 
	 * @return true if a method get, post, put or delete is found false
	 *         otherwise
	 * @throws IOException
	 *             if connection has been lost
	 */
	private boolean parseMethod(InputStream input) throws IOException {
		int i;
		// read the first character, two choice: + g/G + P/p
		i = input.read();
		if (i == -1) {
			return false;
		}

		if (i == 'g' || i == 'G') {
			// so it was g or G so it should be (g)et or (G)ET
			i = input.read();
			if (i == -1 || (i != 'e' && i != 'E')) {
				return false;
			}

			i = input.read();
			if (i == -1 || (i != 't' && i != 'T')) {
				return false;
			}

			i = input.read();
			if (i == -1 || i != ' ') {
				return false;
			}

			method = GET;
			return true;
		} else if (i == 'p' || i == 'P') {
			i = input.read();
			if (i == -1) {
				return false;
			}

			if (i == 'u' || i == 'U') {

				i = input.read();
				if (i == -1 || (i != 't' && i != 'T')) {
					return false;
				}

				i = input.read();
				if (i == -1 || i != ' ') {
					return false;
				}

				method = PUT;
				return true;
			} else if (i == 'o' || i == 'O') {

				i = input.read();
				if (i == -1 || (i != 's' && i != 'S')) {
					return false;
				}

				i = input.read();
				if (i == -1 || (i != 't' && i != 'T')) {
					return false;
				}

				i = input.read();
				if (i == -1 || i != ' ') {
					return false;
				}

				method = POST;
				return true;
			} else {
				return false;
			}
		} else if (i == 'd' || i == 'D') {

			i = input.read();
			if (i == -1 || (i != 'e' && i != 'E')) {
				return false;
			}

			i = input.read();
			if (i == -1 || (i != 'l' && i != 'L')) {
				return false;
			}

			i = input.read();
			if (i == -1 || (i != 'e' && i != 'E')) {
				return false;
			}

			i = input.read();
			if (i == -1 || (i != 't' && i != 'T')) {
				return false;
			}

			i = input.read();
			if (i == -1 || (i != 'e' && i != 'E')) {
				return false;
			}

			i = input.read();
			if (i == -1 || i != ' ') {
				return false;
			}

			method = DELETE;
			return true;

		} else {
			return false;
		}
	}

	/**
	 * Extract the URI and store it in the URI field.
	 * 
	 * @param input the {@link InputStream}
	 * @return <code>true</code> if succeed, <code>false</code> otherwise
	 * @throws IOException
	 *             if connection has been lost
	 */
	private boolean parseURI(InputStream input) throws IOException {
		StringBuffer sb = new StringBuffer(Math.min(64, input.available()));
		// main loop
		loop: while (true) {
			// the stream should now be something like
			// "/resources/index.html?foo=bar " or "/resources/index "
			int i = input.read();
			if (i == -1) {
				return false;
			}

			switch (i) {
			case QUESTION_MARK_CHAR:
				// parse parameters
				if (!parserParameters(parameters, input)) {
					// no parameters found after the '?', error
					return false;
				}
			case SPACE_CHAR:
				break loop; // if QUESTION_MARK_CHAR or SPACE_CHAR break loop
			case PERCENTAGE_CHAR: 
				// percent encoded character decoding
				i = decodePercentage(input);
				if (i == -1) {
					// encoding error
					return false;
				}

				// in case i should be represented as a unicode surrogate pair
				i = handleSurrogatePair(i, sb);
				break;

			}
			sb.append((char) i); // assuming ASCII

		}

		uri = sb.toString();

		// get the version of HTTP
		byte[] version = new byte[10]; // HTTPx.y\r\n
		int readBytes = 0;
		while (readBytes < 10) {
			int r = input.read(version, readBytes, 10 - readBytes);
			if (r == -1) {
				// EOF
				return false;
			}
			readBytes += r;
		}

		// fixed: the version string contained the \r\n, this version removes
		// the \r\n.
		String tmp = new String(version);
		this.version = tmp.substring(0, tmp.length() - 2);
		return true;
	}

	/**
	 * Called by HTTPSession. Parse HTTP header. Fulfill the field header with
	 * the informations contained by the input stream.
	 * 
	 * @param input
	 *            the {@link InputStream}
	 * @return <code>false</code> if request is invalid, <code>true</code>
	 *         otherwise
	 * @throws IOException
	 *             if connection has been lost
	 */
	protected boolean parseRequestHeader(InputStream input) throws IOException {
		if (!parseMethod(input)) {
			return false;
		}
		if (!parseURI(input)) {
			return false;
		}

		// extract header
		if (!parseHeaderFields(input)) {
			return false;
		}
		return true;
	}

	/**
	 * Parses URL query parameters. This method can be called in
	 * {@link HTTPSession#answer(HTTPRequest)} method
	 * implementation to parse POST parameters in message body. Returns the
	 * parameters in a hashtable or <code>null</code> if the parameters cannot
	 * be read (EOF reached).
	 * 
	 * @param parameters
	 *            the hashtable to populate with the parsed parameters
	 * @param is
	 *            the input stream from which parameters should be parsed
	 * @return <code>true</code> if all parameters has been read
	 *         <code>false</code> if EOF or any error detected
	 * @throws IOException
	 *             if an error occurs while reading the input stream.
	 */
	private static boolean parserParameters(Hashtable parameters, InputStream is)
			throws IOException {
		boolean end = false;
		StringBuffer sbKey = new StringBuffer(16);
		StringBuffer sbValue = new StringBuffer(4);
		StringBuffer curBuffer = sbKey;
		// parameters is a hash table
		// the stream looks like
		// "foo=bar&zorg=baz<white space (space, newline, carriage return, tabulation>"
		loop: while (!end) {

			int i = is.read();
			switch (i) {
			case PERCENTAGE_CHAR:
				// if a special character is found then replace it by the real
				// ASCII value
				i = decodePercentage(is);
				if (i == -1) {
					return false;
				}
				// in case percentage decoded character should be represented as a unicode surrogate pair	
				i = handleSurrogatePair(i, curBuffer);

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
				end = true;
				break loop;
			case EQUAL_CHAR: // the key is found so decode the value know;
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
		return true;
	}

	/**
	 * When a percentage encoded UTF-16 Surrogate Pair is encountered (integer
	 * value above 0xFFFF) this method calculates the head and trail surrogate
	 * code point for the Unicode character. The head code point is inserted
	 * into the {@link StringBuffer} and the tail surrogate is returned. If the
	 * code doesn't denote a surrogate pair (value is less than 0xFFFF) simply
	 * return it.
	 * 
	 * @param code
	 *            the Unicode character value in the range 0x0-0x10FFFF)
	 * @param sb
	 *            The {@link StringBuffer}
	 * @return the original code (if code's value less than 0xFFFF) or the tail
	 *         surrogate code point of the surrogate pair.
	 */
	private static int handleSurrogatePair(int code, StringBuffer sb) {
		if (code > 0xffff) {
			/**
			 * 1. 0x10000 is subtracted from the code point, leaving a 20 bit
			 * number in the range 0..0xFFFFF. 2. The top ten bits (a number in
			 * the range 0..0x3FF) are added to 0xD800 to give the first code
			 * unit or lead surrogate, which will be in the range 0xD800..0xDBFF
			 * 3. The low ten bits (also in the range 0..0x3FF) are added to
			 * 0xDC00 to give the second code unit or trail surrogate, which
			 * will be in the range 0xDC00..0xDFFF
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



	/**
	 * Returns the character from the stream, which encode as "%ab" (single byte
	 * UTF-8) or "%ab%cd" (two byte ). The initial % mark has been already reed.
	 * The character is represented as %ab where "a" and "b" is a hexa character
	 * (0-9, A-F) if the value of (a * 16 + b) > 127 the next 3 bytes will be
	 * read as the second byte of a two-byte UTF-8 char. When a character
	 * encoding problem is encountered, returns -1 (ffff). Since there is no
	 * unicode character with this code, this does not cause problems. Unicode
	 * characters above code point ffff are not handled (Suplementary
	 * characters).
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
		 * boolean fiveByte = (x >>> 2) == 0x3E; boolean sixByte = (x >>> 1) ==
		 * 0x7E;
		 */

		// validity check
		if (!(twoByte || threeByte || fourByte /* || fiveByte || sixByte */)) {
			// encoding error
			return -1;
		}

		y = readEncodedCharacter(is, true);

		// validity check
		if ((y >>> 6) != 0x02) {
			return -1;
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
			return -1;
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
			return -1;
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
		return -1;
	}

	/**
	 * Reads a percentage encoded character from the input stream "%ab", where
	 * 'a' and 'b' is a hexadecimal digit.
	 * 
	 * @param is
	 *            the InputStream
	 * @param readPercentageCharacter
	 *            if true, the percentage character '%' is first read from the
	 *            stream. Returns -1 if not found.
	 * @return the value of the percentage encoded character in the range 0-255,
	 *         or -1 if any error occurred.
	 * @throws IOException
	 *             if I/O error occurred
	 */
	private static int readEncodedCharacter(InputStream is,
			boolean readPercentageCharacter) throws IOException {
		int i;
		if (readPercentageCharacter) {
			char percent = (char) (i = is.read());
			if (percent != '%') {
				// encoding error
				return -1;
			}
		}

		// first character
		char c1 = (char) (i = is.read());
		if (i == -1) {
			return -1;
		}
		char c2 = (char) (i = is.read());
		if (i == -1) {
			return -1;
		}

		int x;
		try {
			x = Character.digit(c1, HEXA) * HEXA + Character.digit(c2, HEXA);
		} catch (NumberFormatException e) {
			return -1;
		}
		return x;
	}

	/**
	 * Parse HTTP header fields from the HTTP request.
	 * 
	 * @param input
	 *            {@link InputStream} that contains the HTTP request
	 * @return true, if the parsing was successful, if false the parsing was unsuccessful.
	 * @throws IOException
	 *             if connection has been lost
	 */
	private boolean parseHeaderFields(InputStream input) throws IOException {
		// headers is a hashtable
		// the stream look like "foo:bar zor:zorvalue "
		Hashtable header = new Hashtable(10); // most HTTP requests have less
		// than 10 header fields

		StringBuffer sbKey = new StringBuffer(16);
		StringBuffer sbValue = new StringBuffer(16);
		StringBuffer curBuffer = sbKey;
		boolean pendingSpace = false;
		// read char before entering the loop. This allows to loop without
		// reading a new character and so process an already read character
		int i = input.read();
		loop: while (true) {
			if (i == -1) {
				return false;
			}

			switch (i) {
			case PERCENTAGE_CHAR:
				if (curBuffer == sbKey) {
					// no percent encoding allowed in HTTP header field name
					return false;
				}
				/*
				 RFC5987 is not implemented
				 */				
				break;
			case COLON_CHAR:
				/* patch: ':' char can be present in value */
				if (curBuffer == sbKey) {
					curBuffer = sbValue;
					// ignore this char, read next one and loop
					i = input.read();
					continue loop;
				}
				break;
			case SPACE_CHAR:
			case TABULATION_CHAR:
				// All white spaces sequence can be replaced by a single space
				// char. Moreover leading and trailing white spaces are ignored
				// Only mark that spaces are pending and pass to the next char.
				// A single space will be appended to curBuffer only when
				// something else than a space is appended.
				pendingSpace = true;
				// ignore this char: read next one and loop
				i = input.read();
				continue loop;
			case CARRIAGE_RETURN_CHAR:
				i = input.read();
				if (i != NEWLINE_CHAR) {
					return false;
				}

				// end of header
				if (sbKey.length() == 0 && sbValue.length() == 0) {
					// empty line : end of headers
					break loop;
				}

				// if next char is a white space, the header is not finished
				i = input.read();
				if (i == -1) {
					return false;
				}

				if (i == SPACE_CHAR || i == TABULATION_CHAR) {
					// not the end of the header, loop
					continue loop;
				}

				/**
				 * if the key ends with asterisk "*", this means an RFC5987 encoded header value.
				 * Since the RFC5987 is not implemented, these kind of header fields are ignored
				 */
				if (sbKey.charAt(sbKey.length()-1) != '*') {
					header.put(sbKey.toString().toLowerCase(), sbValue.toString());
				}
				
				// reuse buffers
				sbValue.delete(0, sbValue.length());
				sbKey.delete(0, sbKey.length());
				curBuffer = sbKey;

				// loop without reading a new character (already read to
				// determine if it was the end of the header)
				continue loop;
			}

			// append pending space, only if not leading space.
			if (pendingSpace) {
				pendingSpace = false;
				if (curBuffer.length() > 0) {
					curBuffer.append(SPACE_CHAR);
				}
			}
			curBuffer.append((char) i);
			// read next char
			i = input.read();
		}

		this.header = header;
		return true;
	}

	/**
	 * Parses the request body given in <code>stream</code>.
	 * 
	 * @param stream
	 *            the {@link InputStream}
	 * @return true if the parsing is successful
	 * @throws IOException
	 *             when connection is lost
	 */
	protected boolean parseRequestBody(InputStream stream) throws IOException {
		return parserParameters(parameters, stream);
	}

	/**
	 * Not implemented.
	 * 
	 * @param stream
	 *            the {@link InputStream} to read the body of the HTTP request
	 * @throws IOException
	 *             if an error occured during processing
	 */
	protected void readBody(InputStream stream) throws IOException {
		// it's a multipart type, we are not able to handle it by now
		// throw new RuntimeException("Multipart Encoded Data not supported");
	}

	/**
	 * Not implemented(empty method).
	 */
	protected void finish() {
		// === IMPORTANT NOTE
		// On linux (SunJvm & J9), closing a connection through server side when
		// there are remaining bytes to read
		// throws a reset exception to the client (TCP RST/ACK is sent)
		// This issue does not appear on Windows.
		// => Choice is to empty the InputStream

		// stream is an input stream that reads remaining
		// data in request body when closed.

		// try {
		// // close the input stream. Does NOT close underlying
		// // stream. cf IHTTPTransferCodingHandler.open
		// stream.close();
		// } catch (IOException e) {
		// // can't do anything more
		// }
	}

	/**
	 * The request contains multipart form encoded
	 * @return true if the request has some form encoded multiparts
	 */
	public boolean isMultipartFormEncoded(){
		return isMultipartFormEncoded;
	}
	
	/**
	 * The multiparts
	 * @return the parts if the request has some form encoded multiparts, null otherwise
	 */
	public String[] parts(){
		return parts;
	}
	
}
