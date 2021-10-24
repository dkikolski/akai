package dev.dkikolski.akai.cli

sealed trait Operation

final case class ShowHelp(content: String) extends Operation

final case class Discard(reason: String) extends Operation

final case class ParseCertificate(
    path: Option[String] = None,
    decodeBase64: Boolean = false
) extends Operation