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

import javax.net.ServerSocketFactory;

import ej.hoka.http.encoding.HTTPEncodingRegistry;
import ej.hoka.http.encoding.IHTTPEncodingHandler;
import ej.hoka.http.encoding.UnsupportedHTTPEncodingException;
import ej.hoka.http.requesthandler.RequestHandler;
import ej.hoka.http.requesthandler.RequestHandlerComposite;
import ej.hoka.http.requesthandler.ResourceRequestHandler;
import ej.hoka.log.Messages;
import ej.hoka.tcp.TCPServer;
import ej.util.message.Level;

/**
 * HTTP Server.
 * <p>
 * <b>Features + limitations: </b>
 * <ul>
 * <li>CLDC 1.1</li>
 * <li>No fixed configuration files, logging, authorization, encryption.</li>
 * <li>Supports parameter parsing of GET and POST methods</li>
 * <li>Supports both dynamic content and file serving</li>
 * <li>Never caches anything</li>
 * <li>Doesn't limit bandwidth, request time or simultaneous connections</li>
 * <li>Contains a built-in list of most common MIME types</li>
 * <li>All header names are converted to lower case</li>
 * </ul>
 * <p>
 * Define a {@link RequestHandler} to expose the different services of your application.
 * <p>
 * <b>Example:</b>
 *
 * <pre>
 * // get a new server which uses a ResourceRequestHandler
 * HTTPServer server = new HTTPServer(serverSocket, 10, 1);
 *
 * // start the server
 * server.start();
 * </pre>
 * <p>
 * To parse the request and write the response, a buffer size of 2048 by default is used. To change this value, set the
 * property "hoka.buffer.size".
 *
 * @see RequestHandlerComposite
 * @see ResourceRequestHandler
 */
public class HTTPServer {

	/**
	 * This size is used for the request and answer buffer size (two buffers will be created).
	 */
	private static final int BUFFER_SIZE = 2048;

	/**
	 * Property to set a custom buffer size.
	 */
	private static final String BUFFER_SIZE_PROPERTY = "hoka.buffer.size"; //$NON-NLS-1$

	/**
	 * The default root directory.
	 */
	private static final String HOKA_ROOT_DIRECTORY = "/hoka/"; //$NON-NLS-1$

	/**
	 * The HTML line break tag.
	 */
	private static final String HTML_BR = "<br/>"; //$NON-NLS-1$

	private static final HTTPResponse RESPONSE_INTERNAL_ERROR = HTTPResponse
			.createResponseFromStatus(HTTPConstants.HTTP_STATUS_INTERNALERROR);

	private static final HTTPResponse RESPONSE_REQUEST_TIMEOUT = HTTPResponse
			.createResponseFromStatus(HTTPConstants.HTTP_STATUS_REQUESTTIMEOUT);

	private static final HTTPResponse RESPONSE_NOT_ACCEPTABLE = HTTPResponse
			.createResponseFromStatus(HTTPConstants.HTTP_STATUS_NOTACCEPTABLE);

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
	 * Array of {@link Thread} for the session jobs.
	 */
	private Thread[] jobs;

	private boolean sendStackTraceOnException;

	/**
	 * Constructs the underlying {@link TCPServer} and the HTTP server that manage jobs to handle the connections from
	 * the {@link TCPServer}.
	 * <p>
	 * Requests are handled by a {@link ResourceRequestHandler} targeting the <code>/hoka/</code> resource folder.
	 *
	 * @param port
	 *            the port to use.
	 * @param maxSimultaneousConnection
	 *            the maximum number of simultaneously opened connections.
	 * @param jobCount
	 *            the number of jobs to run.
	 */
	public HTTPServer(int port, int maxSimultaneousConnection, int jobCount) {
		this(port, maxSimultaneousConnection, jobCount, new ResourceRequestHandler(HOKA_ROOT_DIRECTORY));
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
		// Then, apply the application request handler
		this.rootRequestHandler.addRequestHandler(requestHandler);
		// In case the application request handler doesn't process the request, send a "404 Not Found" error
		this.rootRequestHandler.addRequestHandler(NotFoundRequestHandler.instance);

		this.encodingRegistry = encodingRegistry;

		this.sendStackTraceOnException = false;
	}

	/**
	 * Start the {@link HTTPServer} (in a dedicated thread): start listening for connections and start jobs to process
	 * opened connections.
	 * <p>
	 * Multiple start is not allowed.
	 *
	 * @throws IOException
	 *             if an error occurs during the creation of the socket.
	 */
	public void start() throws IOException {
		this.server.start();

		this.jobs = new Thread[this.sessionJobsCount];

		for (int i = this.sessionJobsCount - 1; i >= 0; i--) {
			Thread job = new Thread(newJob(), "HTTP-JOB-" + i); //$NON-NLS-1$
			this.jobs[i] = job;
			job.start();
		}
	}

