/**
 * Java
 *
 * Copyright 2009-2015 IS2T. All rights reserved.
 * IS2T PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.is2t.server.http;

import java.io.IOException;

import com.is2t.connector.net.IServerSocketConnection;
import com.is2t.connector.net.ISocketConnection;
import com.is2t.server.http.encoding.IHTTPEncodingHandler;
import com.is2t.server.http.encoding.IHTTPTransferCodingHandler;
import com.is2t.server.http.encoding.impl.ChunkedTransferCodingHandler;
import com.is2t.server.http.encoding.impl.IdentityEncodingHandler;
import com.is2t.server.http.encoding.impl.IdentityTransferCodingHandler;
import com.is2t.server.tcp.TCPServer;

/**
 * IS2T-API
 * <p>
 * Abstract HTTP Server. Subclasses should override the
 * {@link HTTPServer#newHTTPSession()} method to add specific session handling
 * behaviour.
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
 * Override {@link HTTPSession#answer(HTTPRequest)} and redefine the server
 * behaviour for your own application
 * 
 * </p>
 * 
 * <p>
 * <b>Example:</b>
 * </p>
 * 
 * <pre>
 * // get a new server which handle a Default HTTP Session
 * HTTPServer server = new HTTPServer(serverSocket, 10, 1) {
 * 	protected HTTPSession newHTTPSession() {
 * 		return new DefaultHTTPSession(this);
 * 	}
 * };
 * 
 * // start the server
 * server.start();
 * </pre>
 */
public abstract class HTTPServer extends TCPServer {
	/*
	 * Implementation notes (some informations may be extracted in documentation or example)
	 * <p><b>Features + limitations: </b><ul>
	 * 
	 * <li> CLDC 1.1 </li>
	 * <li> No fixed configuration files, logging, authorization, encryption etc. (Implement yourself if you need them.) </li>
	 * <li> Supports parameter parsing of GET and POST methods </li>
	 * <li> Supports both dynamic content and file serving </li>
	 * <li> Never caches anything </li>
	 * <li> Doesn't limit bandwidth, request time or simultaneous connections </li>
	 * <li> Contains a built-in list of most common MIME types </li>
	 * <li> All header names are converted in lower case so they don't vary between browsers/clients </li>
	 * 
	 * </ul>
	 * 
	 * <p><b>Ways to use: </b><ul>
	 * 
	 * <li> Instantiate the {@link HTTPServer} with wanted end point address and port </li>
	 * <li> Override {@link HTTPSession#answer(String, String, java.util.Hashtable, java.util.Hashtable) HTTPSession.answer()} and redefine the server
	 * behavior for your own application </li>
	 * 
	 * </ul>
	 */
	
	// FIXME for memory usage only
	// public Runtime r;

	/**
	 * IS2T-API
	 * <p>
	 * Creates a {@link HTTPServer} using the given
	 * {@link IServerSocketConnection} .
	 * </p>
	 * <p>
	 * The default encoding to be used is the identity encoding. Further
	 * encodings may be registered using
	 * {@link #registerEncodingHandler(IHTTPEncodingHandler)}.
	 * </p>
	 * <p>
	 * Server is not started until {@link #start()} is called.
	 * </p>
	 * 
	 * @param connection
	 *            the {@link IServerSocketConnection} connection used by the
	 *            server
	 * @param maxSimultaneousConnection
	 *            the maximal number of simultaneously opened connections.
	 * @param jobCountBySession
	 *            the number of parallel jobs to process by opened sessions. if
	 *            <code>jobCountBySession</code> == 1, the jobs are processed
	 *            sequentially.
	 */
	public HTTPServer(IServerSocketConnection connection,
			int maxSimultaneousConnection, int jobCountBySession) {
		this(connection, maxSimultaneousConnection, jobCountBySession,
				DEFAULT_KEEP_ALIVE_DURATION);
	}

	/**
	 * IS2T-API
	 * <p>
	 * Registers a new HTTP content encoding handler.
	 * </p>
	 * <p>
	 * Should be called before {@link #start()}, otherwise a
	 * {@link RuntimeException} is thrown.
	 * </p>
	 * 
	 * @param handler
	 *            the {@link IHTTPEncodingHandler} to register
	 */
	public void registerEncodingHandler(IHTTPEncodingHandler handler) {
		if (!isStopped()) {
			throw new RuntimeException();
		}
		if (encodingHandlers == null) {
			encodingHandlers = new IHTTPEncodingHandler[] { handler };
		} else {
			int length = encodingHandlers.length;
			System.arraycopy(encodingHandlers, 0,
					encodingHandlers = new IHTTPEncodingHandler[length + 1], 0,
					length);
			encodingHandlers[length] = handler;
		}
	}

