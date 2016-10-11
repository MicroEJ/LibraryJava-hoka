/*
 * Java
 *
 * Copyright 2009-2016 IS2T. All rights reserved.
 * IS2T PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package ej.hoka.tcp;

import java.io.IOException;

import ej.hoka.ip.Server;
import ej.hoka.net.IServerSocketConnection;
import ej.hoka.net.ISocketConnection;

/**
 * <p>
 * Abstract TCP/IP server.
 * </p>
 */
public abstract class TCPServer extends Server {

	/**
	 * The thread used by this server.
	 */
	private Thread thread;

	/**
	 * The server socket used by this server.
	 */
	private IServerSocketConnection serverSocket;

	/**
	 * <p>
	 * Constructs a new instance of {@link TCPServer} with {@link IServerSocketConnection} as the underlying connection.
	 * </p>
	 *
	 * @param connection
	 *            the {@link IServerSocketConnection}
	 */
	public TCPServer(IServerSocketConnection connection) {
		this.serverSocket = connection;
	}

	/**
	 * Adds a connection.
	 *
	 * @param client
	 *            the {@link ISocketConnection} to add
	 */
	protected abstract void addConnection(ISocketConnection client);

	/**
	 * Returns the current {@link IServerSocketConnection}.
	 *
	 * @return the current {@link IServerSocketConnection}
	 */
	protected IServerSocketConnection getCurrentConnection() {
		return this.serverSocket;
	}

	/**
	 * Returns the name of this TCPServer.
	 *
	 * @return the string "TCPServer"
	 */
	protected String getName() {
		return "TCPServer"; //$NON-NLS-1$
	}

	/**
	 * <p>
	 * Returns <code>true</code> if the {@link TCPServer} is stopped.
	 * </p>
	 *
	 * @return <code>true</code> if the {@link TCPServer} is stopped, <code>false</code> otherwise
	 */
	public boolean isStopped() {
		return ((this.serverSocket == null) || (this.thread == null));
	}

	/**
	 * Returns a new Server process as {@link Thread}
	 *
	 * @return a new Server process as {@link Thread}
	 */
	private Thread newProcess() {
		return new Thread(getName()) {

			@Override
			public void run() {
				IServerSocketConnection serverSocket = getCurrentConnection();
				// if server is already closed
				if (serverSocket == null) {
					return;
				}
				while (true) {
					ISocketConnection connector;
					try {
						connector = serverSocket.accept();
					} catch (IOException e) {
						if (getCurrentConnection() == null) {
							// this is the stop signal
							return;
						}
						// Connection cannot be handled but server is still
						// alive.
						// It may happen if too many requests are made
						// simultaneously

						TCPServer.this.logger.unexpectedError(e);

						continue;
					}
					addConnection(connector);
				}
			}
		};
	}

	/**
	 * <p>
	 * Starts the {@link TCPServer}. The {@link TCPServer} can be started only once. Calling this method while the
	 * {@link TCPServer} is already running causes a {@link RuntimeException}.
	 * </p>
	 */
	public void start() {
		if (this.thread != null) {
			throw new RuntimeException("No multiple start allowed"); //$NON-NLS-1$
		}
		this.thread = newProcess();
		this.thread.start();
	}

	/**
	 * <p>
	 * Stops the {@link TCPServer} and closes the connection.
	 * </p>
	 */
	public void stop() {
		try {
			IServerSocketConnection serverSocket = this.serverSocket;
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
	}
}