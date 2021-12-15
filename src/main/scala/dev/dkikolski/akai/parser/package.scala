package dev.dkikolski.akai

package object parser {
    def bytesToHex(bytes: Array[Byte]): Array[String] =
    bytes.map(String.format("%02x", _))
}
