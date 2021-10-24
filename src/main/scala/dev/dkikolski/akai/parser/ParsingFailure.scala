package dev.dkikolski.akai.parser

sealed trait ParsingFailure() {
  def getReason(): String
}

final case class CertificateParsingFailure(reason: String) extends ParsingFailure{
  override def getReason(): String = reason
}

final case class TypeMismatch(
    val value: Any,
    val original: Class[?],
    val expected: Class[?]
) extends ParsingFailure {
  override def getReason(): String =
    s"$value of original type $original cannot be parsed as $expected"
}

final case class OutOfSequenceRange(
    val actual: Int,
    val len: Int
) extends ParsingFailure {
  override def getReason(): String = s"Index ${actual} is out of sequence range (length: $len)"
}
