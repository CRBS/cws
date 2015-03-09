mvn clean install

if [ $? != 0 ] ; then
  echo "Build failed"
  exit 1
fi

cd cws-ear

mvn appengine:update

cd ..

