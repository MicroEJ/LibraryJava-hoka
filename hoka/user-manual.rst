.. Copyright 2019-2020 MicroEJ Corp. All rights reserved.
.. This library is provided in source code for use, modification and test, subject to license terms.
.. Any modification of the source code will break MicroEJ Corp. warranties on the whole library.

================
Hoka User Manual
================

Introduction
------------

Scope
~~~~~

This document explains how the Hoka HTTP server can be facilitated to create
web interfaces or M2M capabilities for embedded applications. This manual is
made for the version 7.0.0. For later versions updates, see `changelog
<../CHANGELOG.md>`_.

Intended audience
~~~~~~~~~~~~~~~~~

The intended audience for this document are Java developers who are familiar
with socket communication, the HTTP 1.1 protocol and web server concepts.

Overview
~~~~~~~~

Hoka is an addon library that provides a tiny footprint yet extensible web
server. It also includes tools for REST services and for authentication.

The server is based on the ``java.net.Socket`` API available in the
``ej.api.net`` library.

  This means it is possible to use a secure socket to use the
  server in HTTPS mode (see the library ``ej.api.ssl`` for secure socket
  protocol implementation).

Connection flow
---------------

Upcoming connections
~~~~~~~~~~~~~~~~~~~~

The first class to consider is ``ej.hoka.tcp.TCPServer``. This class is
responsible for maitaining (start and stop) the server socket thread. This
thread accepts new connections and store them in a waiting array. The
maximum number of waiting connections is a parameter. Beyond this amount,
the new upcoming connections sockets are directly closed.

  It is possible to change this behavior by overriding
  ``TCPServer#tooManyOpenConnections(Socket)``.

Connection processing
~~~~~~~~~~~~~~~~~~~~~

After populating the waiting connections array, the method
``TCPServer#getNextStreamConnection()`` returns one socket from the available
connections.

The class ``HTTPServer``, which is also the entry point of this library,
manages jobs that will repeatedly call this method and process the connection
by parsing the HTTP request, processing it, and replying with the appropriate
response.

The number of jobs is a parameter that is used to configure the number of
request processed concurrently. Note that one user (one browser) can send
multiple connections at a time. To prevent idling connections to lock a job,
the ``TCPServer`` has a socket timeout parameter to limit the time a job is
waiting for the client to send data.

  It is the responsability of these jobs to properly close the I/O connections
  associated with the processed socket at the end of the HTTP protocol.

Request parser / Response builder
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

As said in the previous part, the jobs parse the HTTP request and send a
response. To do so, it uses the classes ``HTTPRequest`` and ``HTTPResponse``.
The two uses the ``ej.hoka.http.encoding`` to read/write in the correct
encoding. Available encodings are **identity** for encodings and **identity**
and **chunked** for transfer codings.

  In case the application requires to support other encodings,
  implement the associated handlers and register them in the
  ``HTTPEncodingRegistry`` used by the server.

Request processing
~~~~~~~~~~~~~~~~~~

After the request is parsed and before the response is sent, the
``HTTPResponse`` is constructed based on the ``HTTPRequest``. This
construction is done by request handlers.

  It is possible to define its own handler or to use provided ones
  like the REST request handler or the ``ResourceRequestHandler``.

The design of this processing chain is to use several implementations of the
``RequestHandler`` interface, hierarchically organized (see
``RequestHandlerComposite``) so that the different use cases can be defined
separately. A request handler can delegate the processing of the request to
other handlers by returning ``null``. The list of handlers is ordered : the
hierarchy is only browsed until one handler processes the request. Also, the
handlers can communicate one to another through a ``Map<String, String>`` of
attributes.

Setup a server
--------------

As previously said, the entry point of this library is the ``HTTPServer``
class. It has a couple of *public* constructors :

- ``public HTTPServer(int port, int maxSimultaneousConnection, int jobCount)``
- ::

    public HTTPServer(int port, int maxSimultaneousConnection, int jobCount, RequestHandler requestHandler)

- ::

    public HTTPServer(int port, int maxSimultaneousConnection, int jobCount, RequestHandler requestHandler, ServerSocketFactory serverSocketFactory)

- ::

    public HTTPServer(int port, int maxSimultaneousConnection, int jobCount, RequestHandler requestHandler, ServerSocketFactory serverSocketFactory, int keepAliveDuration)

- ::

    public HTTPServer(TCPServer tcpServer, int jobCount, RequestHandler requestHandler)

- ::

    public HTTPServer(TCPServer tcpServer, int jobCount, RequestHandler requestHandler, HTTPEncodingRegistry encodingRegistry)

