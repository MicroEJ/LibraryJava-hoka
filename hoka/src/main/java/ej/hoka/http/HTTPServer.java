/*
 * Java
 *
 * Copyright 2009-2019 MicroEJ Corp. All rights reserved.
 * This library is provided in source code for use, modification and test, subject to license terms.
 * Any modification of the source code will break MicroEJ Corp. warranties on the whole library.
 */
package ej.hoka.http;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.Map;
import java.util.Map.Entry;

import javax.net.ServerSocketFactory;

import ej.hoka.http.encoding.HTTPEncodingRegister;
import ej.hoka.http.encoding.IHTTPEncodingHandler;
import ej.hoka.http.encoding.IHTTPTransferCodingHandler;
import ej.hoka.http.encoding.IdentityEncodingHandler;
import ej.hoka.http.encoding.IdentityTransferCodingHandler;
import ej.hoka.http.encoding.UnsupportedHTTPEncodingException;
import ej.hoka.http.requesthandler.IfNoneMatchRequestHandler;
import ej.hoka.http.requesthandler.RequestHandler;
import ej.hoka.http.requesthandler.RequestHandlerComposite;
import ej.hoka.http.support.AcceptEncoding;
import ej.hoka.http.support.MIMEUtils;
import ej.hoka.http.support.QualityArgument;
import ej.hoka.log.Messages;
import ej.hoka.tcp.TCPServer;
import ej.util.message.Level;

/**
 * <p>
 * HTTP Server.
 * </p>
 *
 * <p>
 * <b>Features + limitations: </b>
 * <ul>
 *
 * <li>CLDC 1.1</li>
 * <li>No fixed configuration files, logging, authorization, encryption.</li>
 * <li>Supports parameter parsing of GET and POST methods</li>
 * <li>Supports both dynamic content and file serving</li>
 * <li>Never caches anything</li>
 * <li>Doesn't limit bandwidth, request time or simultaneous connections</li>
 * <li>Contains a built-in list of most common MIME types</li>
 * <li>All header names are converted to lower case</li>
 *
 * </ul>
 * <p>
 * Override {@link HTTPSession#answer(HTTPRequest)} and redefine the server behavior for your own application
 *
 * </p>
 *
 * <p>
 * <b>Example:</b>
 * </p>
 *
 * <pre>
 * // get a new server which handle a Default HTTP Session
 * HTTPServer server = new HTTPServer(serverSocket, new DefaultHTTPSession.Factory(), 10, 1);
 *
 * // start the server
 * server.start();
 * </pre>
 */
public class HTTPServer extends TCPServer {
	/*
	 * Implementation notes (some informations may be extracted in documentation or example) <p><b>Features +
	 * limitations: </b><ul>
	 *
	 * <li> CLDC 1.1 </li> <li> No fixed configuration files, logging, authorization, encryption etc. (Implement
	 * yourself if you need them.) </li> <li> Supports parameter parsing of GET and POST methods </li> <li> Supports
	 * both dynamic content and file serving </li> <li> Never caches anything </li> <li> Doesn't limit bandwidth,
	 * request time or simultaneous connections </li> <li> Contains a built-in list of most common MIME types </li> <li>
	 * All header names are converted in lower case so they don't vary between browsers/clients </li>
	 *
	 * </ul>
	 *
	 * <p><b>Ways to use: </b><ul>
	 *
	 * <li> Instantiate the {@link HTTPServer} with wanted end point address and port </li> <li> Override {@link
	 * HTTPSession#answer(String, String, java.util.Hashtable, java.util.Hashtable) HTTPSession.answer()} and redefine
	 * the server behavior for your own application </li>
	 *
	 * </ul>
	 */

	// FIXME for memory usage only
	// public Runtime r;

	/**
	 * This size is used for the request and answer buffer size (two buffers will be created).
	 */
	private static final int BUFFER_SIZE = 2048;

