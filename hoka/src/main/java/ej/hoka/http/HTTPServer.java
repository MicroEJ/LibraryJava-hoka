/*
 * Java
 *
 * Copyright 2009-2019 MicroEJ Corp. All rights reserved.
 * This library is provided in source code for use, modification and test, subject to license terms.
 * Any modification of the source code will break MicroEJ Corp. warranties on the whole library.
 */
package ej.hoka.http;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import javax.net.ServerSocketFactory;

import ej.hoka.tcp.TCPServer;

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
	protected final long keepAliveDuration;

	/**
	 * Array of {@link Thread}s for the session jobs.
	 */
	private Thread[] jobs;

	/**
	 * Array of {@link IHTTPEncodingHandler}s.
	 */
	private IHTTPEncodingHandler[] encodingHandlers;

	/**
	 * Array of {@link IHTTPTransferCodingHandler}s.
	 */
	private IHTTPTransferCodingHandler[] transferCodingHandlers;

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
	public HTTPServer(int port, int maxSimultaneousConnection, int jobCountBySession) throws IOException {
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
			ServerSocketFactory serverSocketFactory) throws IOException {
		this(port, maxSimultaneousConnection, jobCountBySession, serverSocketFactory, DEFAULT_KEEP_ALIVE_DURATION, 0);
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
			ServerSocketFactory serverSocketFactory, long keepAliveDuration, int requestTimeoutDuration)
			throws IOException {
		super(port, maxSimultaneousConnection, requestTimeoutDuration, serverSocketFactory);

		if ((jobCountBySession <= 0) || (keepAliveDuration <= 0)) {
			throw new IllegalArgumentException();
		}

		// FIXME for memory usage only
		// r = Runtime.getRuntime();

		this.sessionJobsCount = jobCountBySession;
		this.keepAliveDuration = keepAliveDuration;

		// connect well known encoding handlers
		this.encodingHandlers = new IHTTPEncodingHandler[] { IdentityEncodingHandler.getInstance() };

		this.transferCodingHandlers = new IHTTPTransferCodingHandler[] { IdentityTransferCodingHandler.getInstance(),
				ChunkedTransferCodingHandler.getInstance() };
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

	/**
	 * Return the {@link IHTTPEncodingHandler} corresponding to chunked transfer coding.
	 *
	 * @return Return the {@link IHTTPEncodingHandler} corresponding to chunked transfer coding
	 */
	protected IHTTPTransferCodingHandler getChunkedTransferCodingHandler() {
		return ChunkedTransferCodingHandler.getInstance();
	}

	/**
	 * Return the {@link IHTTPEncodingHandler} corresponding to the given encoding.
	 *
	 * @param encoding
	 *            case insensitive (See RFC2616, 3.5)
	 * @return null if no handler has been registered to match this encoding
	 */
	protected IHTTPEncodingHandler getEncodingHandler(String encoding) {
		if (encoding == null) {
			return IdentityEncodingHandler.getInstance();
		}
		IHTTPEncodingHandler[] encodingHandlers = this.encodingHandlers;
		for (int i = encodingHandlers.length; --i >= 0;) {
			IHTTPEncodingHandler handler = encodingHandlers[i];
			if (encoding.equalsIgnoreCase(handler.getId())) {
				return handler;
			}
		}
		return null;
	}

	/**
	 * Return the {@link IHTTPEncodingHandler} corresponding to identity transfer coding (i.e. no transfer coding)
	 *
	 * @return Return the {@link IHTTPEncodingHandler} corresponding to identity transfer coding (i.e. no transfer
	 *         coding)
	 */
	protected IHTTPTransferCodingHandler getIdentityTransferCodingHandler() {
		return IdentityTransferCodingHandler.getInstance();
	}

	/**
	 * Return the {@link IHTTPEncodingHandler} corresponding to the given encoding.
	 *
	 * @param encoding
	 *            case insensitive (See RFC2616, 3.5)
	 * @return null if no handler has been registered to match this encoding
	 */
	protected IHTTPTransferCodingHandler getTransferCodingHandler(String encoding) {
		if (encoding == null) {
			return IdentityTransferCodingHandler.getInstance();
		}
		IHTTPTransferCodingHandler[] transferCodingHandlers = this.transferCodingHandlers;
		for (int i = transferCodingHandlers.length; --i >= 0;) {
			IHTTPTransferCodingHandler handler = transferCodingHandlers[i];
			if (encoding.equalsIgnoreCase(handler.getId())) {
				return handler;
			}
		}
		return null;
	}

	/**
	 * <p>
	 * Registers a new HTTP content encoding handler.
	 * </p>
	 * <p>
	 * Should be called before {@link #start()}, otherwise a {@link IllegalStateException} is thrown.
	 * </p>
	 *
	 * @param handler
	 *            the {@link IHTTPEncodingHandler} to register
	 */
	public void registerEncodingHandler(IHTTPEncodingHandler handler) {
		if (!isStopped()) {
			throw new IllegalStateException();
		}
		if (this.encodingHandlers == null) {
			this.encodingHandlers = new IHTTPEncodingHandler[] { handler };
		} else {
			int length = this.encodingHandlers.length;
			System.arraycopy(this.encodingHandlers, 0, this.encodingHandlers = new IHTTPEncodingHandler[length + 1], 0,
					length);
			this.encodingHandlers[length] = handler;
		}
	}

	/**
	 * <p>
	 * Registers a new HTTP transfer coding handler.
	 * </p>
	 * <p>
	 * Should be called before {@link #start()}, otherwise a {@link IllegalStateException} is raised.
	 * </p>
	 *
	 * @param handler
	 *            the {@link IHTTPTransferCodingHandler} to register
	 */
	public void registerTransferCodingHandler(IHTTPTransferCodingHandler handler) {
		if (!isStopped()) {
			throw new IllegalStateException();
		}
		if (this.transferCodingHandlers == null) {
			this.transferCodingHandlers = new IHTTPTransferCodingHandler[] { handler };
		} else {
			int length = this.transferCodingHandlers.length;
			System.arraycopy(this.transferCodingHandlers, 0,
					this.transferCodingHandlers = new IHTTPTransferCodingHandler[length + 1], 0, length);
			this.transferCodingHandlers[length] = handler;
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
					try (Socket connection = HTTPServer.super.getNextStreamConnection()) {
						// TODO Process request with request handlers
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		};
	}

}
