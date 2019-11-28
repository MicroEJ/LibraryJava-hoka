/*
 * Java
 *
 * Copyright 2009-2019 MicroEJ Corp. All rights reserved.
 * This library is provided in source code for use, modification and test, subject to license terms.
 * Any modification of the source code will break MicroEJ Corp. warranties on the whole library.
 */
package ej.hoka.http;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import ej.hoka.http.body.BodyParser;
import ej.hoka.http.body.ParameterParser;
import ej.hoka.http.encoding.HTTPEncodingRegistry;
import ej.hoka.http.encoding.IHTTPEncodingHandler;
import ej.hoka.http.encoding.IHTTPTransferCodingHandler;
import ej.hoka.http.encoding.UnsupportedHTTPEncodingException;
import ej.hoka.http.support.URLDecoder;

/**
 * Represents a HTTP Request.
 */
public class HTTPRequest {

	/**
	 * <p>
	 * Value returned by {@link #getMethod()} if the request method is <code>POST</code>.
	 * </p>
	 */
	public static final int POST = 1;

	/**
	 * <p>
	 * Value returned by {@link #getMethod()} if the request method is <code>GET</code>.
	 * </p>
	 */
	public static final int GET = 2;

	/**
	 * <p>
	 * Value returned by {@link #getMethod()} if the request method is <code>PUT</code>.
	 * </p>
	 */
	public static final int PUT = 3;

	/**
	 * <p>
	 * Value returned by {@link #getMethod()} if the request method is <code>DELETE</code>.
	 * </p>
	 */
	public static final int DELETE = 4;

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
	 * The colon character.
	 */
	private static final String RESPONSE_COLON = ": "; //$NON-NLS-1$

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
	 * Question mark character.
	 */
	private static final char QUESTION_MARK_CHAR = '?';

	/**
	 * Error Malformed HTTP Request.
	 */
	private static final String MALFORMED_HTTP_REQUEST = "Malformed HTTP Request"; //$NON-NLS-1$

	/**
	 * Error connection lost.
	 */
	private static final String CONNECTION_LOST = "Connection lost"; //$NON-NLS-1$

	/**
	 * Request method code.
	 *
	 * @see HTTPRequest#GET
	 * @see HTTPRequest#POST
	 * @see HTTPRequest#PUT
	 * @see HTTPRequest#DELETE
	 */
	private final int method;

	/**
	 * Request URI.
	 */
	private final String uri;

	/**
	 * Parsed request parameters.
	 */
	private final Map<String, String> parameters;

	/**
	 * HTTP Request version String.
	 */
	private final String version;

	/**
	 * Parsed request headers.
	 */
	private final Map<String, String> header;

	/**
	 * The {@link InputStream} to use.
	 */
	private final InputStream body;

	private Map<String, String> cookies;

	/**
	 * Constructs a new instance of HTTPRequest.
	 *
	 * @param inputStream
	 *            the input stream of the request
	 * @param encodingRegistry
	 *            the register of available encoding and transfer coding handlers.
	 * @throws IOException
	 *             if connection is lost during processing the request
	 * @throws IllegalArgumentException
	 *             if parsing the request failed
	 */
	protected HTTPRequest(InputStream inputStream, HTTPEncodingRegistry encodingRegistry) throws IOException {
		this.method = parseMethod(inputStream);
		this.uri = parseURI(inputStream, this.parameters = new HashMap<>(10));
		this.version = parseVersion(inputStream);
		this.header = parseHeaderFields(inputStream);

		this.body = getContentEncodingStream(inputStream, encodingRegistry);
	}

	/**
	 * <p>
	 * Returns the request method as an integer value which is one of {@link #POST}, {@link #GET}, {@link #PUT} or
	 * {@link #DELETE}.
	 * </p>
	 *
	 * @return the request method (one of {@link #POST}, {@link #GET}, {@link #PUT} or {@link #DELETE}).
	 */
	public int getMethod() {
		return this.method;
	}

	/**
	 * <p>
	 * Returns the request URI.
	 * </p>
	 *
	 * @return the request URI string.
	 */
	public String getURI() {
		return this.uri;
	}

	/**
	 * <p>
	 * Returns the query parameters as {@link Map}.
	 * </p>
	 *
	 * @return a {@link Map} of (String,String) representing the HTTP Query Parameters.
	 */
	public Map<String, String> getParameters() {
		return Collections.unmodifiableMap(this.parameters);
	}

