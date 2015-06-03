#!/usr/bin/python
# merge_dir.py

# Call on directory of json objects.
# Files will be joined on common key, val pairs.
# Joined files will be output to the specified output dir as tar files

import sys
import json
import os
import ntpath
import subprocess
import glob
import config

def match(json, avro):
  json_name = ntpath.basename(json)
  avro_name = ntpath.basename(avro)
  json_name = os.path.splitext(json_name)[0]
  avro_name = os.path.splitext(avro_name)[0]
  json_id = json_name.split("_")[-1]
  avro_id = avro_name.split("_")[-1]
  ret = False
  if(json_id == avro_id): ret = True
  return ret

# Usage example: ./merge_dir.py dir/to/merge
dr = sys.argv[1]
dicts = {}
avroDir = ""

# Make sure all files are here ready to join (number of CSVs + one directory of avros)
numCSVs = 0
numAvros = 0
with open ("gobblin/gobblin-dist/test_temp/numCsvFiles.txt", "r") as f:
      numCSVs = int(f.readline())
with open ("gobblin/gobblin-dist/test_temp/numFiles.txt", "r") as f:
      numAvros = int(f.readline())

# Parse out the files, bail if not ready to be merged
files = glob.glob(dr + "/*")
if(len(files) != numCSVs + 1): sys.exit()

avroDir = [f for f in files if os.path.isdir(f)][0]
avros = glob.glob(avroDir + "/*")
jsons = [f for f in files if ".json" in f]

if(len(jsons) != numCSVs): sys.exit()
if(len(avros) != numAvros): sys.exit()


# Loop through jsons and avros looking for matches on identifiers
for jsonfile in jsons:
  for avro in avros:
    if(match(jsonfile, avro)):
      # If a match is found, join the files and overwrite the original avro
      with open(jsonfile, "r") as json_file:
        json_dict = json.load(json_file)
      os.chdir("gobblin")
      subprocess.call(['./avro_to_json.sh', avro.replace("gobblin/", "")])
      os.chdir("..")
      with open("gobblin/output.json", "r") as json_file:
        avro_dict = json.load(json_file)
      merged_dict = {key: value for (key, value) in (json_dict.items() + avro_dict.items())}
      # Write the merged json dict to an output json file.
      with open("gobblin/output.json", "w") as output_file:
        json.dump(merged_dict, output_file)
      os.chdir("gobblin")
      subprocess.call(['./json_to_avro.sh', "output.json"])
      os.chdir("..")
      subprocess.call(['mv', '-f', "gobblin/output.avro", avro])
      subprocess.call(['rm', '-f', "gobblin/output.json"])


# Move script and cleanup
subprocess.call(['./compress_and_move.sh', avroDir, config.data_mover_location])
subprocess.call(['rm', '-rf', 'gobblin/test_workdir/externals'])
subprocess.call(['mkdir', 'gobblin/test_workdir/externals'])
subprocess.call(['rm', '-rf', 'gobblin/test_workdir/job-staging'])
subprocess.call(['mkdir', 'gobblin/test_workdir/job-staging'])

