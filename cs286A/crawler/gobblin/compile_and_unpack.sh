#!/bin/bash

# @author: ianjuch
# Script to run the gradlew build and unpack the tarball

./gradlew build
tar -zxvf gobblin-dist.tar.gz
