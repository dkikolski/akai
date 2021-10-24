package dev.dkikolski.akai.cli

import scala.annotation.tailrec
import scala.util.Failure
import scala.util.Success
import scala.util.Try

object CommandLineArgsParser {
  private val HelpText =
    """Usage: akai [OPTION]... [FILE]\n
      |With no FILE, read standard input.
      | 
      |Options
      |  --base64decode     decode the input using Base64 decoding scheme 
      |                     before parsing as a X509 certificate 
      |  
      |  --help             display this help message and exit
      |
    """.stripMargin

  private val Base64DecodeOption = "--base64decode"
  private val ShowHelpOption     = "--help"

  private val SupportedOptions = Set(ShowHelpOption, Base64DecodeOption)

  def parse(args: Seq[String]): Operation = {

    val (options, locations) = parseRec(args, List(), List())

    val unsupportedSwitches = options.filter(key => !SupportedOptions.contains(key))

    if (options.contains(ShowHelpOption))
      ShowHelp(HelpText)
    else if (!unsupportedSwitches.isEmpty)
      Discard(s"Unsupported option(s): ${unsupportedSwitches.mkString(", ")}")
    else if (locations.size > 1)
      Discard(
        s"Only single file/directory path is supported. Too many locations: ${locations.mkString(", ")}"
      )
    else 
      ParseCertificate(path = locations.headOption, decodeBase64 = options.contains(Base64DecodeOption))
  }

  @tailrec
  private[this] def parseRec(
      args: Seq[String],
      options: List[String],
      locations: List[String]
  ): (List[String], List[String]) = {
    args match {
      case Nil => (options, locations)
      case option :: tail if (option.startsWith("--")) =>
        parseRec(tail, options :+ option, locations)
      case location :: tail => parseRec(tail, options, locations :+ location)
    }
  }
}
