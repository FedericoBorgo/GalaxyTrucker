#!/usr/bin/bash

echo "Cleaning"
mvn clean > /dev/null 2>/dev/null
rm deliverables/GalaxyTrucker.jar > /dev/null 2> /dev/null
rm -r deliverables/apidocs > /dev/null 2> /dev/null
rm -r deliverables/javadoc > /dev/null 2> /dev/null

echo "Generating Jar"
out=$(mvn package 2>/dev/null)

if [ $? -ne 0 ]; then
  echo $out
  exit 1
fi

echo "Generating JavaDoc"
out=$(mvn javadoc:aggregate 2>/dev/null)

if [ $? -ne 0 ]; then
  echo $out
  exit 1
fi

echo "Moving to deliverables"

cp target/GalaxyTrucker.jar deliverables
cp -r target/site/apidocs deliverables
mv deliverables/apidocs deliverables/javadoc

echo "Done"