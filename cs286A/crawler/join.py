#!/usr/bin/python
# join.py

# Make sure input files are flat json dictionaries. 
# Files will be joined on every matching column.
# Make sure that columns with the same name have identical row data.
# Output will not duplicate the columns.

import sys
import json

# Usage example: ./join.py input1_filename.json input2_filename.json output_filename.json.
f1 = sys.argv[1]
f2 = sys.argv[2]
fout = sys.argv[3]

# Open 2 input files and convert to json dicts.
with open(f1, "r") as input_file:    
      dict1 = json.load(input_file)
with open(f2, "r") as input_file:    
      dict2 = json.load(input_file)

# Merge the json dicts.
merged_dict = {key: value for (key, value) in (dict1.items() + dict2.items())}

# Write the merged json dict to an output json file.
with open(fout, "w") as output_file:
      json.dump(merged_dict, output_file)
