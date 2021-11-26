package dev.dkikolski.akai.cli

import java.io.File

sealed trait Operation

final case class ShowHelp(content: String) extends Operation

final case class Discard(reason: String) extends Operation

enum OutputFormat {
    case JSON, Table
}

enum OutputValuesFormat {
    case Raw, HumanFriendly
}

final case class ParseCertificate(
    location: Option[File] = None,
    decodeBase64: Boolean = false,
    outputFormat: OutputFormat = OutputFormat.Table,
    outputValuesFormat: OutputValuesFormat = OutputValuesFormat.HumanFriendly
) extends Operation
