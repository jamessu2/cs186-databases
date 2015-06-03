#!/bin/bash

# @file:inotifywait.sh
# @description: monitors specified directory (recursively)
#   for new files
# @usage:
#   ./inotifywait.sh /path/to/file scp_target_location

inotifywait -m "kafka/incoming" --format '%w%f' -e create |
  while read file; do
    if [[ $file == *".tar.gz" ]]
    then
        ./action.sh $file
    fi
  done
