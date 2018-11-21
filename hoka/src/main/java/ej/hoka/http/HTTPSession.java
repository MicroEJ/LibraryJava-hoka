/*
 * Java
 *
 * Copyright 2009-2018 IS2T. All rights reserved.
 * IS2T PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package ej.hoka.http;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Map;
import java.util.Map.Entry;

import ej.hoka.http.body.BodyParserFactory;
import ej.hoka.http.support.AcceptEncoding;
import ej.hoka.http.support.MIMEUtils;
import ej.hoka.http.support.QualityArgument;
import ej.hoka.log.Logger;

/**
 * <p>
 * Abstract HTTP Session. Subclasses implements the {@link HTTPSession#answer(HTTPRequest)} method to generate
 * {@link HTTPResponse} to a {@link HTTPRequest}.
 * </p>
 */
public abstract class HTTPSession {

	/**
	 * This size is used for the request and answer buffer size (two buffers will be created).
	 */
	private static final int BUFFER_SIZE = 2048;

	/**
	 * Property to set a custom buffer size.
	 */
	private static final String BUFFER_SIZE_PROPERTY = "hoka.buffer.size";

	/**
	 * The HTTP/1.1 version String.
	 */
	private static final String RESPONSE_HTTP11 = "HTTP/1.1 "; //$NON-NLS-1$

	/**
	 * The colon character.
	 */
	private static final String RESPONSE_COLON = ": "; //$NON-NLS-1$

	/**
	 * The "end of header" CR + LF.
	 */
	private static final String RESPONSE_ENDOFHEADER = "\r\n"; //$NON-NLS-1$

	/**
	 * The Content-Type: String.
	 */
	private static final String RESPONSE_CONTENTTYPE = "Content-Type" + RESPONSE_COLON; //$NON-NLS-1$

	/**
	 * {@link HTTPServer} instance.
	 */
	private final HTTPServer server;

	/**
	 * The {@link SocketConnection} instance.
	 */
	private Socket streamConnection;

	private BodyParserFactory bodyParserFactory;

	/**
	 * <p>
	 * Creates a new HTTP Session in the given {@link HTTPServer}.
	 * </p>
	 *
	 * @param server
	 *            a {@link HTTPServer}
	 */
	public HTTPSession(HTTPServer server) {
		this.server = server;
	}

	/**
	 * Create a {@link HTTPResponse} to write the <code>msg</code> for the given <code>status</code>.
	 *
	 * @param status
	 *            the error status. One of <code>HTTP_STATUS_*</code> constant of the {@link HTTPConstants} interface.
	 * @param msg
	 *            an optional error message to add in response.
	 * @return a {@link HTTPResponse} that represent the error.
	 * @see HTTPConstants#HTTP_STATUS_BADREQUEST
	 * @see HTTPConstants#HTTP_STATUS_FORBIDDEN
	 * @see HTTPConstants#HTTP_STATUS_INTERNALERROR
	 * @see HTTPConstants#HTTP_STATUS_MEDIA_TYPE
	 * @see HTTPConstants#HTTP_STATUS_METHOD
	 * @see HTTPConstants#HTTP_STATUS_NOTACCEPTABLE
	 * @see HTTPConstants#HTTP_STATUS_NOTFOUND
	 * @see HTTPConstants#HTTP_STATUS_NOTIMPLEMENTED
	 * @see HTTPConstants#HTTP_STATUS_NOTMODIFIED
	 * @see HTTPConstants#HTTP_STATUS_OK
	 * @see HTTPConstants#HTTP_STATUS_REDIRECT
	 */
	public static HTTPResponse createErrorResponse(String status, String msg) {
		StringBuilder buffer = new StringBuilder();
		buffer.append("<html><head><title>"); //$NON-NLS-1$
		buffer.append(status);
		buffer.append("</title></head><body><h1>"); //$NON-NLS-1$
		buffer.append(status);
		buffer.append("</h1><p>"); //$NON-NLS-1$
		buffer.append(msg);
		buffer.append("</p></body></html>"); //$NON-NLS-1$

		HTTPResponse response = new HTTPResponse(buffer.toString());
		response.setMimeType(MIMEUtils.MIME_HTML);
		response.setStatus(status);
		return response;
	}

