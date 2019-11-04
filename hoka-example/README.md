# Overview

These examples of use of the Hoka HTTP Server set up a simple HTTP server to display pages or serve resources using respectively insecure and secure (over TLS) connections.

**Basic**

Each time a URL if asked by a client (for example an Internet browser) a match is done between this URL and resource in the src/resources source folder.

For example:

http://localhost:8080/html/index.html and https://localhost:8443/html/index.html serves the Java ressource "/html/index.html"

Moreover a custom behaviour is added when asking for the root of the server. The root URL is mapped to a default resource configured in SimpleHTTPSession.DEFAULT\_ROOT\_RESOURCE.

# Requirements

  - EDC 1.2 or later
  - NET 1.1 or later

# Dependencies

  - HOKA 6.0.0 or later
  
# Source

N.A.

# Restrictions

None.

  
---  
_Copyright 2017-2019 MicroEJ Corp. All rights reserved._  
_For demonstration purpose only._  
_MicroEJ Corp. PROPRIETARY. Use is subject to license terms._  