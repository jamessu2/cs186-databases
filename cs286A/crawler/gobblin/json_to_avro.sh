#!/bin/bash

# @author: Kyle Dillon
# Script to convert json to avro
# 1 = input json file

java -jar avro-tools-1.7.7.jar fromjson --schema-file merged_schema.avsc ${1} > output.avro
