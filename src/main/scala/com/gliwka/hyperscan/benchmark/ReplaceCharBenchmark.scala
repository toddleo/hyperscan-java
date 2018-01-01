package com.gliwka.hyperscan.benchmark

import java.util
import java.util.concurrent.TimeUnit

import scala.collection.JavaConversions._
import com.gliwka.hyperscan.wrapper._
import org.openjdk.jmh.annotations._

case class ScannerAndDB(scanner: Scanner, db: Database)

/**
  * This benchmark simply generate UUIDs and replace all digits to character 'd'
  */
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@BenchmarkMode(Array(Mode.Throughput))
class ReplaceCharBenchmark {

  /**
    * Replace digits via HyperScan, with pre-build regex
    */
  @Benchmark
  def HyperScan(): Unit = {
    val scannerAndDB: ScannerAndDB = Compile("""\d""")
    val (scanner, db) = scannerAndDB.scanner -> scannerAndDB.db
    val str = java.util.UUID.randomUUID.toString
    val strBld = new StringBuilder(str)
    HyperScanReplace(matcheItr = scanner.scan(db, str).iterator.toIterator, strBld = strBld, replacement = "d")
  }

  private def Compile(pattern: String) = {
    val expr: Expression = new Expression(pattern, util.EnumSet.of(ExpressionFlag.SOM_LEFTMOST))
    val db = Database.compile(expr)
    val scanner = new Scanner()
    scanner.allocScratch(db)
    ScannerAndDB(scanner, db)
  }

  private def HyperScanReplace(matcheItr: Iterator[Match], strBld: StringBuilder, replacement: String): StringBuilder = {
    matcheItr.foreach { _match: Match =>
      val offset: Int = _match.getStartPosition.toInt
      strBld.replace(offset, offset + 1, replacement)
    }
    strBld
  }

  /**
    * Replace digits via native Java Regex
    */
  @Benchmark
  def Native: Unit = {
    val pattern: String = """\d"""
    java.util.UUID.randomUUID.toString.replaceAll(pattern, "d")
  }
}
