package org.apache.spark.sql.execution.joins

import org.apache.spark.rdd.RDD
import org.apache.spark.sql.SQLContext
import org.apache.spark.sql.catalyst.ScalaReflection
import org.apache.spark.sql.catalyst.expressions.{JoinedRow, Row, Attribute}
import org.apache.spark.sql.execution.joins.dns.GeneralSymmetricHashJoin
import org.apache.spark.sql.execution.{Record, ComplicatedRecord, PhysicalRDD, SparkPlan}
import org.apache.spark.sql.test.TestSQLContext._
import org.scalatest.FunSuite

import scala.collection.immutable.HashSet
import scala.collection.mutable.MutableList
import java.util.{ArrayList => JavaArrayList}


class SymmetricHashJoinSuite extends FunSuite {
  // initialize Spark magic stuff that we don't need to care about
  val sqlContext = new SQLContext(sparkContext)
  val recordAttributes: Seq[Attribute] = ScalaReflection.attributesFor[Record]
  val complicatedAttributes: Seq[Attribute] = ScalaReflection.attributesFor[ComplicatedRecord]

  import sqlContext.createSchemaRDD

  // initialize a SparkPlan that is a sequential scan over a small amount of data
  val smallRDD1: RDD[Record] = sparkContext.parallelize((1 to 100).map(i => Record(i)), 1)
  val smallScan1: SparkPlan = PhysicalRDD(recordAttributes, smallRDD1)
  val smallRDD2: RDD[Record] = sparkContext.parallelize((51 to 150).map(i => Record(i)), 1)
  val smallScan2: SparkPlan = PhysicalRDD(recordAttributes, smallRDD2)

  // the same as above but complicated
  val complicatedRDD1: RDD[ComplicatedRecord] = sparkContext.parallelize((1 to 100).map(i => ComplicatedRecord(i, i.toString, i*2)), 1)
  val complicatedScan1: SparkPlan = PhysicalRDD(complicatedAttributes, complicatedRDD1)
  val complicatedRDD2: RDD[ComplicatedRecord] = sparkContext.parallelize((51 to 150).map(i => ComplicatedRecord(i, i.toString, i*2)), 1)
  val complicatedScan2: SparkPlan = PhysicalRDD(complicatedAttributes, complicatedRDD2)

  // initialize a SparkPlan with duplicated entries in the iterator 
  // i.e. 5x Record(100) and 3x Record(100)
  val duplicateRDD1: RDD[Record] = sparkContext.parallelize((1 to 5).map(i => Record(100)), 1)
  val duplicateScan1: SparkPlan = PhysicalRDD(recordAttributes, duplicateRDD1)
  val duplicateRDD2: RDD[Record] = sparkContext.parallelize((1 to 3).map(i => Record(100)), 1)
  val duplicateScan2: SparkPlan = PhysicalRDD(recordAttributes, duplicateRDD2)

  // initialize a SparkPlan in which GeneralSymmetricHashJoin return late matches
  val lateRDD1: RDD[Record] = sparkContext.parallelize((501 to 600).map(i => Record(i)), 1)
  val lateScan1: SparkPlan = PhysicalRDD(recordAttributes, lateRDD1)
  val lateRDD2: RDD[Record] = sparkContext.parallelize((1 to 550).map(i => Record(i)), 1)
  val lateScan2: SparkPlan = PhysicalRDD(recordAttributes, lateRDD2)

  // initialize a SparkPlan in which GeneralSymmetricHashJoin does not return any match
  val emptyRDD1: RDD[Record] = sparkContext.parallelize((1 to 100).map(i => Record(i)), 1)
  val emptyScan1: SparkPlan = PhysicalRDD(recordAttributes, emptyRDD1)
  val emptyRDD2: RDD[Record] = sparkContext.parallelize((101 to 200).map(i => Record(i)), 1)
  val emptyScan2: SparkPlan = PhysicalRDD(recordAttributes, emptyRDD2)

  // initialize a SparkPlan with single element iterator
  val singleRDD1: RDD[Record] = sparkContext.parallelize((1 to 1).map(i => Record(i)), 1)
  val singleScan1: SparkPlan = PhysicalRDD(recordAttributes, singleRDD1)
  val singleRDD2: RDD[Record] = sparkContext.parallelize((1 to 1).map(i => Record(i)), 1)
  val singleScan2: SparkPlan = PhysicalRDD(recordAttributes, singleRDD2)
  val singleRDD3: RDD[Record] = sparkContext.parallelize((50 to 50).map(i => Record(i)), 1)
  val singleScan3: SparkPlan = PhysicalRDD(recordAttributes, singleRDD3)

  // initialize a SparkPlan with empty iterator
  val nullRDD1: RDD[Record] = sparkContext.parallelize(new MutableList[ Record ](), 1)
  val nullScan1: SparkPlan = PhysicalRDD(recordAttributes, nullRDD1)
  val nullRDD2: RDD[Record] = sparkContext.parallelize(new MutableList[ Record ](), 1)
  val nullScan2: SparkPlan = PhysicalRDD(recordAttributes, nullRDD2)

