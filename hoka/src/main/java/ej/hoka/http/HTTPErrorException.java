/*
 * Java
 *
 * Copyright 2019 MicroEJ Corp. All rights reserved.
 * This library is provided in source code for use, modification and test, subject to license terms.
 * Any modification of the source code will break MicroEJ Corp. warranties on the whole library.
 */
package ej.hoka.http;

public class HTTPErrorException extends Exception {

	private final String status;
	private final String message;

	public HTTPErrorException(String status) {
		this(status, null);
	}

	public HTTPErrorException(String status, String message) {
		this.status = status;
		this.message = message;
	}

	public String getStatus() {
		return this.status;
	}

	@Override
	public String getMessage() {
		if (this.message == null) {
			return "";
		}
		return this.message;
	}

}
