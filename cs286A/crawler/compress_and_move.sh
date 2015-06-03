#!/bin/bash

#--------------------------------------------------------------------
# @file: compress_and_move.sh
# @description: compresses and moves tarball to mover machine
# @arguments:
#   ${1} = directory to be compressed
#   ${2} = data mover location
#--------------------------------------------------------------------

# taring is now being done in merge
output_dir=$(echo $1 | sed 's/\(.*\/\)\(.*_append$\)/\2/')

# Delete hiddent crc files
rm -rf ${1}/.* 2> /dev/null
echo "creating tar archive for ${output_dir}"
tar -czf "gobblin/output-tarballs/${output_dir}.tar.gz" -C ${1} .
echo "transferring to ${2}"
echo "/gobblin/output-tarballs/${output_dir}.tar.gz"
scp -i ec2.pem "gobblin/output-tarballs/${output_dir}.tar.gz" ${2}