	/**
	 * Property to set a custom buffer size.
	 */
	private static final String BUFFER_SIZE_PROPERTY = "hoka.buffer.size"; //$NON-NLS-1$

	/**
	 * The colon character.
	 */
	private static final String RESPONSE_COLON = ": "; //$NON-NLS-1$

	/**
	 * The HTTP/1.1 version String.
	 */
	private static final String RESPONSE_HTTP11 = "HTTP/1.1 "; //$NON-NLS-1$

	/**
	 * The Content-Type: String.
	 */
	private static final String RESPONSE_CONTENTTYPE = HTTPConstants.FIELD_CONTENT_TYPE + RESPONSE_COLON;

	/**
	 * By default, server is configured to keep connection open during one minute if possible (implying that the browser
	 * is able to manage persistent connections).
	 */
	private static final long DEFAULT_KEEP_ALIVE_DURATION = 60000; // 60s in

	/**
	 * Number of jobs per sessions.
	 */
	private final int sessionJobsCount;

	/**
	 * The keep-alive duration in ms (not used).
	 */
	private final long keepAliveDuration;

	private final HTTPEncodingRegister encodingRegister;

	private final RequestHandlerComposite rootRequestHandler;

	/**
	 * Array of {@link Thread}s for the session jobs.
	 */
	private Thread[] jobs;

	/**
	 * <p>
	 * Creates a {@link HTTPServer} on the given port.
	 * </p>
	 * <p>
	 * The default encoding to be used is the identity encoding. Further encodings may be registered using
	 * {@link #registerEncodingHandler(IHTTPEncodingHandler)}.
	 * </p>
	 * <p>
	 * Server is not started until {@link #start()} is called.
	 * </p>
	 *
	 * @param port
	 *            the port.
	 * @param httpSessionFactory
	 *            the HTTP session factory.
	 * @param maxSimultaneousConnection
	 *            the maximal number of simultaneously opened connections.
	 * @param jobCountBySession
	 *            the number of parallel jobs to process by opened sessions. if <code>jobCountBySession</code> == 1, the
	 *            jobs are processed sequentially.
	 * @throws IOException
	 *             if server cannot be bind to given port.
	 */
	public HTTPServer(int port, int maxSimultaneousConnection, int jobCountBySession) {
		this(port, maxSimultaneousConnection, jobCountBySession, ServerSocketFactory.getDefault());
	}

	/**
	 * <p>
	 * Creates a {@link HTTPServer} using the given {@link ServerSocket} .
	 * </p>
	 * <p>
	 * The default encoding to be used is the identity encoding. Further encodings may be registered using
	 * {@link #registerEncodingHandler(IHTTPEncodingHandler)}.
	 * </p>
	 * <p>
	 * Server is not started until {@link #start()} is called.
	 * </p>
	 *
	 * @param port
	 *            the port.
	 * @param maxSimultaneousConnection
	 *            the maximal number of simultaneously opened connections.
	 * @param jobCountBySession
	 *            the number of parallel jobs to process by opened sessions. if <code>jobCountBySession</code> == 1, the
	 *            jobs are processed sequentially.
	 * @param httpSessionFactory
	 *            the HTTP session factory.
	 * @param serverSocketFactory
	 *            the serverSocketFactory to use.
	 * @throws IOException
	 *             if server cannot be bind to given port.
	 */
	public HTTPServer(int port, int maxSimultaneousConnection, int jobCountBySession,
			ServerSocketFactory serverSocketFactory) {
		this(port, maxSimultaneousConnection, jobCountBySession, serverSocketFactory, new HTTPEncodingRegister());
	}

	/**
	 * @param port
	 * @param maxSimultaneousConnection
	 * @param jobCountBySession
	 * @param serverSocketFactory
	 * @param encodingRegister
	 * @throws IOException
	 */
	public HTTPServer(int port, int maxSimultaneousConnection, int jobCountBySession,
			ServerSocketFactory serverSocketFactory, HTTPEncodingRegister encodingRegister) {
		this(port, maxSimultaneousConnection, jobCountBySession, serverSocketFactory, encodingRegister,
				DEFAULT_KEEP_ALIVE_DURATION, 0);
	}