	/**
	 * <p>
	 * Returns the HTTP version request.
	 * </p>
	 *
	 * @return the HTTP version request string.
	 */
	public String getVersion() {
		return this.version;
	}

	/**
	 * <p>
	 * Returns all HTTP Header fields of the request.
	 * </p>
	 *
	 * @return a {@link Map} of (String,String) representing the HTTP Header Fields (may be empty).
	 */
	public Map<String, String> getHeader() {
		return Collections.unmodifiableMap(this.header);
	}

	/**
	 *
	 * <p>
	 * Returns the header field value associated to the given header field <code>key</code>.
	 * </p>
	 *
	 * @param key
	 *            a header field name (if <code>null</code>, <code>null</code> is returned).
	 * @return the requested header field value, <code>null</code> if the header field is not found.
	 */
	public String getHeaderField(String key) {
		if (key == null) {
			return null;
		}
		return this.header.get(key.toLowerCase());
	}

	/**
	 * Return the cookies of the request.
	 * <p>
	 * Cookies are lazily parsed.
	 *
	 * @return the cookies.
	 */
	public Map<String, String> getCookies() {
		if (this.cookies == null) {
			this.cookies = parseCookies(this.header.get(HTTPConstants.FIELD_COOKIES));
		}
		return this.cookies;
	}

	/**
	 * Return the cookie of the request with given name.
	 * <p>
	 * Cookies are lazily parsed.
	 *
	 * @param name
	 *            the name of the cookie.
	 * @return the cookie.
	 */
	public String getCookie(String name) {
		if (name == null) {
			return null;
		}
		return getCookies().get(name);
	}