The 4 following parameters are used by the underlying ``TCPServer`` :

- ``port`` : the port number to bind the server socket on. Typically,
  80 for HTTP and 443 for HTTPS.
- ``maxSimultaneousConnection`` : the size of the waiting connections
  array.
- ``serverSocketFactory`` : the factory used to create a socket at startup.
  A factory that creates secure socket wrappers for HTTPS can be created by
  the ``ej.api.ssl`` library (see ``javax.net.ssl.SSLContext``). This
  parameter is **optional**, its default value is the result of
  ``ServerSocketFactory.getDefault()``.
- ``keepAliveDuration`` : used as the ``timeout`` parameter of
  ``TCPServer``, in milliseconds, connections that fails to send the
  request within the timeout limit are closed after a "408 Request
  Timeout" response is sent. This parameter is **optional**, its default
  value is 60s.

They can also be used to create an instance of ``TCPServer``, then passed in
one of the last two constructors.

The other 3 parameters are used by the ``HTTPServer`` class to manage
its jobs or by the jobs to process the requests :

- ``jobCount`` : the number of jobs (threads) used to process upcoming
  requests. This parameter limits the maximum number of requests
  concurrently processed.
- ``requestHandler`` : the application request handler used to process the
  request. It is added to the hierarchy of other internal handlers
  described later in this document. This parameter is **optional**, its
  default value is an instance of ``ResourceRequestHandler`` that tries to
  match the requests URIs with the Java application resources available in
  "/hoka/", loaded with ``Class.getResourceAsStream(String)``. When the
  matched resource is a directory, its index file is loaded if it exists.
  Directory traversal are refused.
- ``encodingRegistry`` : the registry of encoding and transfer-coding
  handlers available to parse the requests and send the responses. Use this
  parameter to provide the server with new encoding handlers.

Another parameter is used for debug : the boolean ``sendStackTraceOnException``
has a getter and a setter methods. If it is ``true``, when an exception occurs
during the process of a request, the stack trace is sent in a plain text
response. This is useful when developing the web application, otherwise, a "500
Internal Error" response is sent.

When manually creating the ``TCPServer``, it is possible to redefine the name
of the server thread by overriding ``getName()`` and the behavior in case the
waiting connections array is full by overriding
``tooManyOpenConnections(Socket)``.

Finally, both the ``TCPServer`` and the ``HTTPServer`` have a ``start()`` and
a ``stop()`` methods. Do not call the ``start()`` method twice unless the
``stop()`` method is called between the two calls. Even though the
``TCPServer#stop()`` method also stops the ``HTTPServer``, it is recommended to
call the ``start()`` and ``stop()`` methods directly on the ``HTTPServer``
object.

  Note that the server socket is bound to the given port only upon call to the
  ``start()`` method and unbound upon call to the ``stop()`` method.

The following snippet is an example of a simple server setup :

.. code-block:: java

  // Constants
  int PORT = 80;
  int MAX_CONNECTIONS = 10;
  int JOBS = 3;

  // Initialize the server
  HTTPServer server = new HTTPServer(PORT, MAX_CONNECTIONS, JOBS);

  try {
      // Start the server
      server.start();

      // ...

      // Stop the server
      server.stop();
  } catch (IOException e) {
      // Handle the exception
  }


Develop services
----------------

During the initialization of the server, it was mentioned an application
request handler can be defined to control the processing of the requests. This
is the entry point to develop the different services provided by the web
application.

Internal request handling
~~~~~~~~~~~~~~~~~~~~~~~~~

As previously said, the request handler mechanism is designed to be used in a
hierarchical manner. Internally, the request is processed successively by :

- a ``IfNoneMatchRequestHandler`` that handles requests with a
  ``If-None-Match`` header and sends a "304 Not Modified" response, whatever
  the resource fingerprint is to enable browser caching. Do not cache
  dynamic files, they won't be detected as modified and, therefore, the
  server won't send the new content.
- the application request handler
- a ``NotFoundRequestHandler`` that handles all requests not handled by the
  two previous handlers and sends a "404 Not Found" response.

Request handler hierarchy
~~~~~~~~~~~~~~~~~~~~~~~~~

The loop over the handlers is implemented in ``RequestHandlerComposite``. Use
it recursively to define a hierarchical handler :

.. code-block:: java

  RequestHandlerComposite root = new RequestHandlerComposite();

  RequestHandlerComposite node = new RequestHandlerComposite();
  node.addRequestHandler(aRequestHandler);
  node.addRequestHandler(anotherRequestHandler);

  root.addRequestHandler(node);
  root.addRequestHandler(yetAnotherRequestHandler);

