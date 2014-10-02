cws
===

CRBS Workflow Service

Is a REST Service to enable execution of Kepler Workflows

Building and running a local instance
=====================================

The below commands will build CRBS Workflow and start a 
local webserver to host the webservice

    mvn clean install
    cd cws-ear
    mvn appengine:devserver

The script **local.sh** runs the above commands



Deploying to Google App Engine
==============================

The below commands will build and deploy CRBS Workflow Service
to Google App Engine.  The version deployed to is whatever is
set in cws-war/pom.xml line <appengine.app.version>##</appengine.app.version>

    mvn clean install
    cd cws-ear
    mvn appengine:update

The script **deploy.sh** runs the above commands



Command line program
====================

This build system creates a commandline executable jar located at
 
    cws-war/target/cws-war-VERSION-jar.with-dependencies.jar

The above jar can be invoked via java -jar <above jar file> for more
information. 


Adding a new Workflow via Command line program
----------------------------------------------

    # Adds example.kar workflow into the local instance of REST service
    java -jar cws-war/target/cws-war-VERSION-jar.with-dependendies.jar --uploadwf cws-war/src/test/resources/example.kar --url http://localhost:8080 --login foo --token bar


Copyright
=========
Copyright   2014   The Regents of the University of California
All Rights Reserved


Permission to copy, modify and distribute any part of this CRBS Workflow Service (cws) for educational, research and non-profit purposes, without fee, and without a written agreement is hereby granted, provided that the above copyright notice, this paragraph and the following three paragraphs appear in all copies.

Those desiring to incorporate this CRBS Workflow Service (cws) into commercial products or use for commercial purposes should contact the Technology Transfer Office, University of California, San Diego, 9500 Gilman Drive, Mail Code 0910, La Jolla, CA 92093-0910, Ph: (858) 534-5815, FAX: (858) 534-7345, E-MAIL:invent@ucsd.edu.

IN NO EVENT SHALL THE UNIVERSITY OF CALIFORNIA BE LIABLE TO ANY PARTY FOR DIRECT, INDIRECT, SPECIAL, INCIDENTAL, OR CONSEQUENTIAL DAMAGES, INCLUDING LOST PROFITS, ARISING OUT OF THE USE OF THIS CRBS Workflow Service (cws), EVEN IF THE UNIVERSITY OF CALIFORNIA HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

THE CRBS Workflow Service (cws) PROVIDED HEREIN IS ON AN "AS IS" BASIS, AND THE UNIVERSITY OF CALIFORNIA HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS, OR MODIFICATIONS.  THE UNIVERSITY OF CALIFORNIA MAKES NO REPRESENTATIONS AND EXTENDS NO WARRANTIES OF ANY KIND, EITHER IMPLIED OR EXPRESS, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE, OR THAT THE USE OF THE CRBS Workflow Service (cws) WILL NOT INFRINGE ANY PATENT, TRADEMARK OR OTHER RIGHTS. 
