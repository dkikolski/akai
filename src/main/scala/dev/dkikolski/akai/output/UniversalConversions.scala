package dev.dkikolski.akai.output

object UniversalConversions {

  def bytesToHex(bytes: Array[Byte]): Array[String] =
    bytes.map(String.format("%02x", _))

  def convertToString(value: Any): String = value match {
    case None                    => ""
    case Some(x)                 => convertToString(x)
    case bytes: Array[Byte]      => bytesToHex(bytes).mkString(" ")
    case collection: Iterable[_] => collection.map(convertToString).mkString(", ")
    case other                   => other.toString
  }
}
