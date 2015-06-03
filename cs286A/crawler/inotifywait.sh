#!/bin/bash

# @file:inotifywait.sh
# @description: monitors specified directory (recursively)
#   for new files
# @usage:
#   ./inotifywait.sh /path/to/file scp_target_location
#   ${1} = path to file
#   ${2} = scp target location (i.e. ian@localhost)

inotifywait -r -m "${1}" --format '%w%f' -e modify,moved_to,create |
  while read file; do
    ./action_csv.sh ${1} ${2} $file
  done
