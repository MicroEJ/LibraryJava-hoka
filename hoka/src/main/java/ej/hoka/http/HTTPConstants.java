/*
 * Java
 *
 * Copyright 2009-2016 IS2T. All rights reserved.
 * IS2T PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package ej.hoka.http;

/**
 * <p>
 * Constants for HTTP statuses and header fields.
 * </p>
 */
public interface HTTPConstants {

	/**
	 * <p>
	 * HTTP code 200: the response has been found and correctly sent.
	 * </p>
	 */
	String HTTP_STATUS_OK = "200 OK"; //$NON-NLS-1$
	/**
	 * <p>
	 * HTTP code 301: the requested URL redirected to another URL.
	 * </p>
	 */
	String HTTP_STATUS_REDIRECT = "301 Moved Permanently"; //$NON-NLS-1$
	/**
	 * <p>
	 * HTTP code 304: the requested resources hasn't been modified since the last time.
	 * </p>
	 */
	String HTTP_STATUS_NOTMODIFIED = "304 Not Modified"; //$NON-NLS-1$
	/**
	 * <p>
	 * HTTP code 403: the client doesn't have the permission to access the requested URL.
	 * </p>
	 */
	String HTTP_STATUS_FORBIDDEN = "403 Forbidden"; //$NON-NLS-1$
	/**
	 * <p>
	 * HTTP code 404: the requested URL has not been found.
	 * </p>
	 */
	String HTTP_STATUS_NOTFOUND = "404 Not Found"; //$NON-NLS-1$
	/**
	 * <p>
	 * HTTP code 405: the HTTP request method (GET/POST/PUT/DELETE) is not allowed on the server for the requested URI.
	 * </p>
	 */
	String HTTP_STATUS_METHOD = "405 Method Not Allowed"; //$NON-NLS-1$
	/**
	 * <p>
	 * HTTP code 406: the client cannot handle the data returned in the HTTP response.
	 * </p>
	 */
	String HTTP_STATUS_NOTACCEPTABLE = "406 Not Acceptable"; //$NON-NLS-1$
	/**
	 * <p>
	 * HTTP code 400: the request is not valid.
	 * </p>
	 */
	String HTTP_STATUS_BADREQUEST = "400 Bad Request"; //$NON-NLS-1$
	/**
	 * <p>
	 * HTTP code 415: the requested resource type is not supported.
	 * </p>
	 */
	String HTTP_STATUS_MEDIA_TYPE = "415 Unsupported Media Type"; //$NON-NLS-1$
	/**
	 * <p>
	 * HTTP code 500: the server has encountered an error while generating the response.
	 * </p>
	 *
	 */
	String HTTP_STATUS_INTERNALERROR = "500 Internal Server Error"; //$NON-NLS-1$
	/**
	 * <p>
	 * HTTP code 501: the HTTP request method is not implemented.
	 * </p>
	 */
	String HTTP_STATUS_NOTIMPLEMENTED = "501 Not Implemented"; //$NON-NLS-1$

	/**
	 * <p>
	 * HTTP <code>POST</code> method token as String.
	 * </p>
	 */
	String HTTP_METHOD_POST = "POST"; //$NON-NLS-1$

	/**
	 * <p>
	 * HTTP <code>GET</code> method token as String.
	 * </p>
	 */
	String HTTP_METHOD_GET = "GET"; //$NON-NLS-1$

	/**
	 * <p>
	 * HTTP <code>PUT</code> method token as String.
	 * </p>
	 */
	String HTTP_METHOD_PUT = "PUT"; //$NON-NLS-1$

	/**
	 * <p>
	 * HTTP <code>DELETE</code> method token as String.
	 * </p>
	 */
	String HTTP_METHOD_DELETE = "DELETE"; //$NON-NLS-1$

	// HTTP header fields

	/**
	 * <p>
	 * HTTP header field (in lower case) <code>content-type</code>.
	 * </p>
	 */
	String FIELD_CONTENT_TYPE = "content-type"; //$NON-NLS-1$

	/**
	 * <p>
	 * HTTP header field (in lower case) <code>content-encoding</code>.
	 * </p>
	 */
	String FIELD_CONTENT_ENCODING = "content-encoding"; //$NON-NLS-1$

	/**
	 * <p>
	 * HTTP header field (in lower case) <code>transfer-encoding</code>. See RFC HTTP/1.1 RFC2616 3.6.
	 * </p>
	 */
	String FIELD_TRANSFER_ENCODING = "transfer-encoding"; //$NON-NLS-1$

	/**
	 * <p>
	 * HTTP header field (in lower case) <code>content-encoding</code>.
	 * </p>
	 */
	String FIELD_ACCEPT_ENCODING = "accept-encoding"; //$NON-NLS-1$

	/**
	 * <p>
	 * HTTP header field (in lower case) <code>content-length</code>.
	 * </p>
	 */
	String FIELD_CONTENT_LENGTH = "content-length"; //$NON-NLS-1$

	/**
	 * <p>
	 * HTTP header field (in lower case) <code>if-none-match</code>.
	 * </p>
	 */
	String FIELD_IF_NONE_MATCH = "if-none-match"; //$NON-NLS-1$

	/**
	 * <p>
	 * HTTP header field (in lower case) <code>connection</code>.
	 * </p>
	 */
	String FIELD_CONNECTION = "connection"; //$NON-NLS-1$

	/**
	 * <p>
	 * Value for HTTP header field "Connection" (<code>keep-alive</code>).
	 * </p>
	 */
	String CONNECTION_FIELD_VALUE_KEEP_ALIVE = "keep-alive"; //$NON-NLS-1$

	/**
	 * <p>
	 * Value for HTTP header field "Connection" (<code>close</code>).
	 * </p>
	 */
	String CONNECTION_FIELD_VALUE_CLOSE = "close"; //$NON-NLS-1$

}
