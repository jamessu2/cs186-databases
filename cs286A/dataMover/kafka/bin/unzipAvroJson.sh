#!/bin/bash

echo $1
tar -xzf $1
dirName=`echo $1 | cut -d \. -f 1`
cd $dirName
for f in *.avro;
do
  currFileName=`echo $f | cut -d \. -f 1`
  java -jar ../bin/avro-tools-1.7.7.jar tojson $currFileName.avro > $currFileName.json  
  ../bin/kafka-file-producer.sh --broker-list localhost:9092 --topic test --input-file $currFileName.json
  #rm $f
  #echo $currFileName.json
  
done