	/**
	 * Request the body to be parsed.
	 *
	 * @param <T>
	 *            the type of body.
	 * @param bodyParser
	 *            the parser.
	 * @return the parsed body.
	 * @throws IOException
	 *             if an {@link IOException} occurs during parsing.
	 */
	public <T> T parseBody(BodyParser<T> bodyParser) throws IOException {
		return bodyParser.parseBody(this.body, getHeaderField(HTTPConstants.FIELD_CONTENT_TYPE));
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
	 * Returns the content encoding input stream.
	 *
	 * @param in
	 *            input stream which can be encoded with the given Content-Encoding
	 * @return an InputStream which allows the decoding (may be the same as given in input), or null if no handler has
	 *         been found to manage this encoding.
	 * @throws IOException
	 *             when I/O Error occurs.
	 */
	private InputStream getContentEncodingStream(InputStream in, HTTPEncodingRegistry encodingRegistry)
			throws IOException {
		// 1) transfer encoding
		String transferEncoding = getHeaderField(HTTPConstants.FIELD_TRANSFER_ENCODING);
		IHTTPTransferCodingHandler transferCodingHandler = encodingRegistry.getTransferCodingHandler(transferEncoding);
		if (transferCodingHandler == null) {
			// unable to manage transfer encoding
			throw new UnsupportedHTTPEncodingException(HTTPConstants.HTTP_STATUS_NOTIMPLEMENTED,
					HTTPConstants.FIELD_TRANSFER_ENCODING + RESPONSE_COLON + transferEncoding);
		}
		in = transferCodingHandler.open(this, in);

		// 2) content encoding
		String contentEncoding = getHeaderField(HTTPConstants.FIELD_CONTENT_ENCODING);
		if (contentEncoding != null) {
			IHTTPEncodingHandler handler = encodingRegistry.getEncodingHandler(contentEncoding);
			if (handler == null) {
				throw new UnsupportedHTTPEncodingException(HTTPConstants.HTTP_STATUS_NOTIMPLEMENTED,
						HTTPConstants.FIELD_TRANSFER_ENCODING + RESPONSE_COLON + contentEncoding);
			}
			in = handler.open(in);
		}

		return in;
	}

	/**
	 * First step is to extract the method. The HTTP server supports only the GET and POST method. It can be written
	 * upper case or lower case depending of the client.
	 *
	 * @param input
	 *            the {@link InputStream}
	 * @return true if a method get, post, put or delete is found false otherwise
	 * @throws IOException
	 *             if connection has been lost
	 */
	private static int parseMethod(InputStream input) throws IOException {
		StringBuilder builder = new StringBuilder();
		int read;
		while ((read = input.read()) != ' ') {
			if (read == -1) {
				throw new IOException(CONNECTION_LOST);
			}
			builder.append((char) read);
		}

		String inputMethod = builder.toString().toUpperCase();
		switch (inputMethod) {
		case HTTPConstants.HTTP_METHOD_GET:
			return GET;
		case HTTPConstants.HTTP_METHOD_POST:
			return POST;
		case HTTPConstants.HTTP_METHOD_PUT:
			return PUT;
		case HTTPConstants.HTTP_METHOD_DELETE:
			return DELETE;
		default:
			throw new IllegalArgumentException(MALFORMED_HTTP_REQUEST);
		}
	}

	/**
	 * Extract the URI and store it in the URI field.
	 *
	 * @param input
	 *            the {@link InputStream}
	 * @return <code>true</code> if succeed, <code>false</code> otherwise
	 * @throws IOException
	 *             if connection has been lost
	 */
	private static String parseURI(InputStream input, Map<String, String> parameters) throws IOException {
		StringBuilder sb = new StringBuilder(Math.min(64, input.available()));
		// main loop
		loop: while (true) {
			// the stream should now be something like
			// "/resources/index.html?foo=bar " or "/resources/index "
			int i = input.read();
			if (i == -1) {
				throw new IOException(CONNECTION_LOST);
			}

			switch (i) {
			case QUESTION_MARK_CHAR:
				// parse parameters
				ParameterParser.parseParameters(input, parameters);
				break loop;
			case SPACE_CHAR:
				break loop;
			case PERCENTAGE_CHAR:
				// percent encoded character decoding
				i = URLDecoder.decode(input, sb);
				break;

			}
			sb.append((char) i); // assuming ASCII

		}

		return sb.toString();
	}

	private static String parseVersion(InputStream input) throws IOException {
		byte[] version = new byte[10]; // HTTPx.y\r\n
		int readBytes = 0;
		while (readBytes < 10) {
			int r = input.read(version, readBytes, 10 - readBytes);
			if (r == -1) {
				// EOF
				throw new IOException(CONNECTION_LOST);
			}
			readBytes += r;
		}
		return new String(version, 0, version.length - 2);
	}

	/**
	 * Parse HTTP header fields from the HTTP request.
	 *
	 * @param input
	 *            {@link InputStream} that contains the HTTP request
	 * @return
	 * @throws IOException
	 *             if connection has been lost
	 */
	private static Map<String, String> parseHeaderFields(InputStream input) throws IOException {
		// headers is a hashmap
		// the stream look like "foo:bar zor:zorvalue "
		HashMap<String, String> header = new HashMap<>(10); // most HTTP requests have less
		// than 10 header fields

		StringBuilder sbKey = new StringBuilder(16);
		StringBuilder sbValue = new StringBuilder(16);
		StringBuilder curBuffer = sbKey;
		boolean pendingSpace = false;
		// read char before entering the loop. This allows to loop without
		// reading a new character and so process an already read character
		int i = input.read();
		loop: while (true) {
			if (i == -1) {
				throw new IOException(CONNECTION_LOST);
			}

			switch (i) {
			case PERCENTAGE_CHAR:
				if (curBuffer == sbKey) {
					// no percent encoding allowed in HTTP header field name
					throw new IllegalArgumentException(MALFORMED_HTTP_REQUEST);
				}
				/*
				 * RFC5987 is not implemented
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
					throw new IllegalArgumentException(MALFORMED_HTTP_REQUEST);
				}

				// end of header
				if ((sbKey.length() == 0) && (sbValue.length() == 0)) {
					// empty line : end of headers
					break loop;
				}

				// if next char is a white space, the header is not finished
				i = input.read();
				if (i == -1) {
					throw new IOException(CONNECTION_LOST);
				}

				if ((i == SPACE_CHAR) || (i == TABULATION_CHAR)) {
					// not the end of the header, loop
					continue loop;
				}

				/**
				 * if the key ends with asterisk "*", this means an RFC5987 encoded header value. Since the RFC5987 is
				 * not implemented, these kind of header fields are ignored
				 */
				if (sbKey.charAt(sbKey.length() - 1) != '*') {
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

		return header;
	}

	private static Map<String, String> parseCookies(String cookiesHeader) {
		Map<String, String> cookies = new HashMap<>();

		if (cookiesHeader == null) {
			return cookies;
		}

		int prev = 0;
		int next;

		while (prev != -1 && (next = cookiesHeader.indexOf('=', prev)) != -1) {
			String name = cookiesHeader.substring(prev, next);
			String value = cookiesHeader.substring(next + 1);
			cookies.put(name, value);
			prev = cookiesHeader.indexOf(';', next);
		}

		return cookies;
	}

}