	/**
	 * <p>
	 * Generates {@link HTTPResponse} to a {@link HTTPRequest}. Subclasses implements this method to add functionality
	 * to the HTTP Server.
	 * </p>
	 *
	 * @param request
	 *            the request
	 * @return a {@link HTTPResponse}
	 */
	protected abstract HTTPResponse answer(HTTPRequest request);

	/**
	 * Returns true if the {@link HTTPResponse} contains a "HTTP 200 OK" response code.
	 *
	 * @param response
	 *            the {@link HTTPResponse}
	 * @return true if the {@link HTTPResponse} contains a "HTTP 200 OK" response code.
	 */
	protected boolean checkHTTPError(HTTPResponse response) {
		// TODO check other erroneous statuses
		return response.getStatus() != HTTPConstants.HTTP_STATUS_OK;
	}

	/**
	 * Close the current server connection.
	 */
	protected void closeConnection() {
		// closing connection
		try {
			this.streamConnection.close();
		} catch (Throwable t) {
			// not a fatal error
		}
		// free resources
		this.streamConnection = null;
	}

	/**
	 * Returns the most suitable {@link IHTTPEncodingHandler} to match the encodings described in
	 * <code>Accept-Encoding</code> header.
	 *
	 * @param encodingParam
	 *            is on the form <code>gzip, identity</code> or <code>gzip; q=0.8, identity; q=0.2</code>
	 * @return the {@link IHTTPEncodingHandler}, or <code>null</code> if no suitable handler can be found
	 */
	protected IHTTPEncodingHandler getAcceptEncodingHandler(String encodingParam) {

		AcceptEncoding acceptEncoding = new AcceptEncoding();
		acceptEncoding.parse(encodingParam);

		// Try to return the most acceptable handler
		QualityArgument[] encodings = acceptEncoding.getEncodings();
		int nbEncodings = encodings.length;
		boolean[] processed = new boolean[nbEncodings];
		for (int pass = nbEncodings; --pass >= 0;) { // maximum number of passes
			float localMax = 0;
			int ptrMax = -1;
			for (int i = nbEncodings; --i >= 0;) {
				if (processed[i]) {
					continue;
				}
				QualityArgument arg = encodings[i];
				float qvalue = arg.getQuality();
				if (qvalue > localMax) {
					localMax = qvalue;
					ptrMax = i;
				}
			}
			processed[ptrMax] = true;

			// Try to get the handler
			IHTTPEncodingHandler handler = this.server.getEncodingHandler(encodings[ptrMax].getArgument());
			if (handler != null) {
				return handler;
			}
		}

		return null;
	}

