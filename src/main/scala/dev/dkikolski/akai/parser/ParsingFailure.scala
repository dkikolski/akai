package dev.dkikolski.akai.parser

sealed trait ParsingFailure() {
  def getReason(): String
  
  def getContextMessage(): String
  
  def updateContextMessage(msg: String): ParsingFailure
}

final case class CertificateParsingFailure(reason: String, context: String = "") extends ParsingFailure {
  override def getReason(): String = reason

  override def getContextMessage(): String = context

  override def updateContextMessage(msg: String) =
    CertificateParsingFailure(reason, s"$msg: $context")
}

final case class TypeMismatch(
    value: Any,
    expectedType: String,
    context: String = ""
) extends ParsingFailure {
  override def getReason(): String =
    s"Cannot parse $value as a/an $expectedType"

  override def getContextMessage(): String = context  

  override def updateContextMessage(update: String) =
    TypeMismatch(value, expectedType, s"$update $context")
}

final case class OutOfSequenceRange(
    actual: Int,
    len: Int,
    context: String = ""
) extends ParsingFailure {
  override def getReason(): String = s"Index $actual is out of sequence range (length: $len)"

  override def getContextMessage(): String = context

  override def updateContextMessage(update: String) =
    OutOfSequenceRange(actual, len, s"$update $context")
}

final case class UnmatchedEnumeration(
    actual: Int,
    enumerationName: String,
    context: String = ""
) extends ParsingFailure {
  override def getReason(): String = s"Value $actual cannot be mapped to ${enumerationName}"

  override def getContextMessage(): String = context

  override def updateContextMessage(update: String) =
    UnmatchedEnumeration(actual, enumerationName, s"$update $context")

}
