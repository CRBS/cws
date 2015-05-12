#!/bin/bash

if [ $# -ne 1 ] ; then
  echo "$0 <app engine application name>"
  echo ""
  echo "Deploys application to GAE setting application"
  echo "name to argument passed into this script"

  echo ""
  exit 1
fi

declare appEngineAppName=$1

mvn clean install -Dappengine.application.name=${appEngineAppName}

if [ $? != 0 ] ; then
  echo "Build failed"
  exit 1
fi

cd cws-ear

mvn appengine:update -Dappengine.application.name=${appEngineAppName}

cd ..

