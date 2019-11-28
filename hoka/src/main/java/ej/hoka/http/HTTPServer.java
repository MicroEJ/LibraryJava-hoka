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
import java.util.HashMap;
import java.util.Map;

import javax.net.ServerSocketFactory;

import ej.hoka.http.encoding.HTTPEncodingRegistry;
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
 * Define a {@link RequestHandler} to expose the different services of your application.
 * </p>
 *
 * <p>
 * <b>Example:</b>
 * </p>
 *
 * <pre>
 * // get a new server which uses a DefaultRequestHandler
 * HTTPServer server = new HTTPServer(serverSocket, 10, 1);
 *
 * // start the server
 * server.start();
 * </pre>
 *
 * @see RequestHandlerComposite
 * @see DefaultRequestHandler
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
	 * <li> Define a {@link RequestHandler} to expose the different services of your application. </li> <li> Instantiate
	 * the {@link HTTPServer} with wanted underlying socket parameters and the {@link RequestHandler}. </li>
	 *
	 * </ul>
	 */

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

	private final HTTPEncodingRegistry encodingRegistry;

	/**
	 * Array of {@link Thread}s for the session jobs.
	 */
	private Thread[] jobs;

	/**
	 * Constructs the underlying {@link TCPServer} and the HTTP server that manage jobs to handle the connections from
	 * the {@link TCPServer} and a {@link DefaultRequestHandler}.
	 *
	 * @param port
	 *            the port to use.
	 * @param maxSimultaneousConnection
	 *            the maximum number of simultaneously opened connections.
	 * @param jobCount
	 *            the number of jobs to run.
	 */
	public HTTPServer(int port, int maxSimultaneousConnection, int jobCount) {
		this(port, maxSimultaneousConnection, jobCount, new DefaultRequestHandler());
	}

	/**
	 * Constructs the underlying {@link TCPServer} and the HTTP server that manage jobs to handle the connections from
	 * the {@link TCPServer} with <code>requestHandler</code>.
	 *
	 * @param port
	 *            the port to use.
	 * @param maxSimultaneousConnection
	 *            the maximum number of simultaneously opened connections.
	 * @param jobCount
	 *            the number of jobs to run.
	 * @param requestHandler
	 *            the application request handler.
	 */
	public HTTPServer(int port, int maxSimultaneousConnection, int jobCount, RequestHandler requestHandler) {
		this(new TCPServer(port, maxSimultaneousConnection), jobCount, requestHandler);
	}

	/**
	 * Constructs the underlying {@link TCPServer} and the HTTP server that manage jobs to handle the connections from
	 * the {@link TCPServer} with <code>requestHandler</code>.
	 *
	 * @param port
	 *            the port to use.
	 * @param maxSimultaneousConnection
	 *            the maximum number of simultaneously opened connections.
	 * @param jobCount
	 *            the number of jobs to run.
	 * @param requestHandler
	 *            the application request handler.
	 * @param serverSocketFactory
	 *            the {@link ServerSocketFactory}.
	 */
	public HTTPServer(int port, int maxSimultaneousConnection, int jobCount, RequestHandler requestHandler,
			ServerSocketFactory serverSocketFactory) {
		this(new TCPServer(port, maxSimultaneousConnection, serverSocketFactory), jobCount, requestHandler);
	}

	/**
	 * Constructs the underlying {@link TCPServer} and the HTTP server that manage jobs to handle the connections from
	 * the {@link TCPServer} with <code>requestHandler</code>.
	 *
	 * @param port
	 *            the port to use.
	 * @param maxSimultaneousConnection
	 *            the maximum number of simultaneously opened connections.
	 * @param jobCount
	 *            the number of jobs to run.
	 * @param requestHandler
	 *            the application request handler.
	 * @param serverSocketFactory
	 *            the {@link ServerSocketFactory}.
	 * @param keepAliveDuration
	 *            the timeout duration for idling persistent connections.
	 */
	public HTTPServer(int port, int maxSimultaneousConnection, int jobCount, RequestHandler requestHandler,
			ServerSocketFactory serverSocketFactory, int keepAliveDuration) {
		this(new TCPServer(port, maxSimultaneousConnection, serverSocketFactory, keepAliveDuration), jobCount,
				requestHandler);
	}

	/**
	 * Constructs a HTTP server that manage jobs to handle the connections from <code>tcpServer</code> with
	 * <code>requestHandler</code>.
	 *
	 * @param tcpServer
	 *            the underlying TCP server that stores upcoming connections.
	 * @param jobCount
	 *            the number of jobs to run.
	 * @param requestHandler
	 *            the application request handler.
	 */
	public HTTPServer(TCPServer tcpServer, int jobCount, RequestHandler requestHandler) {
		this(tcpServer, jobCount, requestHandler, new HTTPEncodingRegistry());
	}

	/**
	 * Constructs a HTTP server that manage jobs to handle the connections from <code>tcpServer</code> with
	 * <code>requestHandler</code>.
	 *
	 * @param tcpServer
	 *            the underlying TCP server that stores upcoming connections.
	 * @param jobCount
	 *            the number of jobs to run.
	 * @param requestHandler
	 *            the application request handler.
	 * @param encodingRegistry
	 *            the registry of available encoding handlers.
	 */
	public HTTPServer(TCPServer tcpServer, int jobCount, final RequestHandler requestHandler,
			HTTPEncodingRegistry encodingRegistry) {
		this.server = tcpServer;

		if (jobCount <= 0) {
			throw new IllegalArgumentException();
		}
		this.sessionJobsCount = jobCount;

		this.rootRequestHandler = new RequestHandlerComposite();
		// First, check if the resource matches the client cache
		this.rootRequestHandler.addRequestHandler(IfNoneMatchRequestHandler.instance);
		// Then, apply the application request handler and catch exceptions
		this.rootRequestHandler.addRequestHandler(new RequestHandler() {
			@Override
			public HTTPResponse process(HTTPRequest request, Map<String, String> attributes) {
				try {
					return requestHandler.process(request, attributes);
				} catch (Throwable e) {
					Messages.LOGGER.log(Level.SEVERE, Messages.CATEGORY_HOKA, Messages.ERROR_UNKNOWN, e);
					return HTTPResponse.RESPONSE_INTERNAL_ERROR;
				}
			}

		});
		// In case the application request handler doesn't process the request, send a "404 Not Found" error
		this.rootRequestHandler.addRequestHandler(NotFoundRequestHandler.instance);

		this.encodingRegistry = encodingRegistry;
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

						Messages.LOGGER.log(Level.FINE, Messages.CATEGORY_HOKA, Messages.PROCESS_CONNECTION,
								Integer.valueOf(connection.hashCode()), connection.getInetAddress().toString());

						handleConnection(connection);

						Messages.LOGGER.log(Level.FINE, Messages.CATEGORY_HOKA, Messages.CONNECTION_CLOSED,
								Integer.valueOf(connection.hashCode()), connection.getInetAddress().toString());
					} catch (IOException e) {
						Messages.LOGGER.log(Level.WARNING, Messages.CATEGORY_HOKA, Messages.ERROR_UNKNOWN, e);
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
					request = new HTTPRequest(inputStream, this.encodingRegistry);

					response = this.rootRequestHandler.process(request, new HashMap<String, String>());

					encodingHandler = this.encodingRegistry
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
						Messages.CATEGORY_HOKA, Messages.HTTP_RESPONSE, Integer.valueOf(connection.hashCode()),
						connection.getInetAddress().toString(), status, responseMessage);

				response.sendResponse(outputStream, encodingHandler, this.encodingRegistry, getBufferSize());
			} while (keepAlive);
		} catch (IOException e) {
			// connection lost
			Messages.LOGGER.log(Level.INFO, Messages.CATEGORY_HOKA, Messages.CONNECTION_LOST,
					Integer.valueOf(connection.hashCode()), connection.getInetAddress().toString());
		}
	}

	private int getBufferSize() {
		return Integer.getInteger(BUFFER_SIZE_PROPERTY, BUFFER_SIZE).intValue();
	}

}
