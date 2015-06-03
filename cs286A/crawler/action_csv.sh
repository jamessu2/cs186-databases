#!/bin/bash

#--------------------------------------------------------------------
# @file: action.sh
# @description: script that gets called when the monitor finds a
#   new file in a directory
# @arguments:
#   ${1} = directory of being monitored
#   ${2} = string which holds the destination for scp
#           i.e. ian@localhost
#   ${3} = file that just got found
#--------------------------------------------------------------------


dir_path=${3}
output_dir=$(echo $3 | sed 's/\(.*\/\)\(.*_append$\)/\2/')

# Process changes to Gobblin output
if [[ $dir_path == *"job-output/gobblin/example/simplejson/ExampleTable"* && $dir_path != *".avro" && $dir_path != *".avro.crc" ]]
then
  expected_file=gobblin/gobblin-dist/test_temp/numFiles.txt
  expected_num_files=`cat $expected_file` > /dev/null 2>&1
  job_staging=gobblin/test_workdir/job-staging
  num_files=$(ls -l ${dir_path}/*.avro | wc -l)

  if [ "$num_files" -eq "$expected_num_files" ]
  then
    cp -rf $dir_path $job_staging
    ./merge_dir.py $job_staging
  fi
# Process changes to external output
elif [[ $dir_path == *"test_workdir/externals"* ]]
then
  expected_file=gobblin/gobblin-dist/test_temp/numCsvFiles.txt
  expected_num_files=`cat $expected_file` > /dev/null 2>&1
  job_staging=gobblin/test_workdir/job-staging
  num_files=$(ls -l gobblin/test_workdir/externals/*.json 2> /dev/null | wc -l)

  if [ "$num_files" -eq "$expected_num_files" ]
  then
    cp gobblin/test_workdir/externals/* $job_staging
    ./merge_dir.py $job_staging
  fi
fi
# else ignore
