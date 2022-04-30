package dev.dkikolski.akai

import dev.dkikolski.akai.cli.CommandLineArgsParser
import dev.dkikolski.akai.cli.Discard
import dev.dkikolski.akai.cli.ParseCertificate
import dev.dkikolski.akai.cli.ShowHelp
import dev.dkikolski.akai.input.CertificateReader
import dev.dkikolski.akai.output.KeyDescriptionRenderer
import dev.dkikolski.akai.output.TableRenderer
import dev.dkikolski.akai.parser.CertificateParser
import dev.dkikolski.akai.parser.KeyDescriptionParser
import dev.dkikolski.akai.parser.ParsingFailure
import dev.dkikolski.akai.schema.AuthorizationList
import dev.dkikolski.akai.schema.SecurityLevel

import java.io.ByteArrayInputStream
import java.io.File
import java.io.FileInputStream
import java.io.InputStream
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate
import scala.annotation.tailrec
import scala.io.Source
import scala.io.StdIn
import scala.util.Failure
import scala.util.Success
import scala.util.Try
import scala.util.Using

@main def main(args: String*): Unit = {
  CommandLineArgsParser.parse(args) match {
    case d: Discard          => handleUnsupportedAction(d)
    case h: ShowHelp         => handleShowHelp(h)
    case p: ParseCertificate => handleParsingCertificate(p)
  }
}

// TODO: Handle IO operations in more functional way

def handleUnsupportedAction(action: Discard): Unit = println(action.reason)

def handleShowHelp(action: ShowHelp): Unit = println(action.content)

def handleParsingCertificate(action: ParseCertificate): Unit = {
  val certificateSource  = action.location.map(FileInputStream(_)).getOrElse(System.in)
  val certificateBytesIn = CertificateReader.read(certificateSource)

  val certificateParsingResult = for {
    certificateBytes     <- decodeCertificateIfRequired(certificateBytesIn, action.decodeBase64)
    certificate          <- CertificateParser.parse(certificateBytes)
    keyDescCertExtension <- CertificateParser.getKeyDescriptionExtension(certificate)
    parsedKeyDescription <- KeyDescriptionParser.parse(keyDescCertExtension)
  } yield parsedKeyDescription

  certificateParsingResult match {
    case Right(keyDescription) =>
      println(
        KeyDescriptionRenderer.render(
          keyDescription,
          action.outputValuesFormat
        )
      )
    case Left(failure) => println(s"[ERROR] ${failure.getContextMessage()}: ${failure.getReason()}")
  }
}

def decodeCertificateIfRequired(
    bytes: Array[Byte],
    decodeBase64: Boolean
): Either[ParsingFailure, Array[Byte]] = {
  if (decodeBase64) CertificateParser.decodeBase64(bytes) else Right(bytes)
}
