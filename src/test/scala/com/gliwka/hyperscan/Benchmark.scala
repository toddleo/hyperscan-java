package com.gliwka.hyperscan

import java.util
import org.scalatest.FlatSpec
import scala.collection.JavaConversions._
import com.gliwka.hyperscan.wrapper._

class Benchmark extends FlatSpec {

  behavior of "Benchmark on HyperScan against native Java Regex"

  private val ROUNDS: Long = 1000000L
  private val pattern: String = """\d"""

  it should "run really quick (but it's not) via HyperScan, with pre-build regex" in {
    (0L to ROUNDS).foreach { _ =>
      val str = java.util.UUID.randomUUID.toString
      val strBld = new StringBuilder(str)
      HyperScanReplace(scanner.scan(db, str).iterator.toIterator, strBld, "d")
    }
  }

  /**
    * HyperScan cannot run in parallel, an error of "The scratch region was already in use."
    * suggests scratch region cannot be accessed in the same time.
    */

  it should "run much slower in native Java Regex" in {
    (0L to ROUNDS).foreach { _ =>
      NativeReplace(java.util.UUID.randomUUID.toString, pattern, "d")
    }
  }

  it should "run much slower in native Java Regex, in parallel" in {
    (0L to ROUNDS).par.foreach { _ =>
      NativeReplace(java.util.UUID.randomUUID.toString, pattern, "d")
    }
  }

  private def HyperScanReplace(matcheItr: Iterator[Match], strBld: StringBuilder, replacement: String): StringBuilder = {
    matcheItr.foreach { _match: Match =>
      val offset: Int = _match.getStartPosition.toInt
      strBld.replace(offset, offset + 1, replacement)
    }
    strBld
  }

  private def NativeReplace(str: String, pattern: String, to: String): String = str.replaceAll(pattern, to)

  private val expr: Expression = new Expression(pattern, util.EnumSet.of(ExpressionFlag.SOM_LEFTMOST))
  private val db = Database.compile(expr)
  private val scanner = new Scanner()
  scanner.allocScratch(db)
}
