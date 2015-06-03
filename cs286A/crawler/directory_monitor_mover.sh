#!/bin/bash

# @file: directory_monitor_mover.sh
# @description: provides arguments to react.py. this file is for the use case on the mover side, which will react to files that are transferred over by scp
# @usage:
#   action2.sh should contain the action to be performed when a new file is detected
#
#   ./directory_monitor_mover.sh <path_to_directory>
#
#   ${1} = directory to monitor (recursive)


./react.py ${1} -p "*.tar.gz" './action2.sh $f' #for mover group: edit action2.sh to invoke your executable or script