Note that it is possible to add new services to an existing
``RequestHandlerComposite`` while the server is running. After adding a
service, it will become available for next processed requests.

By splitting the web application into multiples, leaf handlers can be
relatively simple and serve only one resource (or a list of similar
resources) in a single way.

Request handler template
~~~~~~~~~~~~~~~~~~~~~~~~

Typically, such a request handler will :

#. Retrieve relevant data from the request :

   - ``HTTPRequest#getMethod()`` : the request method (1 for ``POST``, 2 for
     ``GET``, 3 for ``PUT`` and 4 for ``DELETE``), other methods are not
     supported and the server replies with a "400 Bad Request" in case the
     method specified is not one of the 4 supported methods.
   - ``HTTPRequest#getURI()`` : the URI requested.
   - ``HTTPRequest#getParameters()`` : the parameters parsed from the query
     of the request.
   - ``HTTPRequest#getVersion()`` : the HTTP version of the request.
   - ``HTTPRequest#getHeader()`` : the parsed headers, all header field names
     are converted to lowercase.
   - ``HTTPRequest#getHeaderField(String)`` : the value of the header with
     given name.
   - ``HTTPRequest#getCookies()`` : the (lazily) parsed cookies.
   - ``HTTPRequest#getCookie(String)`` : the value of the cookie with
     given name. Inits the parsing of all cookies.
   - ``HTTPRequest#parseBody(BodyParser)`` : parses the body of the request
     with the given parser.

#. Match the request against the type of requests it handles.
#. If not matched, return ``null`` to delegate the process of the request.
#. Build a ``HTTPResponse`` based on the request with the following data :

   - ``data`` : the body of the response as a ``byte[]`` or as an
     ``InputStream``.
   - ``status`` : the status of the response to send.
   - ``mimeType`` : the value of the ``content-type`` header.
   - ``HTTPRequest#addHeaderField(String, String)`` : adds a header with
     given name and value.

Body parsing
~~~~~~~~~~~~

The ``HTTPRequest#parseBody(BodyParser)`` is used to parse the body of a
request. Prior to a call to this method, the stream is not consumed. Then,
the ``BodyParser`` implementation parses the stream and outputs the body in
the custom form. 4 implementations of ``BodyParser`` are provided by the
library :

