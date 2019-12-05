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
shows a basic instantiation of ``HTTPServer``.

It uses the ``ResourceRequestHandler``. Each time a URL is requested by
a client, a match is done between this URL and resource in the
src/resources/hoka folder.

For example:

http://localhost:8080/index.html serves the Java resource “/hoka/index.html”.

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

Usage
^^^^^

#.  Run the example.
#.  Go to `/ <http://localhost:8080/>`__, you should see the decompressed html content of `index.html.gz <src/resources/hoka/index.html.gz>`__.
#.  Go to `/hello/world <http://localhost:8080/hello/world>`__, you should receive a text starting with "REPLY /hello/world".
#.  Go to `/not/a/valid/endpoint <http://localhost:8080/not/a/valid/endpoint>`__, you should receive a textual dump of the request you sent.

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

Usage
^^^^^

#. Run the example.
#. Go to `/api/private <http://localhost:8080/api/private>`__, you should receive a 401 Not authorized response.
#. Go to `/api/login <http://localhost:8080/api/login>`__ and completes the form.
#. Go back to `/api/private <http://localhost:8080/api/private>`__, this time you should be able to pass the authentication (check cookies).
#. Go to `/api/logout <http://localhost:8080/api/logout>`__.
#. Go back to `/api/private <http://localhost:8080/api/private>`__, you are not authenticated again.

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
