.. Copyright 2019 MicroEJ Corp. All rights reserved.
.. This library is provided in source code for use, modification and test,
   subject to license terms.
.. Any modification of the source code will break MicroEJ Corp. warranties
   on the whole library.

Overview
========

The Hoka HTTP Server can be used to create web interfaces or M2M capabilities
for embedded applications. One Hoka instance can serve multiple architectures
(filesystem, REST, SOAP, …).

Usage
=====

Examples are available in the `hoka-example <..\hoka-example>`__ project.

Instantiating the HTTPServer
----------------------------

``HTTPServer`` is the entry point of this library. His constructor is :

  .. code:: java

       public HTTPServer(int port, int maxSimultaneousConnection, int jobCount,
              RequestHandler requestHandler, ServerSocketFactory serverSocketFactory,
              int keepAliveDuration);

The parameters of the constructor are the following :

-  ``port`` : the port to bind the server on.
-  ``maxSimultaneousConnection`` : the maximum number of waiting
   connections, the underlying ``TCPServer`` will allocate an array of
   connections with a size of ``maxSimultaneousConnection``.
-  ``jobCount`` : the number of ``Thread`` the ``HTTPServer`` will use
   to process incoming requests. If this number equals to 1, only one
   request at a time will be processed.
-  ``requestHandler`` : the handler of requests. To have a hierarchy of
   handlers, use ``RequestHandlerComposite``. For a REST architecture,
   use ``RestRequestHandler``. By default, the server use a
   ``DefaultRequestHandler`` that try to match uri requested with
   resources with ``Class#getResourceAsStream``.
-  ``serverSocketFactory`` : the factory that creates the underling
   socket on which the server operates. With this factory, after a first
   start, the server can be stopped and restarted. By default, the
   system environment default server socket factory is used.
-  ``keepAliveDuration`` : the timeout duration in milliseconds for
   idling persistent connections.

Customizing the HTTP Server
---------------------------

``ej.hoka.tcp``
~~~~~~~~~~~~~~~

The `ej.hoka.tcp <src/main/java/ej/hoka/tcp>`__ package defines
``TCPServer`` that is responsible for opening and closing the main
server socket as well as storing the incoming connections in an array,
waiting for their process.

Instead of some of the arguments of the previously described constructor
of ``HTTPServer``, it is possible to directly pass a ``TCPServer``.

``ej.hoka.http``
~~~~~~~~~~~~~~~~

The `ej.hoka.http <src/main/java/ej/hoka/http>`__ package defines
``HTTPServer`` that is responsible for maintaining jobs that process the
connections stored by ``TCPServer``. The job parses the request into
``HTTPRequest`` and write the built ``HTTPResponse`` into the connection
socket.

``ej.hoka.http.requesthandler``
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

The
`ej.hoka.http.requesthandler <src/main/java/ej/hoka/http/requesthandler>`__
package defines ``RequestHandler``, the interface for processing the
``HTTPRequest`` into ``HTTPResponse``.

Since this interface is designed for an hierarchical use through
``RequestHandlerComposite``, a ``Map`` of attributes is used to
communicate between the handlers.

Implementation must be thread safe since the handler can be used by
several jobs at the same time.

``ej.hoka.rest``
~~~~~~~~~~~~~~~~

The `ej.hoka.rest <src/main/java/ej/hoka/rest>`__ package defines
``RestRequestHandler``, an implementation of ``RequestHandler`` for the
REST architecture, and ``RestEndpoint``, the default implementation of
an endpoint. To enable REST on the HTTP server, put a
``RestRequestHandler`` in the hierarchy of handlers (if other
architectures are available, otherwise it is possible to use the handler
as the root) and add instances of ``RestEndpoint`` to it.

  .. code:: java

       // Initialize the RestRequestHandler and add the endpoints
       RestRequestHandler restHandler = new RestRequestHandler();
       restHandler.addEndpoint(new RestEndpoint("/") { ... });
       restHandler.addEndpoint(new RestEndpoint("/api/resources/") { ... });

       // Add the rest handler to the hierarchy of handlers
       RequestHandlerComposite root = getRootApplicationHandler();
       root.addRequestHandler(restHandler);

``ej.hoka.rest.endpoint``
~~~~~~~~~~~~~~~~~~~~~~~~~

The `ej.hoka.rest.endpoint <src/main/java/ej/hoka/rest/endpoint>`__
package defines useful implementation of ``RestEndpoint`` for common use
cases :

-  ``ResourceRestEndpoint`` : an endpoint that serves a file from the
   application resources (See ``Class#getResourceAsStream``).
-  ``GzipResourceEndpoint`` : an endpoint that serves a compressed file
   from the application resources (See ``Class#getResourceAsStream``).
-  ``AliasEndpoint`` : an endpoint that mimify another endpoint.

``ej.hoka.http.body``
~~~~~~~~~~~~~~~~~~~~~

The `ej.hoka.http.body <src/main/java/ej/hoka/http/body>`__ package
defines ``BodyParser``, the interface for processing the body of
``HTTPRequest``. This is used during the process of the request (after
the header is fully parsed).

The package contains some implementations to parse :

-  the whole body as a string : ``StringBodyParser``
-  multipart bodies :

   -  each body parsed as a string : ``MultipartStringsParser``
   -  each body parsed as a HTTPPart : ``MultiPartBodyParser``

-  parameters like for form encoded data : ``ParameterParser``

To parse a body, use ``HTTPRequest#parseBody(BodyParser)``. For example
:

  .. code:: java

       StringBodyParser parser = new StringBodyParser();
       HTTPRequest request = parseRequestHeader();
       String body = request.parseBody(parser);

Requirements
============

This library requires the following Foundation Libraries:

::

   @FOUNDATION_LIBRARIES_LIST@

Dependencies
============

*All dependencies are retrieved transitively by Ivy resolver*.

Source
======

N.A.

Restrictions
============

None.