- ``StringBodyParser`` : read the whole body into a string.
- ``MultipartStringsParser`` : parse a ``multipart/*`` body, each part read
  into a string.
- ``MultiPartBodyParser`` : parse a ``multipart/*`` body, and parse each part
  as header fields and an ``InputStream`` body.
- ``ParameterParser`` : parse a ``application/x-www-form-urlencoded`` body.

MIME types
~~~~~~~~~~

The ``MIMEUtils`` class provides constant values for commonly used MIME types
and utility methods to return the MIME type of a resource name based on file
extensions.

The predefined MIME types are :

- MIME_PLAINTEXT = "text/plain"
- MIME_HTML = "text/html"
- MIME_XML = "text/xml"
- MIME_DEFAULT_BINARY = "application/octet-stream"
- MIME_CSS = "text/css"
- MIME_PNG = "image/png"
- MIME_JPEG = "image/jpeg"
- MIME_GIF = "image/gif"
- MIME_JS = "application/x-javascript"
- MIME_FORM_ENCODED_DATA = "application/x-www-form-urlencoded"
- MIME_MULTIPART_FORM_ENCODED_DATA = "multipart/form-data"

The method ``getMIMEType(String uri)`` returns the MIME
type of the given uri, assuming that the file extension in the uri was
previously registered with the
``mapFileExtensionToMIMEType(String fileExtension, String mimeType)``.
Only lower case file extensions are recognized.

For example, calling ``getMIMEType("/images/logo.png")`` will return the string
``"image/png"``.

The following table shows the predefined assignments between file extensions
and MIME types:

========= =========
Extension MIME type
========= =========
".png"    ``MIME_PNG``
".css"    ``MIME_CSS``
".gif"    ``MIME_GIF``
".jpeg"   ``MIME_JPEG``
".jpg"    ``MIME_JPEG``
".html"   ``MIME_HTML``
".htm"    ``MIME_HTML``
".js"     ``MIME_JS``
".txt"    ``MIME_PLAINTEXT``
".xml"    ``MIME_XML``
========= =========

The method
``boolean mapFileExtensionToMIMEType(String fileExtension, String mimeType)``
can be used to add further file extension /
MIME type assignments. The MIME type given in the parameter ``mimeType`` will
be assigned to the extension ``fileExtension``.

Examples
~~~~~~~~

The following snippet is an example of a simple request handler
implementation :

.. code-block:: java

  @Override
  public HTTPResponse process(HTTPRequest request, Map<String, String> attributes) {
      // Step 1

      // Use the URI as the path of the resource
      String uri = request.getURI();

      // Step 2

      // Load the targeted resource
      InputStream resource = getClass().getResourceAsStream(uri);

      // Step 3

      // If the targeted resource doesn't exist, do not process the request.
      if (resource == null) {
          return null;
      }

      // Step 4

      // Send a response with status "200 OK", resource corresponding MIME type and
      // resource stream as body.
      HTTPResponse response = new HTTPResponse(resource);
      response.setStatus(HTTPConstants.HTTP_STATUS_OK); // See HTTPConstants
      response.setMimeType(MIMEUtils.getMIMEType(uri)); // See MIMEUtils
      return response;
  }

Another example for the ``PUT`` method :

.. code-block:: java

  @Override
  public HTTPResponse process(HTTPRequest request, Map<String, String> attributes) {
      // Step 1

      int method = request.getMethod();
      String body = request.parseBody(new StringBodyParser());

      // Step 3

      // Process only PUT requests.
      if (method != 1) {
          return null;
      }

      // Step 4

      System.out.println(body);

      // Send a response with an empty body.
      return HTTPResponse.createResponseFromStatus(HTTPConstants.HTTP_STATUS_OK);
  }

Handle encoding
---------------

Content and transfer encoding
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

The HTTP protocol specifies how to send the request / response payload (the
body) with a specific encoding. To guarantee that the receiver can understand
the encoded stream, HTTP has specified headers for encoding :
``content-encoding``, ``transfer-encoding`` and ``accept-encoding``.
The ``HTTPRequest`` and ``HTTPResponse`` classes uses encoding handlers stored
in the ``HTTPEncodingRegistry`` to, respectively, decode and encode the
payloads with the relevant handler (``IHTTPEncodingHandler`` or
``IHTTPTransferCodingHandler``). For the response, the ``accept-encoding``
header value is used to determine the available encoding with the highest
quality (acceptance value).

To add a encoding handler, use the
``public HTTPServer(TCPServer tcpServer, int jobCount, RequestHandler requestHandler, HTTPEncodingRegistry encodingRegistry)``
constructor with a custom instance of ``HTTPEncodingRegistry`` and add the
handler with ``HTTPEncodingRegistry#registerEncodingHandler`` or
``HTTPEncodingRegistry#registerTransferCodingHandler``.

By default, the registry already contains the "identity" encoding handler
and the "identity" and "chunked" transfer-coding handlers.

Request and response encoding
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

When parsing the request, ``HTTPRequest`` wraps the body with the appropriate
decoder or, if not found, send a "501 Not Implemented" response. The body
parser will receive the wrapped (decoded) stream as input so it doesn't have to
deal with encodings. Same for ``HTTPResponse`` that uses the encoder wrapper to
write the response into the encoded stream sent to the socket. Also, when using
an input stream with unknown length as the data of the response, the transfer
encoding used to send the response is "chunked", otherwise it is "identity".
When using a String as the response data, use the
``HTTPResponse(String, String)`` constructor to specify the encoding of the
string (by default, ``ISO-8859-1`` is used).

URL encoding
~~~~~~~~~~~~

The percent-encoded special characters in the URI and in the query (parameters)
are automatically decoded at parsing.

Understand the Hoka logs
------------------------

Hoka uses a logger that prints the messages to the standard output. The
messages are in the format ``Hoka:[LEVEL]=[id]`` followed by additional
information (a list of space-separated strings) depending on the message.

The ids have the following meanings :

- -1 : Too many connections, logged when a connection is rejected by the
  server because the waiting connection array is full.
- -2 : Multiple start, logged when the ``start()`` method is called while
  the server is running.
- -3 : Empty endpoint, logged when a REST endpoint is created for an empty
  endpoint.
- -4 : Directory traversal, logged when a request target a resource using a
  directory traversal URI.
- -255 : Error unknown, logged when an unexpected exception is thrown.
  Additional information (the stack trace of the exception thrown) is sent
  to the browser if the server debug mode is activated. Activate the debug
  mode with ``server.sendStackTraceOnException(true)``.
- 1 : New connection, logged when a new connection is opened.
- 2 : Server started, logged when the server has finished its startup.
- 3 : Server stopped, logged after the server is stopped.
- 4 : Process connection, logged when a job starts processing a connection.
- 5 : Response sent, logged when a response is sent.
- 6 : Connection lost, logged when the connection is broken by the client.
- 7 : Connection closed, logged when the connection is closed.

Some messages contains information about the connection : the socket hashcode
to identify the connection and the IP source address.

The following is an example of the logs produced by Hoka :

.. code-block::

  Hoka:I=2                                            -> Server started
  Hoka:I=1 165120 /127.0.0.1                          -> New connection
  Hoka:F=4 165120 /127.0.0.1                          -> Process connection
  Hoka:I=1 172944 /127.0.0.1                          -> New connection
  Hoka:F=4 172944 /127.0.0.1                          -> Process connection
  Hoka:F=5 165120 /127.0.0.1 200 OK /                 -> Response sent
  Hoka:F=7 165120 /127.0.0.1                          -> Connection closed
  Hoka:I=1 184136 /127.0.0.1                          -> New connection
  Hoka:F=4 184136 /127.0.0.1                          -> Process connection
  Hoka:F=5 172944 /127.0.0.1 200 OK /png/microej.png  -> Response sent
  Hoka:F=7 172944 /127.0.0.1                          -> Connection closed

Additional features
-------------------

REST services
~~~~~~~~~~~~~

For the server to serve REST endpoints, this library provides a REST request
handler. This handler contains a list of endpoints. To add an endpoint to this
handler, use the ``addEndpoint(RestEndpoint)``. This handler uses the URI
requested to select the most specific endpoint that will, then, process the
request depending on the method used.

To define a REST endpoint, extend the ``RestEndpoint`` class and override at
least one of the following methods :

- ::

    public HTTPResponse get(HTTPRequest request, Map<String, String> attributes)

- ::

    public HTTPResponse post(HTTPRequest request, Map<String, String> attributes)``

- ::

    public HTTPResponse put(HTTPRequest request, Map<String, String> attributes)``

- ::

    public HTTPResponse delete(HTTPRequest request, Map<String, String> attributes)``

Not overrided methods return a "501 Not Implemented" response.

Also, the ``RestEndpoint`` constructor has an URI argument used by the REST
request handler to match the URI of the request. By default, the matching is
strict, but adding a ``/*`` trailer to the ``RestEndpoint`` URI enable the
endpoint to match all the sub-URI. For example, ``/my/endpoint/*`` matches
``/my/endpoint/and/sub/URI`` and ``/my/endpoint`` doesn't match
``/my/endpoint/index.html``.

The library provides 3 implementations of ``RestEndpoint`` :

- ``ResourceRestEndpoint`` : Resource-based endpoint, looks for a specific
  file in the application ressources.
- ``GzipResourceRestEndpoint`` : Extension of ``ResourceRestEndpoint`` to use
  to send compressed files with the "gzip" content-encoding and MIME type given
  by ``MIMEUtils#getMIMEType(String)``.
- ``AliasEndpoint`` : Forwards requests to another endpoint. Useful to use a
  ``RestEndpoint`` for different URIs.

Authentication
~~~~~~~~~~~~~~

The Hoka library provides tools to enable authentication on the HTTP server.
First, the ``ej.hoka.auth`` package contains a session authentication engine
``SessionAuthenticator`` that uses, by default, an in-memory database of all
active sessions.

The ``SessionAuthenticator`` class is parameterized by a session lifetime used
to set an expiration date on session tokens and a database interface used to
query the database. By default, the session lifetime is set to 1 hour and the
database inteface used is an instance of ``InMemorySessionDataAccess`` that
creates maps representing the relations between session IDs, user IDs and
session expiration dates.

However the ``SessionAuthenticator`` doesn't use ``SecureRandom`` by default in
order to allow the use of this library without SSL, users shall instantiate the
``SessionAuthenticator`` with an instance of ``SecureRandom`` to generate
secure session IDs.

Then, this engine is used by the following ready-to-use components in the
``ej.hoka.auth.session`` package :

- ``AuthenticatedRequestHandler`` : a ``RequestHandlerComposite`` that requires
  the user to be authenticated before to delegates the request to its
  sub-handlers. The request is only processed when the
  ``protected boolean match(HTTPRequest request)`` returns ``true``. Default
  behavior is that the request targets a sub-URI of the root URI defined in the
  ``AuthenticatedRequestHandler`` constructor. Overrides the method to change
  the behavior.
- ``RestAuthenticatedRequestHandler`` : Extension of the
  ``AuthenticatedRequestHandler`` used for REST services. Only endpoints with
  sub-URIs of the root URI are accepted by
  ``public void addEndpoint(RestEndpointendpoint)``.
- ``LoginEndpoint`` : an abstract extension of ``RestEndpoint`` to quickly
  setup a login endpoint.
- ``LogoutEndpoint`` : an abstract extension of ``RestEndpoint`` to quickly
  setup a logout endpoint.
