## JMH Email, 2/10

Hi all:
Looks like you split up the tasks evenly and we can get coverage if everybody works on their first choice.  Here’s the breakdown:

  - Metadata Repo: Feiyang, Enrico and Jimmy
  - Crawler: Ian, Kyle and Eric
  - Data Mover: Haoyu and Jialiang

Next steps here will be for you to meet with your teams and come up with a plan to get an initial requirements document and plan in the next 10-14 days.  I’d like for this to live in our Github repo so I can subscribe to changes and see how you’re progressing.

Some thoughts per project below.  We will also meet next Tuesday.  As we get more context I can connect you guys to folks on campus, in the open source community, and in industry as needed.  But let’s get some focused questions first.

In general, this is graduate work and you guys need to define the details: what do you think is interesting, and what do you plan to deliver?  I can generate lots of ideas, but you guys should scope out what’s worthwhile and what’s feasible.  Imagine you’re being graded on two axes: (1) how interesting/ambitious/useful is the project, (2) how close did you come to hitting the milestones you set for yourselves.  You have control over the tradeoff between (1) and (2), but I’d encourage you to emphasize (2) — make sure you set expectations realistically, and deliver on what you promise.

Joe

Metadata Repo:

  1. It would be great for you guys to do some requirements gathering -- learn about the metadata that is generated from a few different use cases.  To get started, it’d be useful to look at some specific software artifacts.  I was thinking you could (a) talk to Evan Sparks in the AMPlab to find out about what metadata is generated when MLlib jobs are run (over Spark), (b) figure out what information is stored in an iPython Notebook (probably well documented, but if you need help I can route you to a local expert), and (c) talk with folks at Trifacta about what metadata is currently kept for data wrangling in their system.  Please cc me if/when you reach out to Evan, and let me know when you’re ready to talk to somebody at Trifacta and I’ll connect you.

  1. For a more end-to-end use case, you could interview Frank Nothaft in the AMPLab … he can tell you about some of the computational genomics use cases he’s working on.  Again, please cc me if you reach out to him.  I would do this one after you do the others, as it’s more open-ended.

  1. Have a look at the metadata repo in Gobblin (linked from the Readme.MD in our github repo) and write up some short pros/cons on its functionality relative to the use cases you’re learning about.  

  1. Think about what kind of database backend will work for us at scale here —  relational, JSON, or key-value?  If we go relational, our open-source options at scale (i.e. distributed) are a bit limited.  There’s a parallel version of Postgres coming out into open source in a few months (GreenplumDB) but it may not be the best choice for broad adoption.  Some research into best practices in the open source community here would be good.

Crawler

  1. Here I believe Gobblin is a decent starting point.  I’d encourage you to install it and start experimenting with it.  You can work with desktop data for a while, but we should get on HDFS soon.  Simplest approach for that may be to grab a pre-packaged Hadoop VM from Cloudera or Horton, and install Gobblin in there to play with — not that hard to do so you could just start there if you want to get going with the whole stack.

  1. The first design question will be how to hook up Gobblin to services that can do metadata analysis on files.  E.g. suppose you write a little service that can analyze a CSV file and spit back column metadata and statistics (something like http://hampelm.github.io/csv-overview/).  How would you feed it from the crawler, and get what it spits back into the metadata repo?

  1. Frank Nothaft at AMPLab can probably point you to a live HDFS filesystem and Hadoop installation that you could play with, once you’re ready to fire it up at scale.

Data Mover

  1. On this front, the open source is more mature and there’s good docs and blogs, etc.  The leading open source projects of note are probably Kafka, Sqoop and Flume.  First step here is to figure out which of those is best suited to the tool we’re trying to build.

  1. Would be good for you to talk to the crawler guys early on about their item (b) — what is the API you’ll provide to “catch” the metadata that is spit out during the crawl?  Make sure that it’s able to work at high volume as needed (e.g. with asynchrony, disk-based buffering, etc.)