	/**
	 * IS2T-API
	 * <p>
	 * Registers a new HTTP transfer coding handler.
	 * </p>
	 * <p>
	 * Should be called before {@link #start()}, otherwise a
	 * {@link RuntimeException} is raised.
	 * </p>
	 * 
	 * @param handler
	 *            the {@link IHTTPTransferCodingHandler} to register
	 */
	public void registerTransferCodingHandler(IHTTPTransferCodingHandler handler) {
		if (!isStopped()) {
			throw new RuntimeException();
		}
		if (transferCodingHandlers == null) {
			transferCodingHandlers = new IHTTPTransferCodingHandler[] { handler };
		} else {
			int length = transferCodingHandlers.length;
			System
					.arraycopy(
							transferCodingHandlers,
							0,
							transferCodingHandlers = new IHTTPTransferCodingHandler[length + 1],
							0, length);
			transferCodingHandlers[length] = handler;
		}
	}

	/**
	 * IS2T-API
	 * <p>
	 * This method should be overridden by subclasses to add functionality to the {@link HTTPServer}.
	 * </p>
	 * 
	 * @return the newly created {@link HTTPSession}
	 * @see HTTPSession
	 * @see DefaultHTTPSession
	 */
	protected abstract HTTPSession newHTTPSession();

	/**
	 * IS2T-API
	 * <p>
	 * Start the {@link HTTPServer} (in a dedicated thread): start listening for
	 * connections and start session jobs.<br>
	 * Multiple start is not allowed.
	 * </p>
	 */
	public void start() {
		streamConnections = new ISocketConnection[maxOpenedConnections + 1]; // always
		// an empty index in order to distinguish between empty or full queue
		super.start();
		// start jobs

		// FIXME for mem test only
		// r = Runtime.getRuntime();

		jobs = new Thread[sessionJobsCount];
		// r.gc();
		// System.out.println((new Date()).getTime()+", "+(r.totalMemory() -
		// r.freeMemory())+", beginning of server.start()" );
		for (int i = sessionJobsCount; --i >= 0;) {
			Thread job = new Thread(newHTTPSession().getRunnable(), "HTTP-JOB-"
					+ i);
			// FIXME for mem test only
			// r.gc();
			// System.out.println((new Date()).getTime()+", "+(r.totalMemory() -
			// r.freeMemory())+", job no"+(nbSessionJobs-i));

			jobs[i] = job;
			job.start();
		}
		logger.serverStarted();
	}

	/**
	 * IS2T-API
	 * <p>
	 * Stops the {@link HTTPServer}. Stops listening for connections. This method blocks until all session jobs are stopped.
	 * </p>
	 */
	public void stop() {
		super.stop();
		// awake all waiting threads
		synchronized (streamConnections) {
			streamConnections.notifyAll();
		}

		for (int i = jobs.length; --i >= 0;) {
			try {
				jobs[i].join();
			} catch (InterruptedException e) {
			}
		}
		logger.serverStopped();
	}

	/************************************************************************************
	 * NOT IN API
	 ***********************************************************************************/

	/**
	 * Non growable circular queue of opened connections
	 */
	private ISocketConnection[] streamConnections;

	/**
	 * Pointer to the last added item.
	 */
	private int lastAddedPtr; // initialized to 0

	/**
	 * Pointer for the last read item.
	 */
	private int lastReadPtr; // initialized to 0

	/**
	 * Maximum number of opened connections.
	 */
	private final int maxOpenedConnections;

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
	 * By default, server is configured to keep connection open during one
	 * minute if possible (implying that the browser is able to manage
	 * persistent connections).
	 */
	private static final long DEFAULT_KEEP_ALIVE_DURATION = 60000; // 60s in

	// milliseconds
	// TODO
	// handling
	// persistent
	// connection

