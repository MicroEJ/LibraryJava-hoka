# Overview
This repository contains the Hoka HTTP server and dependencies as well as examples of use to set up a HTTP server and a HTTPS server.

### Structure
Structure of the repository :

- LibraryJava-hoka
   - hoka
   - hoka-example

Each folder is an Eclipse/MicroEJ project. Each project has its own README and LICENSE requirements.

## Setup
Import the projects in MicroEJ.

### Requirements
- JRE 7 x86
- a MicroEJ platform with net 1.1 or later.
- MicroEJ 4.1 or later

## Usage
Import the projects. The example project should be run with a MicroEJ launch by:

- Right-clicking the SimpleServer or SimpleHTTPSServer main class
- Selecting Run As > Run Configurations...
- Double-clicking on MicroEJ Application
- Adding the resources (/html/index.html, /png/microej.png and /com/microej/example/hoka/* for the HTTPS example) on the Main tab
- Selecting a platform with net in the Execution tab
- Running!

## Changes
Nov 2019: https example
Feb 2015: initial release
Share Hoka and its simple example.

---  
_Copyright 2015-2019 MicroEJ Corp. All rights reserved._  
_This library is provided in source code for use, modification and test, subject to license terms._  
_Any modification of the source code will break MicroEJ Corp. warranties on the whole library._  