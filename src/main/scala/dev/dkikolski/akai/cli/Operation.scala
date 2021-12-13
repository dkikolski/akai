package dev.dkikolski.akai.cli

import java.io.File

sealed trait Operation

final case class ShowHelp(content: String) extends Operation

final case class Discard(reason: String) extends Operation

enum OutputValuesFormat {
    case Raw, HumanFriendly
}

final case class ParseCertificate(
    location: Option[File] = None,
    decodeBase64: Boolean = false,
    outputValuesFormat: OutputValuesFormat = OutputValuesFormat.HumanFriendly
) extends Operation
