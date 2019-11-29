.. Copyright 2017-2019 MicroEJ Corp. All rights reserved.
.. For demonstration purpose only.
.. MicroEJ Corp. PROPRIETARY. Use is subject to license terms.

Overview
========

This project contains examples using the Hoka HTTP Server.

SimpleServer
------------

The example
`SimpleServer <src/java/com/microej/example/hoka/SimpleServer.java>`__
shows a basic instantiation of ``HTTPServer`` with a custom
``RequestHandler``.

Like with the ``DefaultRequestHandler``, each time a URL is requested by
a client, a match is done between this URL and resource in the
src/resources source folder.

For example:

http://localhost:8080/html/index.html serves the Java resource
“/html/index.html”.

Moreover, a custom behavior is added when requesting the root of the
server : the root URL is mapped to a default resource configured in
``SimpleRequestHandler.DEFAULT_ROOT_RESOURCE``.

HTTPSServer
-----------

The example
`HTTPSServer <src/java/com/microej/example/hoka/https/HTTPSServer.java>`__
is similar to the **SimpleServer** example but with HTTPS enabled.

RestServer
----------

The example
`RestServer <src/java/com/microej/example/hoka/rest/RestServer.java>`__
use a REST handler with a “Hello” endpoint. The example also has a dump
handler that dumps the request in the output stream of the application
in case the request is out of the REST scope.

AuthenticationExampleServer
---------------------------

The example `AuthenticationExampleServer <src/java/com/microej/example/
hoka/rest/auth/AuthenticationExampleServer.java>`__
use a public REST handler with a login endpoint, a logout endpoint and a
*private* REST handler with a “Hello” endpoint that says hello to the
authenticated user. This example uses the default session-based
authentication implementations in package
`ej.hoka.auth.session <../hoka/src/main/java/ej/hoka/auth/session/>`__
in the Hoka library.

Requirements
============

-  EDC 1.2 or later
-  NET 1.1 or later

Dependencies
============

-  HOKA 7.0.0

Source
======

N.A.

Restrictions
============

None.
