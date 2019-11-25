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
import java.net.Socket;
import java.net.SocketTimeoutException;

import javax.net.ServerSocketFactory;

import ej.hoka.http.encoding.HTTPEncodingRegister;
import ej.hoka.http.encoding.IHTTPEncodingHandler;
import ej.hoka.http.encoding.UnsupportedHTTPEncodingException;
import ej.hoka.http.requesthandler.DefaultRequestHandler;
import ej.hoka.http.requesthandler.RequestHandler;
import ej.hoka.http.requesthandler.RequestHandlerComposite;
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
 * HTTPServer server = new HTTPServer(serverSocket, 10, 1);
 *
 * // start the server
 * server.start();
 * </pre>
 */
public class HTTPServer {
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
	 * The underlying TCP server.
	 */
	private final TCPServer server;

	/**
	 * Number of jobs per sessions.
	 */
	private final int sessionJobsCount;

	private final RequestHandlerComposite rootRequestHandler;

	private final HTTPEncodingRegister encodingRegister;

	/**
	 * Array of {@link Thread}s for the session jobs.
	 */
	private Thread[] jobs;

	public HTTPServer(int port, int maxSimultaneousConnection, int jobCount) {
		this(port, maxSimultaneousConnection, jobCount, new DefaultRequestHandler());
	}

	public HTTPServer(int port, int maxSimultaneousConnection, int jobCount, RequestHandler requestHandler) {
		this(new TCPServer(port, maxSimultaneousConnection), jobCount, requestHandler);
	}

	public HTTPServer(int port, int maxSimultaneousConnection, int jobCount, RequestHandler requestHandler,
			ServerSocketFactory serverSocketFactory) {
		this(new TCPServer(port, maxSimultaneousConnection, serverSocketFactory), jobCount, requestHandler);
	}

	public HTTPServer(int port, int maxSimultaneousConnection, int jobCount, RequestHandler requestHandler,
			ServerSocketFactory serverSocketFactory, int keepAliveDuration) {
		this(new TCPServer(port, maxSimultaneousConnection, serverSocketFactory, keepAliveDuration), jobCount,
				requestHandler);
	}

	public HTTPServer(TCPServer tcpServer, int jobCount, RequestHandler requestHandler) {
		this(tcpServer, jobCount, requestHandler, new HTTPEncodingRegister());
	}

