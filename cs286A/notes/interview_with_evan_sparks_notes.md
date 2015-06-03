# Interview with Evan Sparks, 3/25

Spark spits out a lot of log files regarding data
- The info is useful, but captured in an unstructured way (i.e. log files)
  o Maybe find a way to put data in a query-like manner to capture relevant data by sift through the irrelevant data
- Log files can fit in memory, but keep in mind that they can be more verbose and/or be linked to much longer-running jobs

Spark is about doing everything in memory (things get evicted if there is overflow)

Metadata: all the information under the UI that is running Spark…? (Evan Sparks is unsure of what is meant by metadata in this sense)

Lineage: keeping track of the operators performed on data that resulted in your answer
- data can be cached along the way of the data flow so the answer does not have to be recomputed completely from square one
- how to get lineage data
  o is there a way…? Might involve going into Spark and modifying the code to accommodate that task; Might be out of scope for this project
  o Alternatively: might be able to walk the rdd (resilient distributed dataset) to get the sequence of operations that were performed on dataset

To Summarize, metadata that we could potentially get:
- Log files (low-hanging fruit; free just by turning on event-logging system)
- Data flow data from lineage (will certainly take more work to collect; solution is non-trivial)

To ask Joe Hellerstein: what do you think this project is trying to accomplish (missing from description): e.g. Faster, more robust, etc? putting data in the same format and scheme from all other types of data from other types of use cases

https://spark.apache.org/docs/latest/configuration.html:
see eventlog as a starting point? Captures information from Spark output –> parse it and put it in our repository

MLlib:
- is a library for large-scale machine learning (an application on top of Spark)
- data doesn't get stored in the log files mentioned in the eventlog email

Latest docs about Spark: spark.apache.org/docs/latest/
Recommended: the Programming Guide


### Other Notes

"Latest spark docs: https://spark.apache.org/docs/latest/index.html (the quick start and programming guide are great places to start with)
The spark programming guide: https://spark.apache.org/docs/latest/mllib-guide.html

The RDD API: http://spark.apache.org/docs/latest/api/scala/index.html#org.apache.spark.rdd.RDD

Lastly - if you want to look at the lineage of an RDD, take a look at the "toDebugString" method on RDD. It recursively walks the ".dependencies" field of the RDD. Both fields are public so generating lineage information without modifying spark is certainly possible. Anyway - not trivial, but this .toDebugString thing will get you a good chunk of the way there." - Evan Sparks