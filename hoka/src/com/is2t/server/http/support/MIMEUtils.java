/**
 * Java
 *
 * Copyright 2009-2015 IS2T. All rights reserved.
 * IS2T PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.is2t.server.http.support;

import java.util.Hashtable;

/**
 * IS2T-API
 * <p>
 * Utilities for handling the MIME-Type header field.
 * </p>
 */
public class MIMEUtils {

	/**
	 * IS2T-API
	 * <p>
	 * Define a mapping between a file extension and a MIME-Type.<br>
	 * The mapping is defined only if the given file extension isn't already
	 * mapped to a MIME-Type. A {@link NullPointerException} is thrown if one of the
	 * arguments is <code>null</code>
	 * </p>
	 * 
	 * @param fileExtension
	 *            the file extension to be mapped to the <code>mimeType</code>
	 * @param mimeType
	 *            the MIME-Type to be associated with the given file extension.
	 * @return <code>true</code> if the mapping is successful,
	 *         <code>false</code> otherwise (file extension already mapped to a
	 *         MIME-Type)
	 */
	public static boolean mapFileExtensionToMIMEType(String fileExtension,
			String mimeType) {
		if (fileExtension == null || mimeType == null) {
			throw new NullPointerException();
		}
		if (fileExtension.charAt(0) != '.') {
			fileExtension = '.' + fileExtension;
		}
		if (FILE_EXTENSION_TO_MIME_TYPE.get(fileExtension) == null) {
			FILE_EXTENSION_TO_MIME_TYPE.put(fileExtension, mimeType);
			return true;
		}
		return false;
	}

	/**
	 * IS2T-API
	 * <p>
	 * Returns the MIME type from the name of the URI (file extension).
	 * </p>
	 * 
	 * @param uri
	 *            the URI {@link String} to determine the MIME type (mapping
	 *            between file extension and MIME type)
	 * @return <code>null</code> if the MIME type is unknown or URI is
	 *         <code>null</code>
	 */
	public static String getMIMEType(String uri) {
		int pos;
		if (uri == null || (pos = uri.indexOf('.')) == -1) {
			return null;
		}

		return (String) FILE_EXTENSION_TO_MIME_TYPE.get(uri.substring(pos));
	}

	// MIME types.

	/**
	 * IS2T-API
	 * <p>
	 * Standard text MIME type.
	 * </p>
	 */
	public static final String MIME_PLAINTEXT = "text/plain";
	/**
	 * IS2T-API
	 * <p>
	 * HTML code MIME type.
	 * </p>
	 */
	public static final String MIME_HTML = "text/html";
	/**
	 * IS2T-API
	 * <p>
	 * XML code MIME type.
	 * </p>
	 */
	public static final String MIME_XML = "text/xml";
	/**
	 * IS2T-API
	 * <p>
	 * Binary code MIME type.
	 * </p>
	 */
	public static final String MIME_DEFAULT_BINARY = "application/octet-stream";
	/**
	 * IS2T-API
	 * <p>
	 * CSS code MIME type.
	 * </p>
	 */
	public static final String MIME_CSS = "text/css";
	/**
	 * IS2T-API
	 * <p>
	 * PNG files MIME type.
	 * </p>
	 */
	public static final String MIME_PNG = "image/png";
	/**
	 * IS2T-API
	 * <p>
	 * JPEG files MIME type.
	 * </p>
	 */
	public static final String MIME_JPEG = "image/jpeg";
	/**
	 * IS2T-API
	 * <p>
	 * GIF files MIME type.
	 * </p>
	 */
	public static final String MIME_GIF = "image/gif";
	/**
	 * IS2T-API
	 * <p>
	 * JavaScript code MIME type.
	 * </p>
	 */
	public static final String MIME_JS = "application/x-javascript";
	/**
	 * IS2T-API
	 * <p>
	 * Form Encoded Data MIME type.
	 * </p>
	 */
	public static final String MIME_FORM_ENCODED_DATA = "application/x-www-form-urlencoded";
	/**
	 * IS2T-API
	 * <p>
	 * Multipart Form Encoded Data MIME type.
	 * </p>
	 */
	public static final String MIME_MULTIPART_FORM_ENCODED_DATA = "multipart/form-data";

	/************************************************************************************
	 * NOT IN API
	 ***********************************************************************************/

	/**
	 * File extension mappings to MIME types.
	 */
	private static final Hashtable FILE_EXTENSION_TO_MIME_TYPE;

	static {
		// fill well known MIME descriptors
		FILE_EXTENSION_TO_MIME_TYPE = new Hashtable();

		mapFileExtensionToMIMEType(".png", MIME_PNG);
		mapFileExtensionToMIMEType(".css", MIME_CSS);
		mapFileExtensionToMIMEType(".gif", MIME_GIF);
		mapFileExtensionToMIMEType(".jpeg", MIME_JPEG);
		mapFileExtensionToMIMEType(".jpg", MIME_JPEG);
		mapFileExtensionToMIMEType(".html", MIME_HTML);
		mapFileExtensionToMIMEType(".htm", MIME_HTML);
		mapFileExtensionToMIMEType(".js", MIME_JS);
		mapFileExtensionToMIMEType(".txt", MIME_PLAINTEXT);
		mapFileExtensionToMIMEType(".xml", MIME_XML);
	}

}