	/**
	 * Constructs a new instance of {@link HTTPServer}.<br>
	 * The encoding handler will be {@link IdentityEncodingHandler}. The transfer coding handler will be
	 * {@link IdentityTransferCodingHandler}
	 *
	 * @param port
	 *            the port.
	 * @param maxSimultaneousConnection
	 *            the maximal number of simultaneously opened connections.
	 * @param jobCountBySession
	 *            the number of parallel jobs to process by opened sessions. if <code>jobCountBySession</code> == 1, the
	 *            jobs are processed sequentially.
	 * @param httpSessionFactory
	 *            the HTTP session factory.
	 * @param serverSocketFactory
	 *            the serverSocketFactory to use.
	 * @param keepAliveDuration
	 *            the keep alive duration (not used)
	 * @throws IOException
	 *             if server cannot be bind to given port.
	 * @throws IllegalArgumentException
	 *             when any of the following is true:
	 *             <ul>
	 *             <li><code>maxSimultaneousConnection</code><= 0
	 *             <li><code>jobCountBySession</code><= 0
	 *             <li><code>jobCountBySession</code><=0
	 *             <li><code>keepAliveDuration</code><=0
	 *             </ul>
	 */
	private HTTPServer(int port, int maxSimultaneousConnection, int jobCountBySession,
			ServerSocketFactory serverSocketFactory, HTTPEncodingRegister encodingRegister, long keepAliveDuration,
			int requestTimeoutDuration) {
		super(port, maxSimultaneousConnection, requestTimeoutDuration, serverSocketFactory);

		if ((jobCountBySession <= 0) || (keepAliveDuration <= 0)) {
			throw new IllegalArgumentException();
		}

		// FIXME for memory usage only
		// r = Runtime.getRuntime();

		this.sessionJobsCount = jobCountBySession;
		this.keepAliveDuration = keepAliveDuration;

		this.encodingRegister = encodingRegister;

		this.rootRequestHandler = new RequestHandlerComposite() {
			@Override
			public HTTPResponse process(HTTPRequest request) {
				HTTPResponse response = super.process(request);
				if (response == null) {
					response = new HTTPResponse();
					response.setStatus(HTTPConstants.HTTP_STATUS_NOTFOUND);
				}
				return response;
			}
		};
		this.rootRequestHandler.addChild(new IfNoneMatchRequestHandler());
	}

	/**
	 * <p>
	 * Start the {@link HTTPServer} (in a dedicated thread): start listening for connections and start jobs to process
	 * opened connections.<br>
	 * Multiple start is not allowed.
	 * </p>
	 *
	 * @throws IOException
	 *             if an error occurs during the creation of the socket.
	 */
	@Override
	public void start() throws IOException {
		super.start();

		this.jobs = new Thread[this.sessionJobsCount];

		for (int i = this.sessionJobsCount; --i >= 0;) {
			Thread job = new Thread(newJob(), "HTTP-JOB-" + i); //$NON-NLS-1$
			this.jobs[i] = job;
			job.start();
		}
	}

	/**
	 * <p>
	 * Stops the {@link HTTPServer}. Stops listening for connections. This method blocks until all session jobs are
	 * stopped.
	 * </p>
	 */
	@Override
	public void stop() {
		super.stop();

		for (int i = this.jobs.length; --i >= 0;) {
			try {
				this.jobs[i].join();
			} catch (InterruptedException e) {
				// nothing to do on interrupted exception
			}
		}
	}

	public void addRequestHandler(RequestHandler requestHandler) {
		this.rootRequestHandler.addChild(requestHandler);
	}