	/**
	 * Runs the HTTPServer by waiting for incoming connections and build the response.
	 *
	 * @return the {@link Runnable} interface for core HTTP Session functionality.
	 */
	protected Runnable getRunnable() {
		return new Runnable() {
			@Override
			public void run() {
				runloop: while (true) { // loop and process open connections
					Logger logger = HTTPSession.this.server.getLogger(); // never null, at least
					// NullLogger

					Socket streamConnection = HTTPSession.this.server.getNextStreamConnection();
					setCurrentConnection(streamConnection);

					if (streamConnection == null) {
						// server stopped
						return;
					}

					logger.processConnection(streamConnection);

					HTTPResponse response = null;
					try (InputStream inputStream = new BufferedInputStream(streamConnection.getInputStream(),
							getBufferSize())) {

						// TODO handling persistent connection
						// long connectionStartTime = -server.keepAliveDuration;
						// do{
						// // if input stream contains some data, a new request
						// has been sent
						// if(is.available() > 0){

						HTTPRequest request;
						try {
							request = new HTTPRequest(HTTPSession.this.server, inputStream, getBodyParserFactory());
						} catch (IllegalArgumentException e) {
							e.printStackTrace();
							sendError(HTTPConstants.HTTP_STATUS_BADREQUEST);
							continue runloop;
						} catch (UnsupportedHTTPEncodingException e) {
							e.printStackTrace();
							// unable to decode data
							// RFC2616 / 3.6: A server which receives ... a
							// transfer-coding
							// it does not understand SHOULD return 501
							// (Unimplemented),
							// and close the connection.
							sendError(HTTPConstants.HTTP_STATUS_NOTIMPLEMENTED, e.field + RESPONSE_COLON + e.encoding);
							continue runloop;
						}

						try {
							// Build response or return a 304 status given to
							// the
							// "If-None-Match" field presence in the request.
							String etag = request.getHeaderField(HTTPConstants.FIELD_IF_NONE_MATCH);
							if (etag == null) {
								// do the answer to get back the response
								// depending on the URI called
								try {
									response = answer(request);
								} catch (Throwable e) {
									// An unexpected error occurred
									logger.unexpectedError(e);
									response = null; // ensures next error
									// handling is done
									// fall down
								}

								// both when answer return null and an
								// unexpected error occur
								if (response == null) {
									sendError(HTTPConstants.HTTP_STATUS_INTERNALERROR);
									continue runloop;
								}
							} else {
								response = new HTTPResponse();
								response.setStatus(HTTPConstants.HTTP_STATUS_NOTMODIFIED);
							}
						} finally {
							request.finish();
						}

						// TODO handling persistent connection
						// // manage keep-alive connections: if a request is
						// sent with a keep-alive parameters
						// // then set or reset the keep-alive time.
						// HTTPHeader connection =
						// request.getHeader(CONNECTION_FIELD);
						// if(connection != null){
						// String value = connection.value.toLowerCase();
						// if(value.equals(KEEP_ALIVE_CONNECTION_FIELD_VALUE)
						// &&
						// !CLOSE_CONNECTION_FIELD_VALUE.equals(r.header.get(CONNECTION_FIELD))){
						// connectionStartTime = System.currentTimeMillis();
						// r.addHeader(CONNECTION_FIELD,
						// KEEP_ALIVE_CONNECTION_FIELD_VALUE);
						// }
						// }

						// Send the response
						String encoding = request.getHeaderField(HTTPConstants.FIELD_ACCEPT_ENCODING);
						IHTTPEncodingHandler handler = null;
						// if the Accept-Encoding header is not found, skip
						// finding the handler
						if (encoding != null) {
							handler = getAcceptEncodingHandler(encoding);
						}
						if (handler == null) {
							if (CalibrationConstants.STRICT_ACCEPT_ENCODING_COMPLIANCE) {
								// RFC2616 14.3
								sendError(HTTPConstants.HTTP_STATUS_NOTACCEPTABLE);
								continue runloop;
							} /*
								 * else { // continue with no encoding (null handler == // identity) // Example: Firefox
								 * 3.6 asks for // Accept-Encoding=gzip,deflate by default. // If none of these
								 * encodings if present on this // embedded server, send without encoding // instead of
								 * Error 406. This avoid to modify // default options (about:config => //
								 * network.http.accept-encoding=gzip,deflate,identity }
								 */
						}

						// FIXME do not only log erroneous responses, log any
						// processed request (maybe according to an error level
						// in logger)
						if (checkHTTPError(response)) {
							logger.httpError(streamConnection, response.getStatus(), request.getURI());
						}

						sendResponse(response, handler);

						// } // TODO handling persistent connection
						// }
						// // if the keep alive time is not elapsed then wait
						// for next request
						// while(System.currentTimeMillis()-connectionStartTime
						// < server.keepAliveDuration);

					} catch (IOException e) {
						// connection lost
						logger.connectionLost(streamConnection, e);
						continue runloop;
					} finally {
						// close response if needed (only if response has
						// not been sent correctly).
						if (response != null) {
							response.close();
						}

						// FIXME keep a generic "closeConnection" behaviour and
						// let implementation do their own implementation
						// on associated streams

						closeConnection();

						logger.connectionClosed(streamConnection);
					}
				}
			}
		};
	}

