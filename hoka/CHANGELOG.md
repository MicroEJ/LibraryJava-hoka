# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## 7.1.1 - 2020-02-18

### Changed

  - Update ej.api.edc library to version 1.3.0.
  - Update ej.api.bon library to version 1.4.0.
  - Update ej.api.net library to version 1.1.1.
  - Update ej.library.eclasspath.base64 library to version 1.1.0.
  - Update ej.library.runtime.basictool library to version 1.3.0.

## 7.1.0 - 2020-02-04

### Changed

  - Update the user manual.
  - Optimize MultipartBodyParser (getBoundaryIndex).
  
### Deprecated

  - SessionAuthenticator constructors that use the default Random implementation.

### Removed

  - Remove SSL dependency used by SessionAuthenticator to generate secure session IDs.

### Fixed

  - Fix HTTPServer.getHtmlExceptionStackTrace() OutOfBounds exception that was thrown when the first 
    element of the stack trace wasn't a part of the java.lang package.

## 7.0.0 - 2019-12-10

### Changed

  - Change the static HTTPSession to the RequestHandler customizable mechanism
  - Move encoding registering to a dedicated class.
  - Change the request's body parsing API.

### Added

  - Add timeout mechanism to cleanup HTTP inactive connections.
  - Merge REST library.
  - Merge authentication library.

## 6.0.0 - 2019-10-31

### Added

  - Add default parameters constructors.

### Changed

  - Replace abstract method HTTPServer#newHTTPSession(HTTPSession) with an abstract factory inner class to HTTPSession that subclasses can implement.
  - Use ServerSocketFactory instead of ServerSocket.

## 5.1.0 - 2019-03-13

### Changed

  - Use MMM.

## 5.0.0 - 2018-11-21

### Added

  - Add multi part body parser.
  - Move previous multipart parsing into StringBodyParser
  - Use Map instead of Hashtable
  - Use StrngBuilder instead of string buffer.
  - Sonar review
  
## 4.1.1 - 2018-12-26

### Added

  - J0153H-8 Hoka jobs should wait until a new connection is added.

## 4.1.0 - 2018-09-04

### Added

  - J0153H-4 Use buffer input stream to optimize speed.
  
## 4.0.0 - 2018-02-16

### Fixed

  - J0153H-2 Use a standard logger.
  
### Added

  - J0153H-3 Add body parser factory to the server.
  
## 3.0.0 - 2017-08-21

### Added

  - Use net library and remove net interfaces.

## 2.1.1 - 2017-06-23

### Added

  - Add build component.
  
## 2.1.0 - 2017-04-06

### Added

  - Add possibility to parse the body.
  
## 2.0.0 - 2016-10-12

### Added

  - Initial  revision.
  
---  
_Copyright 2017-2020 MicroEJ Corp. All rights reserved._  
_This library is provided in source code for use, modification and test, subject to license terms._  
_Any modification of the source code will break MicroEJ Corp. warranties on the whole library._  