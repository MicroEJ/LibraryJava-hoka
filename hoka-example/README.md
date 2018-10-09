# Overview
This example of use of the Hoka HTTP Server sets up a simple HTTP server to display pages or serve resources.

**Basic**

Each time a URL if asked by a client (for example an Internet browser) a match is done between this URL and resource in the src/resources source folder.

For example:

http://localhost/html/index.html serves the Java ressource "/html/index.html"

Moreover a custom behaviour is added when asking for the root of the server. The root URL is mapped to a default resource configured in SimpleHTTPSession.DEFAULT\_ROOT\_RESOURCE.

# Requirements
  - EDC 1.2 or later
  - NET 1.2 or later

# Dependencies
  - HOKA 3.0.0 or later
  
# Source
N.A.

# Restrictions
None.

<!--
	Markdown
	Copyright 2017-2018 IS2T. All rights reserved.
	For demonstration purpose only.
	IS2T PROPRIETARY. Use is subject to license terms.
-->