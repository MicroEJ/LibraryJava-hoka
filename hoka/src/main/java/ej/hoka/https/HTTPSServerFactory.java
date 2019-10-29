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
import java.net.ServerSocket;
import java.security.GeneralSecurityException;

import javax.net.ServerSocketFactory;
import javax.net.ssl.SSLContext;

import ej.hoka.http.HTTPServer;
import ej.net.util.ssl.SslContextBuilder;

/**
 * Helps to create a HTTPS server.
 */
public class HTTPSServerFactory {

	private final SSLContext sslContext;

	/**
	 * Sets the SSL context using the given key, certificate and certification chain.
	 *
	 * The key and the certificates are loaded using {@link Class#getResourceAsStream(String)}.
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
	public HTTPSServerFactory(String keyPath, String password, String certificatePath,
			String... certificationChainPaths) throws IOException, GeneralSecurityException {
		InputStream[] certificationChain = null;
		try (InputStream key = getClass().getResourceAsStream(keyPath);
				InputStream certificate = getClass().getResourceAsStream(certificatePath)) {
			if (certificationChainPaths != null) {
				certificationChain = new InputStream[certificationChainPaths.length];
				for (int i = 0; i < certificationChainPaths.length; i++) {
					certificationChain[i] = getClass().getResourceAsStream(certificationChainPaths[i]);
				}
			}
			this.sslContext = initSSLContext(key, password, certificate, certificationChain);
		} finally {
			if (certificationChain != null) {
				for (int i = 0; i < certificationChain.length; i++) {
					certificationChain[i].close();
				}
			}
		}
	}

	/**
	 * Sets the SSL context using the given key, certificate and certification chain.
	 *
	 * @param key
	 *            the server private key.
	 * @param password
	 *            the password of {@code key}.
	 * @param certificate
	 *            the {@code key} certificate.
	 * @param certificationChain
	 *            the certificates in the certification chain, ordered, the root certificate at the end.
	 * @throws IOException
	 *             if an exception occurs while loading the key and the certificates.
	 * @throws GeneralSecurityException
	 *             if an exception occurs during the initialization of the SSL context.
	 */
	public HTTPSServerFactory(InputStream key, String password, InputStream certificate,
			InputStream... certificationChain) throws IOException, GeneralSecurityException {
		this.sslContext = initSSLContext(key, password, certificate, certificationChain);
	}

	private SSLContext initSSLContext(InputStream key, String password, InputStream certificate,
			InputStream... certificationChain) throws IOException, GeneralSecurityException {
		SslContextBuilder sslContextBuilder = new SslContextBuilder();
		sslContextBuilder.addClientKey(key, certificate, certificationChain);
		return sslContextBuilder.build(password);
	}

	/**
	 * Creates a HTTPS server on the given port, using the given maximum number of simultaneous connection and number of
	 * jobs.
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
		ServerSocketFactory serverSocketFactory = this.sslContext.getServerSocketFactory();
		ServerSocket serverSocket = serverSocketFactory.createServerSocket(port);
		return new HTTPServer(serverSocket, maxSimultaneousConnection, jobCountBySession);
	}

}
