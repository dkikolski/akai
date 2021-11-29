package dev.dkikolski.akai

package object output {
 
  def bytesToHex(bytes: Array[Byte]): Array[String] =
    bytes.map(String.format("%02x", _))

  def bytesToPrintableCharString(bytes: Array[Byte]): String = 
    bytes.map(b => if (b >= 32) b.toChar else '.').mkString("")
}
