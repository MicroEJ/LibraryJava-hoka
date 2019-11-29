/*
 * Java
 *
 * Copyright 2009-2019 MicroEJ Corp. All rights reserved.
 * This library is provided in source code for use, modification and test, subject to license terms.
 * Any modification of the source code will break MicroEJ Corp. warranties on the whole library.
 */
package ej.hoka.http;

/**
 * Constants for HTTP statuses, methods and header fields.
 */
public final class HTTPConstants {

	/**
	 * HTTP code 200: the response has been found and correctly sent.
	 */
	public static final String HTTP_STATUS_OK = "200 OK"; //$NON-NLS-1$
	/**
	 * HTTP code 301: the requested URL redirected to another URL.
	 */
	public static final String HTTP_STATUS_REDIRECT = "301 Moved Permanently"; //$NON-NLS-1$
	/**
	 * HTTP code 304: the requested resources hasn't been modified since the last time.
	 */
	public static final String HTTP_STATUS_NOTMODIFIED = "304 Not Modified"; //$NON-NLS-1$
	/**
	 * HTTP code 400: the request is not valid.
	 */
	public static final String HTTP_STATUS_BADREQUEST = "400 Bad Request"; //$NON-NLS-1$
	/**
	 * HTTP code 401: the requested resources require valid authentication credentials.
	 */
	public static final String HTTP_STATUS_UNAUTHORIZED = "401 Unauthorized"; //$NON-NLS-1$
	/**
	 * HTTP code 403: the client doesn't have the permission to access the requested URL.
	 */
	public static final String HTTP_STATUS_FORBIDDEN = "403 Forbidden"; //$NON-NLS-1$
	/**
	 * HTTP code 404: the requested URL has not been found.
	 */
	public static final String HTTP_STATUS_NOTFOUND = "404 Not Found"; //$NON-NLS-1$
	/**
	 * HTTP code 405: the HTTP request method (GET/POST/PUT/DELETE) is not allowed on the server for the requested URI.
	 */
	public static final String HTTP_STATUS_METHOD = "405 Method Not Allowed"; //$NON-NLS-1$
	/**
	 * HTTP code 406: the client cannot handle the data returned in the HTTP response.
	 */
	public static final String HTTP_STATUS_NOTACCEPTABLE = "406 Not Acceptable"; //$NON-NLS-1$
	/**
	 * HTTP code 408: the client initiated the connection but didn't send the request (idle connection).
	 */
	public static final String HTTP_STATUS_REQUESTTIMEOUT = "408 Request Timeout"; //$NON-NLS-1$
	/**
	 * HTTP code 415: the requested resource type is not supported.
	 */
	public static final String HTTP_STATUS_MEDIA_TYPE = "415 Unsupported Media Type"; //$NON-NLS-1$
	/**
	 * HTTP code 500: the server has encountered an error while generating the response.
	 */
	public static final String HTTP_STATUS_INTERNALERROR = "500 Internal Server Error"; //$NON-NLS-1$
	/**
	 * HTTP code 501: the HTTP request method is not implemented.
	 */
	public static final String HTTP_STATUS_NOTIMPLEMENTED = "501 Not Implemented"; //$NON-NLS-1$

	/**
	 * HTTP <code>POST</code> method token as String.
	 */
	public static final String HTTP_METHOD_POST = "POST"; //$NON-NLS-1$
	/**
	 * HTTP <code>GET</code> method token as String.
	 */
	public static final String HTTP_METHOD_GET = "GET"; //$NON-NLS-1$
	/**
	 * HTTP <code>PUT</code> method token as String.
	 */
	public static final String HTTP_METHOD_PUT = "PUT"; //$NON-NLS-1$
	/**
	 * HTTP <code>DELETE</code> method token as String.
	 */
	public static final String HTTP_METHOD_DELETE = "DELETE"; //$NON-NLS-1$

	private static final String ENCODING = "encoding"; //$NON-NLS-1$
	private static final String CONTENT = "content-"; //$NON-NLS-1$

	/**
	 * HTTP header field (in lower case) <code>content-type</code>.
	 */
	public static final String FIELD_CONTENT_TYPE = CONTENT + "type"; //$NON-NLS-1$
	/**
	 * HTTP header field (in lower case) <code>content-encoding</code>.
	 */
	public static final String FIELD_CONTENT_ENCODING = CONTENT + ENCODING;
	/**
	 * HTTP header field (in lower case) <code>transfer-encoding</code>. See RFC HTTP/1.1 RFC2616 3.6.
	 */
	public static final String FIELD_TRANSFER_ENCODING = "transfer-" + ENCODING; //$NON-NLS-1$
	/**
	 * HTTP header field (in lower case) <code>content-encoding</code>.
	 */
	public static final String FIELD_ACCEPT_ENCODING = "accept-" + ENCODING; //$NON-NLS-1$
	/**
	 * HTTP header field (in lower case) <code>content-length</code>.
	 */
	public static final String FIELD_CONTENT_LENGTH = CONTENT + "length"; //$NON-NLS-1$
	/**
	 * HTTP header field (in lower case) <code>if-none-match</code>.
	 */
	public static final String FIELD_IF_NONE_MATCH = "if-none-match"; //$NON-NLS-1$
	/**
	 * HTTP header field (in lower case) <code>connection</code>.
	 */
	public static final String FIELD_CONNECTION = "connection"; //$NON-NLS-1$
	/**
	 * Value for HTTP header field "Connection" (<code>keep-alive</code>).
	 */
	public static final String FIELD_CONNECTION_VALUE_KEEP_ALIVE = "keep-alive"; //$NON-NLS-1$
	/**
	 * Value for HTTP header field "Connection" (<code>close</code>).
	 */
	public static final String FIELD_CONNECTION_VALUE_CLOSE = "close"; //$NON-NLS-1$
	/**
	 * HTTP header field (in lower case) <code>cookie</code>.
	 */
	public static final String FIELD_COOKIES = "cookie"; //$NON-NLS-1$

	/**
	 * The "end of line" CR + LF.
	 */
	public static final String END_OF_LINE = "\r\n"; //$NON-NLS-1$

	private HTTPConstants() {
		// Forbid instantiation.
	}

}
