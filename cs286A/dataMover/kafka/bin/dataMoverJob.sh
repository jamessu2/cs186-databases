#!/bin/bash
incomingDirName="kafka/incoming"
#echo $1
sleep 1s
while true; do
    tar -xzf $1 -C $incomingDirName 2>/dev/null
    if [ "$?" -eq 0 ]
    then break
    else sleep 2s
    fi
done
dirName=`echo $1 | cut -d \. -f 1`

echo curr dir is $dirName
cd $incomingDirName
for f in *.avro;
do
  currFileName=part.`echo $f | cut -d \. -f 2`
 
  java -jar ../bin/avro-tools-1.7.7.jar tojson $f > $currFileName.json  
  ../bin/kafka-file-producer.sh --broker-list localhost:9092 --topic test --input-file $currFileName.json
 
  rm $f 
  rm $currFileName.json
  
done
