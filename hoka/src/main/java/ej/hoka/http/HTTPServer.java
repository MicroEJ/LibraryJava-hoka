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
import ej.hoka.http.requesthandler.IfNoneMatchRequestHandler;
import ej.hoka.http.requesthandler.NotFoundRequestHandler;
import ej.hoka.http.requesthandler.RequestHandler;
import ej.hoka.http.requesthandler.RequestHandlerComposite;
import ej.hoka.http.support.MIMEUtils;
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
	 * The colon character.
	 */
	private static final String RESPONSE_COLON = ": "; //$NON-NLS-1$

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
		this.rootRequestHandler.addChild(new IfNoneMatchRequestHandler());
		this.rootRequestHandler.addChild(new RequestHandler() {
			@Override
			public HTTPResponse process(HTTPRequest request) throws HTTPErrorException {
				try {
					return requestHandler.process(request);
				} catch (HTTPErrorException e) {
					throw e;
				} catch (Throwable e) {
					Messages.LOGGER.log(Level.SEVERE, Messages.CATEGORY, Messages.ERROR_UNKNOWN, e);
					throw new HTTPErrorException(HTTPConstants.HTTP_STATUS_INTERNALERROR);
				}
			}

		});
		this.rootRequestHandler.addChild(new NotFoundRequestHandler());

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
						Messages.LOGGER.log(Level.FINE, Messages.CATEGORY, Messages.ERROR_UNKNOWN, e);
					}
				}
			}
		};
	}

	private void handleConnection(Socket connection) {
		try (InputStream inputStream = new BufferedInputStream(connection.getInputStream(), getBufferSize());
				OutputStream outputStream = connection.getOutputStream()) {
			// boolean keepAlive = true;

			// while (keepAlive) {
			HTTPRequest request = null;
			HTTPResponse response;
			IHTTPEncodingHandler encodingHandler = null;

			try {
				request = parseRequestHeader(inputStream);

				response = this.rootRequestHandler.process(request);

				encodingHandler = request.getEncodingHandler();

				String requestConnectionHeader = request.getHeaderField(HTTPConstants.FIELD_CONNECTION);
				String responseConnectionHeader = response.getHeaderField(HTTPConstants.FIELD_CONNECTION);

				// keepAlive = HTTPConstants.CONNECTION_FIELD_VALUE_KEEP_ALIVE
				// .equalsIgnoreCase(requestConnectionHeader)
				// && !HTTPConstants.CONNECTION_FIELD_VALUE_CLOSE.equalsIgnoreCase(responseConnectionHeader);
			} catch (HTTPErrorException e) {
				Messages.LOGGER.log(Level.INFO, Messages.CATEGORY, Messages.HTTP_ERROR,
						Integer.valueOf(connection.hashCode()), connection.getInetAddress().toString(), e.getStatus(),
						e.getMessage());
				sendError(e.getStatus(), e.getMessage(), connection);
				// continue;
				return;
			} catch (SocketTimeoutException e) {
				response = HTTPResponse.RESPONSE_REQUESTTIMEOUT;
				// keepAlive = false;
			}

			// if (keepAlive) {
			// response.addHeaderField(HTTPConstants.FIELD_CONNECTION,
			// HTTPConstants.CONNECTION_FIELD_VALUE_KEEP_ALIVE);
			// }

			Messages.LOGGER.log(Level.FINE, Messages.CATEGORY, Messages.HTTP_RESPONSE,
					Integer.valueOf(connection.hashCode()), connection.getInetAddress().toString(),
					response.getStatus(), request.getURI());

			sendResponse(response, outputStream, encodingHandler);
			// }
		} catch (IOException e) {
			// connection lost
			Messages.LOGGER.log(Level.INFO, Messages.CATEGORY, Messages.CONNECTION_LOST,
					Integer.valueOf(connection.hashCode()), connection.getInetAddress().toString());
		}
	}

	private HTTPRequest parseRequestHeader(InputStream inputStream) throws HTTPErrorException, IOException {
		HTTPRequest request;
		try {
			request = new HTTPRequest(inputStream, HTTPServer.this.encodingRegister);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			throw new HTTPErrorException(HTTPConstants.HTTP_STATUS_BADREQUEST);
		} catch (UnsupportedHTTPEncodingException e) {
			e.printStackTrace();
			// unable to decode data
			// RFC2616 / 3.6: A server which receives ... a
			// transfer-coding
			// it does not understand SHOULD return 501
			// (Unimplemented),
			// and close the connection.
			throw new HTTPErrorException(HTTPConstants.HTTP_STATUS_NOTIMPLEMENTED,
					e.field + RESPONSE_COLON + e.encoding);
		}
		return request;
	}

	/**
	 * Sends the {@link HTTPResponse} to the {@link OutputStream}.
	 *
	 */
	private void sendResponse(HTTPResponse response, OutputStream outputStream, IHTTPEncodingHandler encodingHandler) {
		try {
			if (encodingHandler != null) {
				response.addHeaderField(HTTPConstants.FIELD_CONTENT_ENCODING, encodingHandler.getId());
			}
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

				response.writeHTTPHeader(outputStream);

				if (rawData != null) {
					try (OutputStream dataOutput = this.encodingRegister.getIdentityTransferCodingHandler()
							.open(response, outputStream)) {
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
							? this.encodingRegister.getChunkedTransferCodingHandler().open(response, outputStream)
							: this.encodingRegister.getIdentityTransferCodingHandler().open(response, outputStream)) {
						try (OutputStream ecodedOutput = (encodingHandler != null) ? encodingHandler.open(dataOutput)
								: null) {
							OutputStream output = (ecodedOutput != null) ? ecodedOutput : dataOutput;
							final byte[] readBuffer = new byte[getBufferSize()];
							while (true) {
								int len = dataStream.read(readBuffer);

								if (len < 0) { // read until EOF is reached
									break;
								}
								// store read data
								output.write(readBuffer, 0, len);
								output.flush();
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
			outputStream.flush();
		} catch (IOException e) {
			// an error occurred when sending the response: can't do anything
			// more
			Messages.LOGGER.log(Level.SEVERE, Messages.CATEGORY, Messages.ERROR_UNKNOWN, e);
		}
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
	 * @throws IOException
	 * @see #sendError(String, String)
	 */
	protected void sendError(String status, Socket connection) throws IOException {
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
	 * @throws IOException
	 * @see #sendResponse(HTTPResponse, IHTTPEncodingHandler)
	 */
	protected void sendError(String status, String msg, Socket connection) throws IOException {
		sendResponse(createErrorResponse(status, msg), connection.getOutputStream(), (IHTTPEncodingHandler) null);
	}

	private int getBufferSize() {
		return Integer.getInteger(BUFFER_SIZE_PROPERTY, BUFFER_SIZE).intValue();
	}

	private static void writeAndFlush(byte[] data, OutputStream stream) throws IOException {
		stream.write(data);
		stream.flush();
		stream.close();
	}

}
