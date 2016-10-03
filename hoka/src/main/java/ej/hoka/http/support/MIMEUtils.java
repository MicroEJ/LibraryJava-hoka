/*
 * Java
 *
 * Copyright 2009-2016 IS2T. All rights reserved.
 * IS2T PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package ej.hoka.http.support;

import java.util.Hashtable;

/**
 * <p>
 * Utilities for handling the MIME-Type header field.
 * </p>
 */
public class MIMEUtils {

	/**
	 * <p>
	 * Standard text MIME type.
	 * </p>
	 */
	public static final String MIME_PLAINTEXT = "text/plain"; //$NON-NLS-1$

	/**
	 * <p>
	 * HTML code MIME type.
	 * </p>
	 */
	public static final String MIME_HTML = "text/html"; //$NON-NLS-1$

	// MIME types.

	/**
	 * <p>
	 * XML code MIME type.
	 * </p>
	 */
	public static final String MIME_XML = "text/xml"; //$NON-NLS-1$
	/**
	 * <p>
	 * Binary code MIME type.
	 * </p>
	 */
	public static final String MIME_DEFAULT_BINARY = "application/octet-stream"; //$NON-NLS-1$
	/**
	 * <p>
	 * CSS code MIME type.
	 * </p>
	 */
	public static final String MIME_CSS = "text/css"; //$NON-NLS-1$
	/**
	 * <p>
	 * PNG files MIME type.
	 * </p>
	 */
	public static final String MIME_PNG = "image/png"; //$NON-NLS-1$
	/**
	 * <p>
	 * JPEG files MIME type.
	 * </p>
	 */
	public static final String MIME_JPEG = "image/jpeg"; //$NON-NLS-1$
	/**
	 * <p>
	 * GIF files MIME type.
	 * </p>
	 */
	public static final String MIME_GIF = "image/gif"; //$NON-NLS-1$
	/**
	 * <p>
	 * JavaScript code MIME type.
	 * </p>
	 */
	public static final String MIME_JS = "application/x-javascript"; //$NON-NLS-1$
	/**
	 * <p>
	 * Form Encoded Data MIME type.
	 * </p>
	 */
	public static final String MIME_FORM_ENCODED_DATA = "application/x-www-form-urlencoded"; //$NON-NLS-1$
	/**
	 * <p>
	 * Multipart Form Encoded Data MIME type.
	 * </p>
	 */
	public static final String MIME_MULTIPART_FORM_ENCODED_DATA = "multipart/form-data"; //$NON-NLS-1$
	/**
	 * File extension mappings to MIME types.
	 */
	private static final Hashtable<String, String> FILE_EXTENSION_TO_MIME_TYPE;
	static {
		// fill well known MIME descriptors
		FILE_EXTENSION_TO_MIME_TYPE = new Hashtable<String, String>();

		mapFileExtensionToMIMEType(".png", MIME_PNG); //$NON-NLS-1$
		mapFileExtensionToMIMEType(".css", MIME_CSS); //$NON-NLS-1$
		mapFileExtensionToMIMEType(".gif", MIME_GIF); //$NON-NLS-1$
		mapFileExtensionToMIMEType(".jpeg", MIME_JPEG); //$NON-NLS-1$
		mapFileExtensionToMIMEType(".jpg", MIME_JPEG); //$NON-NLS-1$
		mapFileExtensionToMIMEType(".html", MIME_HTML); //$NON-NLS-1$
		mapFileExtensionToMIMEType(".htm", MIME_HTML); //$NON-NLS-1$
		mapFileExtensionToMIMEType(".js", MIME_JS); //$NON-NLS-1$
		mapFileExtensionToMIMEType(".txt", MIME_PLAINTEXT); //$NON-NLS-1$
		mapFileExtensionToMIMEType(".xml", MIME_XML); //$NON-NLS-1$
	}

	/**
	 * <p>
	 * Returns the MIME type from the name of the URI (file extension).
	 * </p>
	 *
	 * @param uri
	 *            the URI {@link String} to determine the MIME type (mapping between file extension and MIME type)
	 * @return <code>null</code> if the MIME type is unknown or URI is <code>null</code>
	 */
	public static String getMIMEType(String uri) {
		int pos;
		if ((uri == null) || ((pos = uri.indexOf('.')) == -1)) {
			return null;
		}

		return FILE_EXTENSION_TO_MIME_TYPE.get(uri.substring(pos));
	}

	/**
	 * <p>
	 * Define a mapping between a file extension and a MIME-Type.<br>
	 * The mapping is defined only if the given file extension isn't already mapped to a MIME-Type. A
	 * {@link NullPointerException} is thrown if one of the arguments is <code>null</code>
	 * </p>
	 *
	 * @param fileExtension
	 *            the file extension to be mapped to the <code>mimeType</code>
	 * @param mimeType
	 *            the MIME-Type to be associated with the given file extension.
	 * @return <code>true</code> if the mapping is successful, <code>false</code> otherwise (file extension already
	 *         mapped to a MIME-Type)
	 */
	public static boolean mapFileExtensionToMIMEType(String fileExtension, String mimeType) {
		if ((fileExtension == null) || (mimeType == null)) {
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

}
