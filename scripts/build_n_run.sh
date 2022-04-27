#! /usr/bin/bash
rm -r target | echo "No target directory found"
mvn compile
mvn exec:java -Dexec.mainClass=Main -Dexec.args="$1"