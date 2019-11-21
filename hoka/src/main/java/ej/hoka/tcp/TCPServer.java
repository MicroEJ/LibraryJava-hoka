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
import java.net.SocketException;

import javax.net.ServerSocketFactory;

import ej.hoka.log.Messages;
import ej.util.message.Level;

/**
 * <p>
 * Abstract TCP/IP server.
 * </p>
 */
public abstract class TCPServer {

	/**
	 * The server socket factory used by this server.
	 */
	private final ServerSocketFactory serverSocketFactory;

	/**
	 * The port used by this server.
	 */
	private final int port;

	/**
	 * Maximum number of opened connections.
	 */
	private final int maxOpenedConnections;

	/**
	 * The request timeout duration in ms, default = 0 (infinite).
	 */
	private final int requestTimeoutDuration;

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
	private int lastAddedPtr; // initialized to 0

	/**
	 * Pointer for the last read item.
	 */
	private int lastReadPtr; // initialized to 0

	/**
	 * <p>
	 * Constructs a new instance of {@link TCPServer} using environment's default socket factory.
	 * </p>
	 *
	 * @param port
	 *            the port to use.
	 * @param maxOpenedConnection
	 *            the maximal number of simultaneously opened connections.
	 * @param requestTimeoutDuration
	 *            the request timeout in milliseconds.
	 */
	public TCPServer(int port, int maxOpenedConnection, int requestTimeoutDuration) {
		this(port, maxOpenedConnection, requestTimeoutDuration, ServerSocketFactory.getDefault());
	}

	/**
	 * <p>
	 * Constructs a new instance of {@link TCPServer} that use {@link Socket} created by the {@link ServerSocketFactory}
	 * as the underlying connection.
	 * </p>
	 *
	 * @param port
	 *            the port to use.
	 * @param serverSocketFactory
	 *            the {@link ServerSocketFactory}.
	 * @param maxOpenedConnection
	 *            the maximal number of simultaneously opened connections.
	 * @param requestTimeoutDuration
	 *            the request timeout in milliseconds.
	 */
	public TCPServer(int port, int maxOpenedConnection, int requestTimeoutDuration,
			ServerSocketFactory serverSocketFactory) {
		if ((maxOpenedConnection <= 0) || (requestTimeoutDuration < 0)) {
			throw new IllegalArgumentException();
		}

		this.port = port;
		this.serverSocketFactory = serverSocketFactory;
		this.maxOpenedConnections = maxOpenedConnection;
		this.requestTimeoutDuration = requestTimeoutDuration;
	}

	/**
	 * <p>
	 * Starts the {@link TCPServer}. The {@link TCPServer} can be started only once. Calling this method while the
	 * {@link TCPServer} is already running causes a {@link IllegalStateException}.
	 * </p>
	 *
	 * @throws IOException
	 *             if an error occurs during the creation of the socket.
	 */
	public void start() throws IOException {
		if (this.thread != null) { // The serverSocket is closed before the thread is stopped.
			throw new IllegalStateException(
					Messages.BUILDER.buildMessage(Level.SEVERE, Messages.CATEGORY, Messages.MULTIPLE_START_FORBIDDEN));
		}

		this.streamConnections = new Socket[this.maxOpenedConnections + 1]; // always an empty index in order to
																			// distinguish between empty or full queue

		this.serverSocket = this.serverSocketFactory.createServerSocket(this.port);

		this.thread = new Thread(newProcess(), getName());
		this.thread.start();

		Messages.LOGGER.log(Level.INFO, Messages.CATEGORY, Messages.SERVER_STARTED);
	}

	/**
	 * <p>
	 * Stops the {@link TCPServer} and closes the connection.
	 * </p>
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

		Messages.LOGGER.log(Level.INFO, Messages.CATEGORY, Messages.SERVER_STOPPED);
	}

	/**
	 * <p>
	 * Returns <code>true</code> if the {@link TCPServer} is stopped.
	 * </p>
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
	 * Get the next {@link Socket} to process. Block until a new connection is available or server is stopped.
	 *
	 * @return null if server is stopped
	 */
	protected Socket getNextStreamConnection() {
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
	 * Called when a connection cannot be added to the buffer. By default, the connection is closed.
	 *
	 * @param connection
	 *            {@link Socket} that can not be added
	 */
	protected void tooManyOpenConnections(Socket connection) {
		try {
			connection.close();
		} catch (IOException e) {
			Messages.LOGGER.log(Level.SEVERE, Messages.CATEGORY, Messages.ERROR_UNKNOWN, e);
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
					try (Socket connection = TCPServer.this.serverSocket.accept()) {
						addConnection(connection);
					} catch (IOException e) {
						if (isStopped()) {
							return;
						}

						// Connection cannot be handled but server is still alive.
						// It may happen if too many requests are made simultaneously.

						Messages.LOGGER.log(Level.SEVERE, Messages.CATEGORY, Messages.ERROR_UNKNOWN, e);
					}
				}
			}
		};
	}

	/**
	 * Add a connection to the list of current connections.
	 *
	 * @param connection
	 *            {@link Socket} to add
	 * @throws SocketException
	 *             if there is an error in the underlying protocol, such as a TCP error.
	 */
	private void addConnection(Socket connection) throws SocketException {
		synchronized (this.streamConnections) {
			int nextPtr = this.lastAddedPtr + 1;
			if (nextPtr == this.streamConnections.length) {
				nextPtr = 0;
			}

			if (nextPtr == this.lastReadPtr) {
				Messages.LOGGER.log(Level.SEVERE, Messages.CATEGORY, Messages.TOO_MANY_CONNECTION,
						connection.getInetAddress().toString(), Integer.valueOf(this.maxOpenedConnections));
				tooManyOpenConnections(connection);
				return;
			}

			connection.setSoTimeout(this.requestTimeoutDuration);

			this.streamConnections[this.lastAddedPtr = nextPtr] = connection;
			this.streamConnections.notify();
		}

		Messages.LOGGER.log(Level.INFO, Messages.CATEGORY, Messages.NEW_CONNECTION,
				Integer.valueOf(connection.hashCode()), connection.getInetAddress().toString());
	}

}
