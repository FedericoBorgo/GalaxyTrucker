#!/usr/bin/bash

echo "Cleaning"
mvn clean > /dev/null 2>/dev/null

echo "Generating Jars"
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
rm deliveries/GalaxyTrucker.jar > /dev/null 2> /dev/null
rm -r deliveries/apidocs > /dev/null 2> /dev/null
cp target/GalaxyTrucker.jar deliveries
cp -r target/site/apidocs deliveries