package com.gliwka.hyperscan.benchmark

import java.util
import java.util.concurrent.TimeUnit
import java.util.regex.Pattern
import scala.collection.JavaConversions._
import com.gliwka.hyperscan.wrapper._
import org.openjdk.jmh.annotations._

case class ScannerAndDB(scanner: Scanner, db: Database)

/**
  * This benchmark simply generate UUIDs and replace all digits to character 'd'
  */
@State(Scope.Benchmark)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@BenchmarkMode(Array(Mode.Throughput))
class ReplaceCharBenchmark {

  val pattern: String = """\d"""
  var expr: Expression = _
  var db: Database = _
  var scanner: Scanner = _
  var str: String = _
  val javaRegex = Pattern.compile(pattern)

  @Setup(Level.Trial)
  def TrailFixture(): Unit = {
    expr = new Expression(pattern, util.EnumSet.of(ExpressionFlag.SOM_LEFTMOST))
    db = Database.compile(expr)
    scanner = new Scanner()
    scanner.allocScratch(db)
  }

  @Setup(Level.Iteration)
  def IterationFixture(): Unit = {
    str = java.util.UUID.randomUUID.toString
  }

  /**
    * Replace digits via HyperScan, with pre-build regex
    */
  @Benchmark
  def HyperScan(): Unit = {
    val strBld = new StringBuilder(str)
    HyperScanReplace(matcheItr = scanner.scan(db, str).iterator.toIterator, strBld = strBld, replacement = "d")
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
  def Native(): Unit = javaRegex.matcher(str).replaceAll("d")
}
