package dev.dkikolski.akai.parser

sealed trait ParsingFailure() {
  def getReason(): String
}

final case class CertificateParsingFailure(reason: String) extends ParsingFailure {
  override def getReason(): String = reason
}

final case class TypeMismatch(
    val value: Any,
    val expectedType: String
) extends ParsingFailure {
  override def getReason(): String =
    s"Cannot parse $value as a/an $expectedType"
}

final case class OutOfSequenceRange(
    val actual: Int,
    val len: Int
) extends ParsingFailure {
  override def getReason(): String = s"Index $actual is out of sequence range (length: $len)"
}

final case class UnmatchedEnumeration(
    val actual: Int,
    val enumerationName: String
) extends ParsingFailure {
  override def getReason(): String = s"Value $actual cannot be mapped to ${enumerationName}"
}
