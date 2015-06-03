# cs286A

### UPDATE: DOCUMENTATION
The primary work done for this project is compartmentalized into three groups (see section below).
Documentation for each group is located in a .../doc folder in the respective folder of each group:
- cs286a/crawler/doc/
- cs286a/dataMover/doc/
- cs286a/repo/doc/

### Thoughts from March 2015

General Repo for CS286A projects

In the large, the class project is intended to prototype infrastructure for managing metadata and lineage in the Apache Big Data context.  The project should have three phases:
  1. requirements gathering (2 weeks)
  2. functional specification and design (2 weeks) 
  3. prototype implementation (4 weeks)
These phases will likely overlap and have feedback loops.  That's OK.

We envision three basic components:
  1. A *metadata repository* that (a) has a schema to capture relevant information from a set of prototypical tasks and tools, (b) is extensible to new tasks and tools with varying degrees of opacity, and (c) can scale up to large volumes of metadata and high access rates.
  2. A *crawler* that can walk large repositories of information, and call out to an extensible set of external data "recognizers" or "profilers" that can assess the contents of individual data files or sets.  Candidate datastores include HDFS, POSIX filesystems, and relational databases (via standards like JDBC), and perhaps some special file types like iPython notebooks.  The crawler should interface with a standard scheduling infrastructure at two levels
    1. macro: fire off crawls on a schedule (nightly, weekly, etc)
    2. micro: execute the crawl through the scheduler: i.e. visit files and feed them up for REST calls at a load-sensitive pace.
  3. A *metadata mover* facility that provides (a) an API for inserting metadata into the repository, (b) a facility for reliable bulk movement of large volumes data into the repository, and (c) an interface to the same scheduling infrastructure as the crawler for executing bulk metadata movement

The goal here is not to write everything from scratch, but rather to use and augment well-supported open source components.  Potentially useful components include:
  - [Gobblin](https://github.com/linkedin/gobblin). This is an excellent starting point for the crawler and metadata mover projects.
  - A variety of open-source databases could be used for the metadata repository.  We will have to decide very early on whether we want to use a relational database or a key-value store.  A concern with relational databases is that there isn't currently a well-used scale-out (parallel/distributed) relational database in the typical Apache Big Data environment
  - [Kafka](https://github.com/apache/kafka) is a standard open source tool for reliable bulk data movement that could be useful and will likely work well with Gobblin as both are from LinkedIn.

We will endeavor to support some real-world use cases from scientists on campus, as well as partners in industry.  On campus, we have access to experts on two important toolchains:
  - The [BDAS stack](https://amplab.cs.berkeley.edu/software/) including [Spark](http://spark.apache.org) and [MLlib](http://www.mlbase.org).
  - [Project Jupyter](http://www.mlbase.org), and especially [iPython](http://ipython.org).
