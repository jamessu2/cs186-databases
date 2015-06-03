#!/bin/bash

# @author: ianjuch
# Script to convert avro to json

java -jar avro-tools-1.7.7.jar tojson --pretty ${1} > output.json