	/**
	 * Returns a new job process as {@link Runnable}.
	 *
	 * @return a new job process as {@link Runnable}
	 */
	private Runnable newJob() {
		return new Runnable() {
			@Override
			public void run() {
				while (true) {
					try (Socket connection = HTTPServer.super.getNextStreamConnection()) {
						if (connection == null) {
							// server stopped
							return;
						}

						Messages.LOGGER.log(Level.FINE, Messages.CATEGORY, Messages.PROCESS_CONNECTION,
								Integer.valueOf(connection.hashCode()), connection.getInetAddress().toString());

						try (InputStream inputStream = new BufferedInputStream(connection.getInputStream(),
								getBufferSize())) {

							// TODO handling persistent connection
							// long connectionStartTime = -server.keepAliveDuration;
							// do{
							// // if input stream contains some data, a new request
							// has been sent
							// if(is.available() > 0){

							try {
								HTTPRequest request = parseRequestHeader(inputStream);

								HTTPResponse response = null; // TODO process request

								IHTTPEncodingHandler encodingHandler = getEncodingHandler(request);

								// FIXME do not only log erroneous responses, log any
								// processed request (maybe according to an error level
								// in logger)
								if (checkHTTPError(response)) {
									Messages.LOGGER.log(Level.INFO, Messages.CATEGORY, Messages.HTTP_ERROR,
											Integer.valueOf(connection.hashCode()),
											connection.getInetAddress().toString(), response.getStatus(),
											request.getURI());
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

								sendResponse(response, encodingHandler, connection);
							} catch (Exception e) {
								sendError("", connection);
							}

							// } // TODO handling persistent connection
							// }
							// // if the keep alive time is not elapsed then wait
							// for next request
							// while(System.currentTimeMillis()-connectionStartTime
							// < server.keepAliveDuration);

						} catch (IOException e) {
							// connection lost
							Messages.LOGGER.log(Level.INFO, Messages.CATEGORY, Messages.CONNECTION_LOST, e,
									Integer.valueOf(connection.hashCode()), connection.getInetAddress().toString());
						}

						Messages.LOGGER.log(Level.FINE, Messages.CATEGORY, Messages.CONNECTION_CLOSED,
								Integer.valueOf(connection.hashCode()), connection.getInetAddress().toString());
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		};
	}

	private HTTPRequest parseRequestHeader(InputStream inputStream) throws Exception {
		HTTPRequest request;
		try {
			request = new HTTPRequest(inputStream, HTTPServer.this.encodingRegister);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			throw new Exception(HTTPConstants.HTTP_STATUS_BADREQUEST);
		} catch (UnsupportedHTTPEncodingException e) {
			e.printStackTrace();
			// unable to decode data
			// RFC2616 / 3.6: A server which receives ... a
			// transfer-coding
			// it does not understand SHOULD return 501
			// (Unimplemented),
			// and close the connection.
			throw new Exception(HTTPConstants.HTTP_STATUS_NOTIMPLEMENTED + e.field + RESPONSE_COLON + e.encoding);
		} catch (SocketTimeoutException e) {
			throw new Exception(HTTPConstants.HTTP_STATUS_REQUESTTIMEOUT);
		}
		return request;
	}

	protected IHTTPEncodingHandler getEncodingHandler(HTTPRequest request) throws Exception {

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
				throw new Exception(HTTPConstants.HTTP_STATUS_NOTACCEPTABLE);
			} /*
				 * else { // continue with no encoding (null handler == // identity) // Example: Firefox 3.6 asks for //
				 * Accept-Encoding=gzip,deflate by default. // If none of these encodings if present on this // embedded
				 * server, send without encoding // instead of Error 406. This avoid to modify // default options
				 * (about:config => // network.http.accept-encoding=gzip,deflate,identity }
				 */
		}

		return handler;
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
			IHTTPEncodingHandler handler = this.encodingRegister.getEncodingHandler(encodings[ptrMax].getArgument());
			if (handler != null) {
				return handler;
			}
		}

		return null;
	}

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
	 * Send a HTTP error code with given status.
	 *
	 * @param status
	 *            the HTTP status
	 * @see #sendError(String, String)
	 */
	protected void sendError(String status, Socket connection) {
		sendError(status, null, connection);
	}

	/*
	 * Content for building the answer
	 */

	/**
	 * Sends a HTTP response with given status and optional message. The information is also logged.
	 *
	 * @param msg
	 *            the message, could be <code>null</code>
	 *
	 * @param status
	 *            the error status
	 * @see #sendResponse(HTTPResponse, IHTTPEncodingHandler)
	 */
	protected void sendError(String status, String msg, Socket connection) {
		Messages.LOGGER.log(Level.INFO, Messages.CATEGORY, Messages.HTTP_ERROR, Integer.valueOf(connection.hashCode()),
				connection.getInetAddress().toString(), status, msg);
		sendResponse(createErrorResponse(status, msg), (IHTTPEncodingHandler) null, connection);
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
	protected void sendResponse(HTTPResponse response, IHTTPEncodingHandler encodingHandler, Socket connection) {
		try {
			if (encodingHandler != null) {
				response.addHeaderField(HTTPConstants.FIELD_CONTENT_ENCODING, encodingHandler.getId());
			}
			try (OutputStream out = connection.getOutputStream()) {
				writeResponse(response, encodingHandler, out);
			}
		} catch (IOException e) {
			// an error occurred when sending the response: can't do anything
			// more
			Messages.LOGGER.log(Level.SEVERE, Messages.CATEGORY, Messages.ERROR_UNKNOWN, e);
		}
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
		final byte[] eofHeader = HTTPConstants.END_OF_LINE.getBytes();

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
		try (InputStream dataStream = response.getData()) {
			long length = response.getLength();

			if (length < 0) {
				// data will be transmitted using chunked transfer coding
				// only when dataStream is used, the size is known otherwise
				response.addHeaderField(HTTPConstants.FIELD_TRANSFER_ENCODING,
						this.encodingRegister.getChunkedTransferCodingHandler().getId());
			} // else the length is already defined in a header by the response

			writeHTTPHeader(response, output);

			if (rawData != null) {
				try (OutputStream dataOutput = this.encodingRegister.getIdentityTransferCodingHandler().open(response,
						output)) {
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
				try (OutputStream dataOutput = (length == -1)
						? this.encodingRegister.getChunkedTransferCodingHandler().open(response, output)
						: this.encodingRegister.getIdentityTransferCodingHandler().open(response, output)) {
					try (OutputStream ecodedOutput = (encodingHandler != null) ? encodingHandler.open(dataOutput)
							: null) {
						OutputStream outputStream = (ecodedOutput != null) ? ecodedOutput : dataOutput;
						final byte[] readBuffer = new byte[getBufferSize()];
						while (true) {
							int len = dataStream.read(readBuffer);

							if (len < 0) { // read until EOF is reached
								break;
							}
							// store read data
							outputStream.write(readBuffer, 0, len);
							outputStream.flush();
						}
					}
				} catch (Throwable t) {
					Messages.LOGGER.log(Level.SEVERE, Messages.CATEGORY, Messages.ERROR_UNKNOWN, t);
				} finally {
					// close data output stream. This does not close underlying
					// TCP connection since transfer output stream does not
					// close its underlying output stream
					// if (dataOutput != null) {
					// dataOutput.close();
					// }
					response.setDataStreamClosed();
				}
			}
		}
		output.flush();
	}

	private void writeAndFlush(byte[] data, OutputStream stream) throws IOException {
		stream.write(data);
		stream.flush();
		stream.close();
	}

	private int getBufferSize() {
		return Integer.getInteger(BUFFER_SIZE_PROPERTY, BUFFER_SIZE).intValue();
	}

}