	/**
	 * Send a HTTP error code with given status.
	 *
	 * @param status
	 *            the HTTP status
	 * @see #sendError(String, String)
	 */
	protected void sendError(String status) {
		sendError(status, null);
	}

	/*
	 * Content for building the answer
	 */

	/**
	 * Sends a HTTP response with given status and optional message. The information is also logged using
	 * {@link Logger#httpError(Socket, String, String)}
	 *
	 * @param msg
	 *            the message, could be <code>null</code>
	 *
	 * @param status
	 *            the error status
	 * @see #sendResponse(HTTPResponse, IHTTPEncodingHandler)
	 */
	protected void sendError(String status, String msg) {
		Logger logger = this.server.getLogger(); // never null, at least NullLogger
		logger.httpError(this.streamConnection, status, msg);

		sendResponse(createErrorResponse(status, msg), (IHTTPEncodingHandler) null);
	}

	/**
	 * Sends the given {@link HTTPResponse} to the previously initialized {@link Socket}.
	 *
	 * @param response
	 *            the {@link HTTPResponse} to be sent
	 * @param encodingHandler
	 *            the encoding handler to be used to encode the response. If <code>null</code> the
	 *            {@link IdentityEncodingHandler} is used to encode the response.
	 */
	protected void sendResponse(HTTPResponse response, IHTTPEncodingHandler encodingHandler) {
		try {
			if (encodingHandler != null) {
				response.addHeaderField(HTTPConstants.FIELD_CONTENT_ENCODING, encodingHandler.getId());
			}
			try (OutputStream out = this.streamConnection.getOutputStream()) {
				writeResponse(response, encodingHandler, out);
			}
		} catch (IOException e) {
			// an error occurred when sending the response: can't do anything
			// more
			this.server.getLogger().unexpectedError(e);
		}
	}

	/**
	 * Sets the {@link Socket} to be used.
	 *
	 * @param c
	 *            the {@link Socket} to be used.
	 */
	protected void setCurrentConnection(Socket c) {
		this.streamConnection = c;
	}

	/**
	 * Writes the HTTP Header using the {@link HTTPResponse} <code>response</code> and the {@link OutputStream}
	 * <code>output</code>.
	 *
	 * @param response
	 *            the {@link HTTPResponse}
	 * @param output
	 *            {@link OutputStream}
	 * @throws IOException
	 *             when the connection is lost
	 */
	private void writeHTTPHeader(HTTPResponse response, OutputStream output) throws IOException {
		final byte[] eofHeader = RESPONSE_ENDOFHEADER.getBytes();

		output.write(RESPONSE_HTTP11.getBytes());
		output.write(response.getStatus().getBytes());
		output.write(' ');
		output.write(eofHeader);

		String mimeType = response.getMimeType();
		if (mimeType != null) {
			output.write(RESPONSE_CONTENTTYPE.getBytes());
			output.write(mimeType.getBytes());
			output.write(eofHeader);
		}

		// add header parameters
		Map<String, String> header = response.getHeader();
		for (Entry<String, String> entry : header.entrySet()) {
			String key = entry.getKey();
			String value = entry.getValue();
			output.write(key.getBytes());
			output.write(RESPONSE_COLON.getBytes());
			output.write(value.getBytes());
			output.write(eofHeader);
		}

		output.write(eofHeader);
	}

	/*
	 * private static final String RESPONSE_CONTENTLENGTH = "Content-Length" + RESPONSE_COLON; private static final
	 * String RESPONSE_HOST = "Host" + RESPONSE_COLON;
	 */

