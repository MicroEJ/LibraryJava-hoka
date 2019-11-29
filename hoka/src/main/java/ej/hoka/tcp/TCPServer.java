/*
 * Java
 *
 * Copyright 2009-2019 MicroEJ Corp. All rights reserved.
 * This library is provided in source code for use, modification and test, subject to license terms.
 * Any modification of the source code will break MicroEJ Corp. warranties on the whole library.
 */
package ej.hoka.tcp;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import javax.net.ServerSocketFactory;

import ej.hoka.log.Messages;
import ej.util.message.Level;

/**
 * TCP/IP server that stores incoming connections.
 * <p>
 * After starting this server with {@link #start()}, connections are available through
 * {@link #getNextStreamConnection()}.
 */
public class TCPServer {

	/**
	 * By default, server is configured to keep connection open during one minute if possible.
	 */
	private static final int DEFAULT_TIMEOUT_DURATION = 60000; // 60s

	/**
	 * The port used by this server.
	 */
	private final int port;

	/**
	 * Maximum number of opened waiting connections.
	 */
	private final int maxOpenedConnections;

	/**
	 * The server socket factory used to start this server.
	 */
	private final ServerSocketFactory serverSocketFactory;

	/**
	 * The request timeout duration in milliseconds, 0 means no timeout (infinite persistent connections).
	 */
	private final int timeout;

	/**
	 * The server socket used by this server.
	 */
	private ServerSocket serverSocket;

	/**
	 * The thread used by this server.
	 */
	private Thread thread;

	/**
	 * Non growable circular queue of opened connections.
	 */
	private Socket[] streamConnections;

	/**
	 * Pointer to the last added item.
	 */
	private int lastAddedPtr;

	/**
	 * Pointer for the last read item.
	 */
	private int lastReadPtr;

	/**
	 * Constructs a new instance of {@link TCPServer} using environment's default socket factory.
	 *
	 * @param port
	 *            the port to use.
	 * @param maxOpenedConnections
	 *            the maximal number of simultaneously opened connections.
	 */
	public TCPServer(int port, int maxOpenedConnections) {
		this(port, maxOpenedConnections, ServerSocketFactory.getDefault());
	}

	/**
	 * Constructs a new instance of {@link TCPServer} using the {@link ServerSocketFactory}
	 * <code>serverSocketFactory</code>.
	 *
	 * @param port
	 *            the port to use.
	 * @param maxOpenedConnections
	 *            the maximal number of simultaneously opened connections.
	 * @param serverSocketFactory
	 *            the {@link ServerSocketFactory}.
	 */
	public TCPServer(int port, int maxOpenedConnections, ServerSocketFactory serverSocketFactory) {
		this(port, maxOpenedConnections, serverSocketFactory, DEFAULT_TIMEOUT_DURATION);
	}

	/**
	 * Constructs a new instance of {@link TCPServer} using the {@link ServerSocketFactory}
	 * <code>serverSocketFactory</code>.
	 *
	 * @param port
	 *            the port to use.
	 * @param maxOpenedConnections
	 *            the maximal number of simultaneously opened connections.
	 * @param serverSocketFactory
	 *            the {@link ServerSocketFactory}.
	 * @param timeout
	 *            the timeout of opened connections.
	 * @see Socket#setSoTimeout(int)
	 */
	public TCPServer(int port, int maxOpenedConnections, ServerSocketFactory serverSocketFactory, int timeout) {
		if (maxOpenedConnections <= 0 || timeout < 0) {
			throw new IllegalArgumentException();
		}

		this.port = port;
		this.maxOpenedConnections = maxOpenedConnections;
		this.serverSocketFactory = serverSocketFactory;
		this.timeout = timeout;
	}

	/**
	 * Starts the {@link TCPServer}. The {@link TCPServer} can be started only once. Calling this method while the
	 * {@link TCPServer} is already running causes a {@link IllegalStateException}.
	 *
	 * @throws IOException
	 *             if an error occurs during the creation of the socket.
	 */
	public void start() throws IOException {
		if (!isStopped()) {
			throw new IllegalStateException(Messages.BUILDER.buildMessage(Level.SEVERE, Messages.CATEGORY_HOKA,
					Messages.MULTIPLE_START_FORBIDDEN));
		}

		this.streamConnections = new Socket[this.maxOpenedConnections + 1]; // always an empty index in order to
																			// distinguish between empty or full queue

		this.lastAddedPtr = 0;
		this.lastReadPtr = 0;

		this.serverSocket = this.serverSocketFactory.createServerSocket(this.port);

		this.thread = new Thread(newProcess(), getName());
		this.thread.start();

		Messages.LOGGER.log(Level.INFO, Messages.CATEGORY_HOKA, Messages.SERVER_STARTED);
	}

