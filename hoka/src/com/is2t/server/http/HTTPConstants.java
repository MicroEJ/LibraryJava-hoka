/**
 * Java
 *
 * Copyright 2009-2013 IS2T. All rights reserved.
 * IS2T PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.is2t.server.http;

/**
 * IS2T-API
 * <p>
 * Constants for HTTP statuses and header fields.
 * </p>
 */
public interface HTTPConstants {

	// HTTP error codes.

	/**
	 * IS2T-API
	 * <p>
	 * HTTP code 200: the response has been found and correctly sent.
	 * </p>
	 */
	public static final String HTTP_STATUS_OK = "200 OK";
	/**
	 * IS2T-API
	 * <p>
	 * HTTP code 301: the requested URL redirected to another URL.
	 * </p>
	 */
	public static final String HTTP_STATUS_REDIRECT = "301 Moved Permanently";
	/**
	 * IS2T-API
	 * <p>
	 * HTTP code 304: the requested resources hasn't been modified since the
	 * last time.
	 * </p>
	 */
	public static final String HTTP_STATUS_NOTMODIFIED = "304 Not Modified";
	/**
	 * IS2T-API
	 * <p>
	 * HTTP code 403: the client doesn't have the permission to access the
	 * requested URL.
	 * </p>
	 */
	public static final String HTTP_STATUS_FORBIDDEN = "403 Forbidden";
	/**
	 * IS2T-API
	 * <p>
	 * HTTP code 404: the requested URL has not been found.
	 * </p>
	 */
	public static final String HTTP_STATUS_NOTFOUND = "404 Not Found";
	/**
	 * IS2T-API
	 * <p>
	 * HTTP code 405: the HTTP request method (GET/POST/PUT/DELETE) is not
	 * allowed on the server for the requested URI.
	 * </p>
	 */
	public static final String HTTP_STATUS_METHOD = "405 Method Not Allowed";
	/**
	 * IS2T-API
	 * <p>
	 * HTTP code 406: the client cannot handle the data returned in the HTTP
	 * response.
	 * </p>
	 */
	public static final String HTTP_STATUS_NOTACCEPTABLE = "406 Not Acceptable";
	/**
	 * IS2T-API
	 * <p>
	 * HTTP code 400: the request is not valid.
	 * </p>
	 */
	public static final String HTTP_STATUS_BADREQUEST = "400 Bad Request";
	/**
	 * IS2T-API
	 * <p>
	 * HTTP code 415: the requested resource type is not supported.
	 * </p>
	 */
	public static final String HTTP_STATUS_MEDIA_TYPE = "415 Unsupported Media Type";
	/**
	 * IS2T-API
	 * <p>
	 * HTTP code 500: the server has encountered an error while generating the
	 * response.
	 * </p>
	 * 
	 */
	public static final String HTTP_STATUS_INTERNALERROR = "500 Internal Server Error";
	/**
	 * IS2T-API
	 * <p>
	 * HTTP code 501: the HTTP request method is not implemented.
	 * </p>
	 */
	public static final String HTTP_STATUS_NOTIMPLEMENTED = "501 Not Implemented";

	/**
	 * IS2T-API
	 * <p>
	 * HTTP <code>POST</code> method token as String.
	 * </p>
	 */
	public static final String HTTP_METHOD_POST = "POST";

	/**
	 * IS2T-API
	 * <p>
	 * HTTP <code>GET</code> method token as String.
	 * </p>
	 */
	public static final String HTTP_METHOD_GET = "GET";

	/**
	 * IS2T-API
	 * <p>
	 * HTTP <code>PUT</code> method token as String.
	 * </p>
	 */
	public static final String HTTP_METHOD_PUT = "PUT";

	/**
	 * IS2T-API
	 * <p>
	 * HTTP <code>DELETE</code> method token as String.
	 * </p>
	 */
	public static final String HTTP_METHOD_DELETE = "DELETE";

	// HTTP header fields

	/**
	 * IS2T-API
	 * <p>
	 * HTTP header field (in lower case) <code>content-type</code>.
	 * </p>
	 */
	public static final String FIELD_CONTENT_TYPE = "content-type";

	/**
	 * IS2T-API
	 * <p>
	 * HTTP header field (in lower case) <code>content-encoding</code>.
	 * </p>
	 */
	public static final String FIELD_CONTENT_ENCODING = "content-encoding";

	/**
	 * IS2T-API
	 * <p>
	 * HTTP header field (in lower case) <code>transfer-encoding</code>. See RFC
	 * HTTP/1.1 RFC2616 3.6.
	 * </p>
	 */
	public static final String FIELD_TRANSFER_ENCODING = "transfer-encoding";

	/**
	 * IS2T-API
	 * <p>
	 * HTTP header field (in lower case) <code>content-encoding</code>.
	 * </p>
	 */
	public static final String FIELD_ACCEPT_ENCODING = "accept-encoding";

	/**
	 * IS2T-API
	 * <p>
	 * HTTP header field (in lower case) <code>content-length</code>.
	 * </p>
	 */
	public static final String FIELD_CONTENT_LENGTH = "content-length";

	/**
	 * IS2T-API
	 * <p>
	 * HTTP header field (in lower case) <code>if-none-match</code>.
	 * </p>
	 */
	public static final String FIELD_IF_NONE_MATCH = "if-none-match";

	/**
	 * IS2T-API
	 * <p>
	 * HTTP header field (in lower case) <code>connection</code>.
	 * </p>
	 */
	public static final String FIELD_CONNECTION = "connection";

	/**
	 * IS2T-API
	 * <p>
	 * Value for HTTP header field "Connection" (<code>keep-alive</code>).
	 * </p>
	 */
	public static final String CONNECTION_FIELD_VALUE_KEEP_ALIVE = "keep-alive";

	/**
	 * IS2T-API
	 * <p>
	 * Value for HTTP header field "Connection" (<code>close</code>).
	 * </p>
	 */
	public static final String CONNECTION_FIELD_VALUE_CLOSE = "close";

}
