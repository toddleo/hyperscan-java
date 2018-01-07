package com.gliwka.hyperscan.wrapper

import com.gliwka.hyperscan.jna.HyperscanLibraryDirect
import java.nio.charset.StandardCharsets
import java.util

import com.sun.jna.Pointer

class SingleRegexScanner extends Scanner {
  def SingleRegexScan(db: Database, input: String): util.LinkedList[Array[Long]] = {
    val dbPointer: Pointer = db.getPointer

    val utf8bytes = input.getBytes(StandardCharsets.UTF_8)
    val bytesLength = utf8bytes.length

    matchedIds.clear()
    val hsError: Int = HyperscanLibraryDirect.hs_scan(dbPointer, input, bytesLength,
      0, scratch, matchHandler, Pointer.NULL)

    if (hsError != 0) throw Util.hsErrorIntToException(hsError)

    matchedIds
  }
}