	/**
	 * Stops the {@link TCPServer} and closes the connection.
	 */
	public void stop() {
		try {
			ServerSocket serverSocket = this.serverSocket;
			this.serverSocket = null; // indicates the connection is being
			// closed
			try {
				serverSocket.close();
			} catch (Throwable e) {
				return; // already closed
			}
			// join the end of the thread.
			try {
				this.thread.join();
			} catch (InterruptedException e) {
				// nothing to do on interrupted exception
			}
		} finally {
			this.thread = null; // to allow server to restart after being stopped
		}

		// awake all waiting threads
		synchronized (this.streamConnections) {
			this.streamConnections.notifyAll();
		}

		Messages.LOGGER.log(Level.INFO, Messages.CATEGORY_HOKA, Messages.SERVER_STOPPED);
	}

	/**
	 * Add a connection to the list of opened connections.
	 *
	 * @param connection
	 *            {@link Socket} to add
	 */
	public void addConnection(Socket connection) {
		synchronized (this.streamConnections) {
			int nextPtr = this.lastAddedPtr + 1;
			if (nextPtr == this.streamConnections.length) {
				nextPtr = 0;
			}

			if (nextPtr == this.lastReadPtr) {
				Messages.LOGGER.log(Level.SEVERE, Messages.CATEGORY_HOKA, Messages.TOO_MANY_CONNECTION,
						connection.getInetAddress().toString(), Integer.valueOf(this.maxOpenedConnections));
				tooManyOpenConnections(connection);
				return;
			}

			Messages.LOGGER.log(Level.INFO, Messages.CATEGORY_HOKA, Messages.NEW_CONNECTION,
					Integer.valueOf(connection.hashCode()), connection.getInetAddress().toString());

			this.streamConnections[this.lastAddedPtr = nextPtr] = connection;
			this.streamConnections.notify();
		}
	}

	/**
	 * Get the next {@link Socket} to process. Block until a new connection is available or server is stopped.
	 *
	 * @return null if server is stopped
	 */
	public Socket getNextStreamConnection() {
		synchronized (this.streamConnections) {
			while (this.lastAddedPtr == this.lastReadPtr) {
				if (isStopped()) {
					return null;
				}
				try {
					this.streamConnections.wait();
				} catch (InterruptedException e) {
					// nothing to do on interrupted exception
				}
			}

			int nextPtr = this.lastReadPtr + 1;
			if (nextPtr == this.streamConnections.length) {
				nextPtr = 0;
			}
			Socket connection = this.streamConnections[this.lastReadPtr = nextPtr];
			// allow GC
			this.streamConnections[nextPtr] = null;
			return connection;
		}
	}

	/**
	 * Returns <code>true</code> if the {@link TCPServer} is stopped.
	 *
	 * @return <code>true</code> if the {@link TCPServer} is stopped, <code>false</code> otherwise
	 */
	public boolean isStopped() {
		return this.serverSocket == null || this.thread == null;
	}

	/**
	 * Returns the name of this TCPServer.
	 *
	 * @return the string "TCPServer"
	 */
	protected String getName() {
		return TCPServer.class.getSimpleName();
	}

	/**
	 * Called when a connection cannot be added to the buffer. By default, the connection is closed.
	 *
	 * @param connection
	 *            {@link Socket} that can not be added
	 */
	protected void tooManyOpenConnections(Socket connection) {
		try {
			connection.close();
		} catch (IOException e) {
			Messages.LOGGER.log(Level.SEVERE, Messages.CATEGORY_HOKA, Messages.ERROR_UNKNOWN, e);
		}
	}

	/**
	 * Returns a new Server process as {@link Runnable}.
	 *
	 * @return a new Server process as {@link Runnable}
	 */
	private Runnable newProcess() {
		return new Runnable() {
			@Override
			public void run() {
				while (true) {
					try {
						Socket connection = TCPServer.this.serverSocket.accept();
						connection.setSoTimeout(TCPServer.this.timeout);
						addConnection(connection);
					} catch (IOException e) {
						if (isStopped()) {
							return;
						}

						// Connection cannot be handled but server is still alive.
						// It may happen if too many requests are made simultaneously.

						Messages.LOGGER.log(Level.SEVERE, Messages.CATEGORY_HOKA, Messages.ERROR_UNKNOWN, e);
					}
				}
			}
		};
	}

}
