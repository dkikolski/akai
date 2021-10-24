import dev.dkikolski.akai.cli.CommandLineArgsParser
import dev.dkikolski.akai.cli.ParseCertificate
import dev.dkikolski.akai.cli.ShowHelp
import dev.dkikolski.akai.cli.Discard
import dev.dkikolski.akai.parser.CertificateParser
import dev.dkikolski.akai.parser.KeyDescriptionParser
import dev.dkikolski.akai.parser.ParsingFailure
import dev.dkikolski.akai.printer.TablePrinter
import dev.dkikolski.akai.schema.AuthorizationList
import dev.dkikolski.akai.schema.SecurityLevel

import java.io.ByteArrayInputStream
import java.io.File
import java.io.FileInputStream
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

private def handleUnsupportedAction(action: Discard): Unit = println(action.reason)

private def handleShowHelp(action: ShowHelp): Unit = println(action.content)

private def handleParsingCertificate(action: ParseCertificate): Unit = {
  val parsingResult = for {
    certificate <-
      if (action.path.isDefined) CertificateParser.parse(File(action.path.get))
      else CertificateParser.parse(Source.stdin.mkString.getBytes)
    keyDescCertExtension <- CertificateParser.getKeyDescriptionExtension(certificate)
    parsedKeyDescription <- KeyDescriptionParser.parse(keyDescCertExtension)
  } yield parsedKeyDescription

  parsingResult match {
    case Right(keyDescription) => println(TablePrinter.render(keyDescription))
    case Left(failure)         => println(s"[ERROR] ${failure.getReason()}")
  }
}
