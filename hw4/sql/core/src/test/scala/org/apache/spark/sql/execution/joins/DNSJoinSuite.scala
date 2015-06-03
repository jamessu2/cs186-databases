package org.apache.spark.sql.execution.joins

import java.util.{ArrayList => JavaArrayList}

import org.apache.spark.rdd.RDD
import org.apache.spark.sql.SQLContext
import org.apache.spark.sql.catalyst.ScalaReflection
import org.apache.spark.sql.catalyst.expressions.Attribute
import org.apache.spark.sql.execution.joins.dns.GeneralDNSJoin
import org.apache.spark.sql.execution.{PhysicalRDD, SparkPlan}
import org.apache.spark.sql.test.TestSQLContext._
import org.scalatest.FunSuite

import scala.util.Random

case class IP (ip: String)

class DNSJoinSuite extends FunSuite {
  val random: Random = new Random

  // initialize Spark magic stuff that we don't need to care about
  val sqlContext = new SQLContext(sparkContext)
  val IPAttributes: Seq[Attribute] = ScalaReflection.attributesFor[IP]

  var createdIPs: JavaArrayList[IP] = new JavaArrayList[IP]()

  import sqlContext.createSchemaRDD

  // initialization for 1st test
  // initialize a SparkPlan that is a sequential scan over a small amount of data
  val smallRDD1: RDD[IP] = sparkContext.parallelize((1 to 100).map(i => {
    val ip: IP = IP((random.nextInt(220) + 1) + "." + random.nextInt(256) + "." + random.nextInt(256) + "." + random.nextInt(256))
    createdIPs.add(ip)
    ip
  }), 1)
  val smallScan1: SparkPlan = PhysicalRDD(IPAttributes, smallRDD1)

  // initialization for 2nd test
  var identicalCreatedIPs: JavaArrayList[IP] = new JavaArrayList[IP]()
  val identicalIP : String = "8.8.8.8"

  val identicalRDD1: RDD[IP] = sparkContext.parallelize((1 to 100).map(i => {
    val ip: IP = IP( identicalIP )
    identicalCreatedIPs.add(ip)
    ip
  }), 1)
  val identicalScan1: SparkPlan = PhysicalRDD(IPAttributes, identicalRDD1)

  // initialization for 3rd test
  var largecreatedIPs: JavaArrayList[IP] = new JavaArrayList[IP]()
  val largeRDD1: RDD[IP] = sparkContext.parallelize((1 to 500).map(i => {
    val ip: IP = IP((random.nextInt(220) + 1) + "." + random.nextInt(256) + "." + random.nextInt(256) + "." + random.nextInt(256))
    largecreatedIPs.add(ip)
    ip
  }), 1)
  val largeScan1: SparkPlan = PhysicalRDD(IPAttributes, largeRDD1)

  // initialization for 4th test
  var largeIdenticalCreatedIPs: JavaArrayList[IP] = new JavaArrayList[IP]()
  val largeIdenticalIP : String = "8.8.8.8"

  val largeIdenticalRDD1: RDD[IP] = sparkContext.parallelize((1 to 500).map(i => {
    val ip: IP = IP( largeIdenticalIP )
    largeIdenticalCreatedIPs.add(ip)
    ip
  }), 1)
  val largeIdenticalScan1: SparkPlan = PhysicalRDD(IPAttributes, largeIdenticalRDD1)

  test ("simple dns join") {
    val outputRDD = GeneralDNSJoin(IPAttributes, IPAttributes, smallScan1, smallScan1).execute()

    val result = outputRDD.collect
    assert(result.length == 100)

    result.foreach(x => {
      val ip = IP(x.getString(0))
      assert(createdIPs contains ip)
      createdIPs remove ip
    })
  }

  test ("dns join on 100 identical IPs") {
    val outputRDD = GeneralDNSJoin(IPAttributes, IPAttributes, identicalScan1, identicalScan1).execute()

    val result = outputRDD.collect
    assert(result.length == 100)

    result.foreach(x => {
      val ip = IP(x.getString(0))
      assert(identicalCreatedIPs contains ip)
      identicalCreatedIPs remove ip
    })
  }

  test ("large simple dns join") {
    val outputRDD = GeneralDNSJoin(IPAttributes, IPAttributes, largeScan1, largeScan1).execute()

    val result = outputRDD.collect
    assert(result.length == 500)

    result.foreach(x => {
      val ip = IP(x.getString(0))
      assert(largecreatedIPs contains ip)
      largecreatedIPs remove ip
    })
  }

  test ("dns join on 500 identical IPs") {
    val outputRDD = GeneralDNSJoin(IPAttributes, IPAttributes, largeIdenticalScan1, largeIdenticalScan1).execute()

    val result = outputRDD.collect
    assert(result.length == 500)

    result.foreach(x => {
      val ip = IP(x.getString(0))
      assert(largeIdenticalCreatedIPs contains ip)
      largeIdenticalCreatedIPs remove ip
    })
  }
}