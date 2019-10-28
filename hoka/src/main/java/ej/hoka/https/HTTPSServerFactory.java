/*
 * Java
 *
 * Copyright 2019 MicroEJ Corp. All rights reserved.
 * This library is provided in source code for use, modification and test, subject to license terms.
 * Any modification of the source code will break MicroEJ Corp. warranties on the whole library.
 */
package ej.hoka.https;

import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;

import javax.net.ServerSocketFactory;
import javax.net.ssl.SSLContext;

import ej.hoka.http.DefaultHTTPSession;
import ej.hoka.http.HTTPServer;
import ej.hoka.http.HTTPSession;
import ej.net.util.ssl.SslContextBuilder;

/**
 * Helps to create a HTTPS server. Subclasses should override the {@link HTTPSServerFactory#newHTTPSession(HTTPServer)}
 * method to add specific session handling behavior.
 */
public abstract class HTTPSServerFactory {

	private SSLContext sslContext;

	/**
	 * Initializes the SSL context using the given key, certificate and certification chain.
	 *
	 * @param keyPath
	 *            the server private key path.
	 * @param password
	 *            the password of the file at {@code keyPath}.
	 * @param certificatePath
	 *            the {@code key} certificate path.
	 * @param certificationChainPaths
	 *            the paths to certificates in the certification chain, ordered, the root certificate at the end.
	 * @throws IOException
	 *             if an exception occurs while loading the key and the certificates.
	 * @throws GeneralSecurityException
	 *             if an exception occurs during the initialization of the SSL context.
	 */
	public void initSSLContext(String keyPath, String password, String certificatePath,
			String... certificationChainPaths) throws IOException, GeneralSecurityException {
		SslContextBuilder sslContextBuilder = new SslContextBuilder();

		InputStream key = getClass().getResourceAsStream(keyPath);
		InputStream certificate = getClass().getResourceAsStream(certificatePath);
		InputStream[] certificationChain;
		if (certificationChainPaths == null) {
			certificationChain = null;
		} else {
			certificationChain = new InputStream[certificationChainPaths.length];
			for (int i = 0; i < certificationChainPaths.length; i++) {
				certificationChain[i] = getClass().getResourceAsStream(certificationChainPaths[i]);
			}
		}
		sslContextBuilder.addClientKey(key, certificate, certificationChain);

		this.sslContext = sslContextBuilder.build(password);
	}

	/**
	 * Creates a HTTPS server on the given port, using the given maximum number of simultaneous connection and number of
	 * jobs.
	 *
	 * Before to call this method, this {@code HttpsServerFactory} must have its SSL context initialized
	 * ({@link HTTPSServerFactory#initSSLContext(String, String, String, String...)}). Otherwise, an
	 * IllegalStateException is thrown.
	 *
	 * @param port
	 *            the port to connect on.
	 * @param maxSimultaneousConnection
	 *            the maximum number of simultaneous connections to handle.
	 * @param jobCountBySession
	 *            the number of jobs to use.
	 * @return the created HTTPS server.
	 * @throws IOException
	 *             if an exception occurs during the creation of the connection.
	 */
	public HTTPServer create(int port, int maxSimultaneousConnection, int jobCountBySession) throws IOException {
		if (this.sslContext == null) {
			throw new IllegalStateException("SSL Context not initialized."); //$NON-NLS-1$
		}
		ServerSocketFactory serverSocketFactory = this.sslContext.getServerSocketFactory();
		return new HTTPServer(serverSocketFactory.createServerSocket(port), maxSimultaneousConnection,
				jobCountBySession) {
			@Override
			protected HTTPSession newHTTPSession() {
				return HTTPSServerFactory.this.newHTTPSession(this);
			}
		};
	}

	/**
	 * <p>
	 * This method should be overridden by subclasses to add functionality to the {@link HTTPServer} created by this
	 * factory.
	 * </p>
	 *
	 * @param server
	 *            the server on which the @{code HTTPSession} will be used.
	 * @return the newly created {@link HTTPSession}
	 * @see HTTPSession
	 * @see DefaultHTTPSession
	 */
	protected abstract HTTPSession newHTTPSession(HTTPServer server);

}