 test ("simple join") {
    val outputRDD = GeneralSymmetricHashJoin(recordAttributes, recordAttributes, smallScan1, smallScan2).execute()
    var seenValues: HashSet[Row] = new HashSet[Row]()

    outputRDD.collect().foreach(x => seenValues = seenValues + x)

    (51 to 100).foreach(x => assert(seenValues.contains(new JoinedRow(Row(x), Row(x)))))
    assert( !seenValues.contains( null ) )
    assert( seenValues.size == 50 )
  }

  test ("complicated join") {
    val outputRDD = GeneralSymmetricHashJoin(Seq(complicatedAttributes(0)), Seq(complicatedAttributes(0)), complicatedScan1, complicatedScan2).execute()
    var seenValues: HashSet[Row] = new HashSet[Row]()

    outputRDD.collect().foreach(x => seenValues = seenValues + x)

    (51 to 100).foreach(x => assert(seenValues.contains(new JoinedRow(Row(x, x.toString, x*2), Row(x, x.toString, x*2)))))
    assert( !seenValues.contains( null ) )
    assert( seenValues.size == 50 )
  }

  test ("duplicated join") {
    val outputRDD = GeneralSymmetricHashJoin(recordAttributes, recordAttributes, duplicateScan1, duplicateScan2).execute()
    // use JavaArrayList to allow duplicate
    var seenValues: JavaArrayList[Row] = new JavaArrayList[Row]()

    outputRDD.collect().foreach(x => seenValues.add( x ) )

    // size should be 5 x 3 = 15
    assert( seenValues.size == 15 )
    (1 to 15).foreach(x => assert(seenValues.contains(new JoinedRow(Row(100), Row(100)))))
    assert( !seenValues.contains( null ) )
  }

  test ("asymmetric left and right iterator join") {
    val outputRDD = GeneralSymmetricHashJoin(recordAttributes, Seq(complicatedAttributes(0)), smallScan1, complicatedScan1).execute()
    var seenValues: HashSet[Row] = new HashSet[Row]()

    outputRDD.collect().foreach(x => seenValues = seenValues + x)

    (1 to 100).foreach(x => assert(seenValues.contains(new JoinedRow(Row(x), Row(x, x.toString, x*2)))))
    assert( !seenValues.contains( null ) )
    assert( seenValues.size == 100 )
  }

  test ("late simple join") {
    val outputRDD = GeneralSymmetricHashJoin(recordAttributes, recordAttributes, lateScan1, lateScan2).execute()
    var seenValues: HashSet[Row] = new HashSet[Row]()

    outputRDD.collect().foreach(x => seenValues = seenValues + x)

    (501 to 550).foreach(x => assert(seenValues.contains(new JoinedRow(Row(x), Row(x)))))
    assert( !seenValues.contains( null ) )
    assert( seenValues.size == 50 )
  }

   test ("empty join") {
    val outputRDD = GeneralSymmetricHashJoin(recordAttributes, recordAttributes, emptyScan1, emptyScan2).execute()
    var seenValues: HashSet[Row] = new HashSet[Row]()

    outputRDD.collect().foreach(x => seenValues = seenValues + x)

    assert( seenValues.size == 0 )
  }

  test ("single join with match") {
    val outputRDD = GeneralSymmetricHashJoin(recordAttributes, recordAttributes, singleScan1, singleScan2).execute()
    var seenValues: HashSet[Row] = new HashSet[Row]()

    outputRDD.collect().foreach(x => seenValues = seenValues + x)

    (1 to 1).foreach(x => assert(seenValues.contains(new JoinedRow(Row(x), Row(x)))))
    assert( !seenValues.contains( null ) )
    assert( seenValues.size == 1 )
  }

  test ("single join with no match") {
    val outputRDD = GeneralSymmetricHashJoin(recordAttributes, recordAttributes, singleScan1, singleScan3).execute()
    var seenValues: HashSet[Row] = new HashSet[Row]()

    outputRDD.collect().foreach(x => seenValues = seenValues + x)

    assert( seenValues.size == 0 )
  }

  test ("join with empty left iterator") {
    val outputRDD = GeneralSymmetricHashJoin(recordAttributes, recordAttributes, nullScan1, smallScan2).execute()
    var seenValues: HashSet[Row] = new HashSet[Row]()

    outputRDD.collect().foreach(x => seenValues = seenValues + x)

    assert( seenValues.size == 0 )
  }

  test ("join with empty right iterator") {
    val outputRDD = GeneralSymmetricHashJoin(recordAttributes, recordAttributes, smallScan1, nullScan2).execute()
    var seenValues: HashSet[Row] = new HashSet[Row]()

    outputRDD.collect().foreach(x => seenValues = seenValues + x)

    assert( seenValues.size == 0 )
  }

  test ("join with empty left and right iterator") {
    val outputRDD = GeneralSymmetricHashJoin(recordAttributes, recordAttributes, nullScan1, nullScan2).execute()
    var seenValues: HashSet[Row] = new HashSet[Row]()

    outputRDD.collect().foreach(x => seenValues = seenValues + x)

    assert( seenValues.size == 0 )
  }

}