	/**
	 * Constructs a new instance of {@link HTTPServer}.<br>
	 * The encoding handler will be {@link IdentityEncodingHandler}. The
	 * transfer coding handler will be {@link IdentityTransferCodingHandler}
	 * 
	 * @param connection
	 *            the {@link IServerSocketConnection}
	 * @param maxSimultaneousConnection
	 *            number of maximum simultaneous connections the server can
	 *            handle
	 * @param jobCountBySession
	 *            the number of jobs per session
	 * @param keepAliveDuration
	 *            the keep alive duration (not used)
	 * @throws IllegalArgumentException
	 *             when any of the following is true:
	 *             <ul>
	 *             <li><code>maxSimultaneousConnection</code><= 0
	 *             <li><code>jobCountBySession</code><= 0
	 *             <li><code>jobCountBySession</code><=0
	 *             <li><code>keepAliveDuration</code><=0
	 *             </ul>
	 */
	private HTTPServer(IServerSocketConnection connection,
			int maxSimultaneousConnection, int jobCountBySession,
			long keepAliveDuration) {
		super(connection);

		if (maxSimultaneousConnection <= 0 || jobCountBySession <= 0
				|| keepAliveDuration <= 0) {
			throw new IllegalArgumentException();
		}

		// FIXME for memory usage only
		// r = Runtime.getRuntime();

		this.maxOpenedConnections = maxSimultaneousConnection;
		this.sessionJobsCount = jobCountBySession;
		this.keepAliveDuration = keepAliveDuration; // TODO handling persistent
		// connection

		// connect well known encoding handlers
		encodingHandlers = new IHTTPEncodingHandler[] { IdentityEncodingHandler
				.getInstance() };

		transferCodingHandlers = new IHTTPTransferCodingHandler[] {
				IdentityTransferCodingHandler.getInstance(),
				ChunkedTransferCodingHandler.getInstance() };
	}

	/**
	 * Return the {@link IHTTPEncodingHandler} corresponding to the given
	 * encoding.
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
	 * Return the {@link IHTTPEncodingHandler} corresponding to the given
	 * encoding.
	 * 
	 * @param encoding
	 *            case insensitive (See RFC2616, 3.5)
	 * @return null if no handler has been registered to match this encoding
	 */
	protected IHTTPTransferCodingHandler getTransferCodingHandler(
			String encoding) {
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
	 * Return the {@link IHTTPEncodingHandler} corresponding to chunked transfer
	 * coding.
	 * 
	 * @return Return the {@link IHTTPEncodingHandler} corresponding to chunked
	 *         transfer coding
	 */
	protected IHTTPTransferCodingHandler getChunkedTransferCodingHandler() {
		return ChunkedTransferCodingHandler.getInstance();
	}

	/**
	 * Return the {@link IHTTPEncodingHandler} corresponding to identity
	 * transfer coding (i.e. no transfer coding)
	 * 
	 * @return Return the {@link IHTTPEncodingHandler} corresponding to identity
	 *         transfer coding (i.e. no transfer coding)
	 */
	protected IHTTPTransferCodingHandler getIdentityTransferCodingHandler() {
		return IdentityTransferCodingHandler.getInstance();
	}

	/**
	 * Called by HTTPSession. Get a next {@link ISocketConnection} to process.
	 * Block until a new connection is available or server is stopped.
	 * 
	 * @return null if server is stopped
	 */
	protected ISocketConnection getNextStreamConnection() {
		synchronized (streamConnections) {
			if (lastAddedPtr == lastReadPtr) {
				if (isStopped()) {
					return null;
				}
				try {
					streamConnections.wait();
				} catch (InterruptedException e) {
				}
				if (isStopped()) {
					return null; // notifyAll from close()
				}
			}

			int nextPtr = lastReadPtr + 1;
			if (nextPtr == streamConnections.length) {
				nextPtr = 0;
			}
			ISocketConnection connection = streamConnections[lastReadPtr = nextPtr];
			// allow GC
			streamConnections[nextPtr] = null;
			return connection;
		}
	}

	/**
	 * Add a connection to the list of current connections.
	 * 
	 * @param connection
	 *            {@link ISocketConnection} to add
	 */
	protected void addConnection(ISocketConnection connection) {
		// FIXME for memory usage only
		// r.gc();
		// System.out.println((new Date()).getTime()+", "+(r.totalMemory() -
		// r.freeMemory())+", start of addConnection");

		synchronized (streamConnections) {
			int nextPtr = lastAddedPtr + 1;
			if (nextPtr == streamConnections.length) {
				nextPtr = 0;
			}

			if (nextPtr == lastReadPtr) {
				tooManyOpenConnections(connection);
				return;
			}

			streamConnections[lastAddedPtr = nextPtr] = connection;
			streamConnections.notify();
		}
		logger.newConnection(connection);
		// FIXME for memory usage only
		// r.gc();
		// System.out.println((new Date()).getTime()+", "+(r.totalMemory() -
		// r.freeMemory())+", end of addConnection");
	}

	/**
	 * Called when a connection cannot be added to the buffer. By default, an
	 * event is logged and connection is closed.
	 * 
	 * @param connection
	 *            {@link ISocketConnection} that can not be added
	 */
	protected void tooManyOpenConnections(ISocketConnection connection) {
		logger.tooManyOpenConnections(maxOpenedConnections, connection);
		try {
			connection.close();
		} catch (IOException e) {
		}
	}
}
