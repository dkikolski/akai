package dev.dkikolski.akai.cli

import java.io.File
import scala.annotation.tailrec
import scala.util.Failure
import scala.util.Success
import scala.util.Try

object CommandLineArgsParser {
  private val HelpText =
    """Usage: akai [OPTION]... [FILE]
      |With no FILE, read standard input.
      | 
      |Options
      |  --decode-base64           decode the input using Base64 decoding scheme 
      |                            before parsing as a X509 certificate 
      |
      |  --raw-values              do not translate schema values to human 
      |                            friendly format e.g. octet strings will 
      |                            be printed in hex format instead of 
      |                            converting to pritable characters
      |
      |  --human-friendly-values   translate schema values to human friendly
      |                            format e.g. map integer to corresponding 
      |                            enum values (default option)
      |
      |  --help                    display this help message and exit
      |
    """.stripMargin

  private val ShowHelpOption            = "--help"
  private val Base64DecodeOption        = "--decode-base64"
  private val RawValuesOption           = "--raw-values"
  private val HumanFriendlyValuesOption = "--human-friendly-values"

  private val SupportedOptions =
    Set(
      ShowHelpOption,
      Base64DecodeOption,
      RawValuesOption,
      HumanFriendlyValuesOption
    )

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
      ParseCertificate(
        location = locations.headOption.map(File(_)),
        decodeBase64 = options.contains(Base64DecodeOption),
        outputValuesFormat = resolveValuesFormat(options)
      )
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

  private[this] def resolveValuesFormat(options: List[String]): OutputValuesFormat = {
    if (options.indexOf(RawValuesOption) > options.indexOf(HumanFriendlyValuesOption))
      OutputValuesFormat.Raw
    else OutputValuesFormat.HumanFriendly
  }
}