	public HTTPServer(TCPServer tcpServer, int jobCount, final RequestHandler requestHandler,
			HTTPEncodingRegister encodingRegister) {
		this.server = tcpServer;

		if (jobCount <= 0) {
			throw new IllegalArgumentException();
		}
		this.sessionJobsCount = jobCount;

		this.rootRequestHandler = new RequestHandlerComposite();
		this.rootRequestHandler.addChild(IfNoneMatchRequestHandler.instance);
		this.rootRequestHandler.addChild(new RequestHandler() {
			@Override
			public HTTPResponse process(HTTPRequest request) {
				try {
					return requestHandler.process(request);
				} catch (Throwable e) {
					Messages.LOGGER.log(Level.SEVERE, Messages.CATEGORY, Messages.ERROR_UNKNOWN, e);
					return HTTPResponse.RESPONSE_INTERNAL_ERROR;
				}
			}

		});
		this.rootRequestHandler.addChild(NotFoundRequestHandler.instance);

		this.encodingRegister = encodingRegister;
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
	public void start() throws IOException {
		this.server.start();

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
	public void stop() {
		this.server.stop();

		for (int i = this.jobs.length; --i >= 0;) {
			try {
				this.jobs[i].join();
			} catch (InterruptedException e) {
				// nothing to do on interrupted exception
			}
		}
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
					try (Socket connection = HTTPServer.this.server.getNextStreamConnection()) {
						if (connection == null) {
							// server stopped
							return;
						}

						Messages.LOGGER.log(Level.FINE, Messages.CATEGORY, Messages.PROCESS_CONNECTION,
								Integer.valueOf(connection.hashCode()), connection.getInetAddress().toString());

						handleConnection(connection);

						Messages.LOGGER.log(Level.FINE, Messages.CATEGORY, Messages.CONNECTION_CLOSED,
								Integer.valueOf(connection.hashCode()), connection.getInetAddress().toString());
					} catch (IOException e) {
						Messages.LOGGER.log(Level.WARNING, Messages.CATEGORY, Messages.ERROR_UNKNOWN, e);
					}
				}
			}
		};
	}

	private void handleConnection(Socket connection) {
		try (InputStream inputStream = new BufferedInputStream(connection.getInputStream(), getBufferSize());
				OutputStream outputStream = connection.getOutputStream()) {
			boolean keepAlive;
			do {
				HTTPRequest request = null;
				HTTPResponse response;
				IHTTPEncodingHandler encodingHandler = null;
				String responseMessage;

				try {
					request = new HTTPRequest(inputStream, this.encodingRegister);

					response = this.rootRequestHandler.process(request);

					encodingHandler = this.encodingRegister
							.getEncodingHandler(request.getHeaderField(HTTPConstants.FIELD_ACCEPT_ENCODING));

					if (encodingHandler == null && CalibrationConstants.STRICT_ACCEPT_ENCODING_COMPLIANCE) {
						// RFC2616 14.3
						response = HTTPResponse.RESPONSE_NOT_ACCEPTABLE;
					} /*
						 * else { // continue with no encoding (null handler == // identity) // Example: Firefox 3.6
						 * asks for // Accept-Encoding=gzip,deflate by default. // If none of these encodings if present
						 * on this // embedded server, send without encoding // instead of Error 406. This avoid to
						 * modify // default options (about:config => //
						 * network.http.accept-encoding=gzip,deflate,identity }
						 */

					String requestConnectionHeader = request.getHeaderField(HTTPConstants.FIELD_CONNECTION);
					String responseConnectionHeader = response.getHeaderField(HTTPConstants.FIELD_CONNECTION);
					keepAlive = HTTPConstants.CONNECTION_FIELD_VALUE_KEEP_ALIVE
							.equalsIgnoreCase(requestConnectionHeader)
							&& !HTTPConstants.CONNECTION_FIELD_VALUE_CLOSE.equalsIgnoreCase(responseConnectionHeader);
					responseMessage = request.getURI();
				} catch (IllegalArgumentException e) {
					response = HTTPResponse.createError(HTTPConstants.HTTP_STATUS_BADREQUEST,
							responseMessage = e.getMessage());
					keepAlive = request != null && request.getHeaderField(HTTPConstants.FIELD_CONNECTION)
							.equalsIgnoreCase(HTTPConstants.CONNECTION_FIELD_VALUE_KEEP_ALIVE);
				} catch (UnsupportedHTTPEncodingException e) {
					response = HTTPResponse.createError(HTTPConstants.HTTP_STATUS_NOTIMPLEMENTED,
							responseMessage = e.getMessage());
					keepAlive = request != null && request.getHeaderField(HTTPConstants.FIELD_CONNECTION)
							.equalsIgnoreCase(HTTPConstants.CONNECTION_FIELD_VALUE_KEEP_ALIVE);
				} catch (SocketTimeoutException e) {
					response = HTTPResponse.RESPONSE_REQUESTTIMEOUT;
					keepAlive = false;
					responseMessage = ""; //$NON-NLS-1$
				}

				String connectionHeader = keepAlive ? HTTPConstants.CONNECTION_FIELD_VALUE_KEEP_ALIVE
						: HTTPConstants.CONNECTION_FIELD_VALUE_CLOSE;
				response.addHeaderField(HTTPConstants.FIELD_CONNECTION, connectionHeader);

				String status = response.getStatus();
				Messages.LOGGER.log(status.equals(HTTPConstants.HTTP_STATUS_OK) ? Level.FINE : Level.INFO,
						Messages.CATEGORY, Messages.HTTP_RESPONSE, Integer.valueOf(connection.hashCode()),
						connection.getInetAddress().toString(), status, responseMessage);

				response.sendResponse(outputStream, encodingHandler, this.encodingRegister, getBufferSize());
			} while (keepAlive);
		} catch (IOException e) {
			// connection lost
			Messages.LOGGER.log(Level.INFO, Messages.CATEGORY, Messages.CONNECTION_LOST,
					Integer.valueOf(connection.hashCode()), connection.getInetAddress().toString());
		}
	}

	private int getBufferSize() {
		return Integer.getInteger(BUFFER_SIZE_PROPERTY, BUFFER_SIZE).intValue();
	}

}
