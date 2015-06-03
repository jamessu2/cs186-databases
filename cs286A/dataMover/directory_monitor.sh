#!/bin/bash

# @file: react_wrapper.sh
# @description: provides arguments to react.py
# @usage:
#   ${1} = directory to monitor (recursive)
#   ${2} = string which holds the destination for scp
#           i.e. ian@localhost


echo "NOMNOM ${1} ${2}" > testing.txt
./react.py ${1} -p "*.avro" './action.sh ${1} ${2} $f'