	/**
	 * Stops the {@link HTTPServer}. Stops listening for connections. This method blocks until all session jobs are
	 * stopped.
	 */
	public void stop() {
		this.server.stop();

		for (int i = this.jobs.length - 1; i >= 0; i--) {
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
	 * @return a new job process as {@link Runnable}.
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
						response = RESPONSE_NOT_ACCEPTABLE;
					} /*
						 * else { // continue with no encoding (null handler == // identity) // Example: Firefox 3.6
						 * asks for // Accept-Encoding=gzip,deflate by default. // If none of these encodings if present
						 * on this // embedded server, send without encoding // instead of Error 406. This avoid to
						 * modify // default options (about:config => //
						 * network.http.accept-encoding=gzip,deflate,identity }
						 */

					String requestConnectionHeader = request.getHeaderField(HTTPConstants.FIELD_CONNECTION);
					String responseConnectionHeader = response.getHeaderField(HTTPConstants.FIELD_CONNECTION);
					keepAlive = HTTPConstants.FIELD_CONNECTION_VALUE_KEEP_ALIVE
							.equalsIgnoreCase(requestConnectionHeader)
							&& !HTTPConstants.FIELD_CONNECTION_VALUE_CLOSE.equalsIgnoreCase(responseConnectionHeader);
					responseMessage = request.getURI();
				} catch (IllegalArgumentException e) {
					responseMessage = e.getMessage();
					response = HTTPResponse.createError(HTTPConstants.HTTP_STATUS_BADREQUEST, responseMessage);
					keepAlive = request != null && request.getHeaderField(HTTPConstants.FIELD_CONNECTION)
							.equalsIgnoreCase(HTTPConstants.FIELD_CONNECTION_VALUE_KEEP_ALIVE);
				} catch (UnsupportedHTTPEncodingException e) {
					responseMessage = e.getMessage();
					response = HTTPResponse.createError(HTTPConstants.HTTP_STATUS_NOTIMPLEMENTED, responseMessage);
					keepAlive = request != null && request.getHeaderField(HTTPConstants.FIELD_CONNECTION)
							.equalsIgnoreCase(HTTPConstants.FIELD_CONNECTION_VALUE_KEEP_ALIVE);
				} catch (SocketTimeoutException e) {
					responseMessage = ""; //$NON-NLS-1$
					response = RESPONSE_REQUEST_TIMEOUT;
					keepAlive = false;
				} catch (IOException e) {
					throw e;
				} catch (final Throwable e) {
					responseMessage = e.getMessage();
					if (this.sendStackTraceOnException) {
						StringBuilder fullMessageBuilder = new StringBuilder(responseMessage);
						fullMessageBuilder.append(HTML_BR);
						for (StackTraceElement stackTraceElement : e.getStackTrace()) {
							fullMessageBuilder.append(stackTraceElement.toString()).append(HTML_BR);
						}
						response = HTTPResponse.createError(HTTPConstants.HTTP_STATUS_INTERNALERROR,
								fullMessageBuilder.toString());
					} else {
						response = RESPONSE_INTERNAL_ERROR;
					}

					keepAlive = false;
				} finally {
					// TODO : Remove to allow Keep-Alive
					keepAlive = false;
				}

				String connectionHeader = keepAlive ? HTTPConstants.FIELD_CONNECTION_VALUE_KEEP_ALIVE
						: HTTPConstants.FIELD_CONNECTION_VALUE_CLOSE;
				response.addHeaderField(HTTPConstants.FIELD_CONNECTION, connectionHeader);

				String status = response.getStatus();
				Messages.LOGGER.log(
						status.equals(HTTPConstants.HTTP_STATUS_OK) ? Level.FINE
								: status.equals(HTTPConstants.HTTP_STATUS_INTERNALERROR) ? Level.SEVERE : Level.INFO,
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

	/**
	 * Returns whether or not the server sends the stack trace of thrown exceptions.
	 * <p>
	 * Returns false by default.
	 *
	 * @return {@code true} if the server sends the stack trace of thrown exceptions, {@code false} otherwise.
	 * @see #sendStackTraceOnException(boolean)
	 */
	public boolean getSendStackTraceOnException() {
		return this.sendStackTraceOnException;
	}

	/**
	 * Sets whether or not the server must send the stack trace of thrown exceptions.
	 *
	 * @param sendStackTraceOnException
	 *            {@code true} if the server must send the stack trace of thrown exceptions, {@code false} otherwise.
	 */
	public void sendStackTraceOnException(boolean sendStackTraceOnException) {
		this.sendStackTraceOnException = sendStackTraceOnException;
	}

}
