#!/usr/bin/bash

echo "Cleaning"
mvn clean > /dev/null 2>/dev/null
rm deliveries/GalaxyTrucker.jar > /dev/null 2> /dev/null
rm -r deliveries/apidocs > /dev/null 2> /dev/null
rm -r deliveries/javadoc > /dev/null 2> /dev/null

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

echo "Moving to deliveries"

cp target/GalaxyTrucker.jar deliveries
cp -r target/site/apidocs deliveries
mv deliveries/apidocs deliveries/javadoc

echo "Done"