	/**
	 * Send the {@link HTTPResponse} to the given {@link OutputStream} using the given {@link IHTTPEncodingHandler}.
	 *
	 * @param response
	 *            the {@link HTTPResponse}
	 * @param encodingHandler
	 *            the {@link IHTTPEncodingHandler} to encode the response. If <code>null</code>, the
	 *            {@link IHTTPTransferCodingHandler} is used.
	 * @param output
	 *            {@link OutputStream} used to write the response to
	 * @throws IOException
	 *             if the connection has been lost
	 */
	protected void writeResponse(HTTPResponse response, IHTTPEncodingHandler encodingHandler, OutputStream output)
			throws IOException {
		// only one of the next data can be defined.
		// A better way may be to specialize HTTPResponse for Raw String and
		// InputStream
		// and makes theses classes "visitable" by a HTTPWriter which is able to
		// visit both Raw String and InputStream HTTP Response
		// we keep this implementation to avoid new hierarchy for performance
		// but if the specialization evolves to a more and more
		// specific way, do it!
		byte[] rawData = response.getRawData();
		InputStream dataStream = response.getData();
		long length = response.getLength();

		if (length < 0) {
			// data will be transmitted using chunked transfer coding
			// only when dataStream is used, the size is known otherwise
			response.addHeaderField(HTTPConstants.FIELD_TRANSFER_ENCODING,
					this.server.getChunkedTransferCodingHandler().getId());
		} // else the length is already defined in a header by the response

		writeHTTPHeader(response, output);

		if (rawData != null) {
			try (OutputStream dataOutput = this.server.getIdentityTransferCodingHandler().open(response, output)) {
				if (encodingHandler != null) {
					try (OutputStream encodedDataOutput = encodingHandler.open(dataOutput)) {
						writeAndFlush(rawData, encodedDataOutput);
					}
				} else {
					writeAndFlush(rawData, dataOutput);
				}
				response.setDataStreamClosed();
			}
		} else if (dataStream != null) {
			try {
				OutputStream dataOutput;
				if (length == -1) {
					dataOutput = this.server.getChunkedTransferCodingHandler().open(response, output);
				} else {
					dataOutput = this.server.getIdentityTransferCodingHandler().open(response, output);
				}
				try {
					if (encodingHandler != null) {
						dataOutput = encodingHandler.open(dataOutput);
					}
					final byte[] readBuffer = new byte[getBufferSize()];
					while (true) {
						int len = dataStream.read(readBuffer);

						if (len < 0) { // read until EOF is reached
							break;
						}
						// store read data
						dataOutput.write(readBuffer, 0, len);
						dataOutput.flush();
					}
				} catch (Throwable t) {
					this.server.getLogger().unexpectedError(t);
				} finally {
					// close data output stream. This does not close underlying
					// TCP connection since transfer output stream does not
					// close its underlying output stream
					dataOutput.close();
					response.setDataStreamClosed();
				}
			} finally {
				dataStream.close();
			}
		}
		output.flush();
	}

	private void writeAndFlush(byte[] data, OutputStream stream) throws IOException {
		stream.write(data);
		stream.flush();
		stream.close();
	}

	/**
	 * Gets the bodyParserFactory.
	 *
	 * @return the bodyParserFactory.
	 */
	public BodyParserFactory getBodyParserFactory() {
		return this.bodyParserFactory;
	}

	/**
	 * Sets the bodyParserFactory.
	 *
	 * @param bodyParserFactory
	 *            the bodyParserFactory to set.
	 */
	public void setBodyParserFactory(BodyParserFactory bodyParserFactory) {
		this.bodyParserFactory = bodyParserFactory;
	}

	private int getBufferSize() {
		return Integer.getInteger(BUFFER_SIZE_PROPERTY, BUFFER_SIZE).intValue();
	}
}
