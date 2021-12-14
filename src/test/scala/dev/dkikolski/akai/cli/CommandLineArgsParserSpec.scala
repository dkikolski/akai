package dev.dkikolski.akai.cli

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.GivenWhenThen
import org.scalatest.matchers.should.Matchers._
import scala.util.CommandLineParser
import java.io.File

class CommandLineArgsParserSpec extends AnyFlatSpec {

  "Parser" should "return 'Discard' operation when flag is not recognized" in {
    val usnupportedFlag          = "--test"
    val expectedDiscardOperation = Discard(s"Unsupported option(s): ${usnupportedFlag}")

    val actualParsedOperation = CommandLineArgsParser.parse(Seq(usnupportedFlag))

    actualParsedOperation shouldBe expectedDiscardOperation
  }

  it should "return 'Show Help' operation when '--help' flag is passed" in {
    val actualOperation = CommandLineArgsParser.parse(Seq("--help"))

    actualOperation shouldBe a[ShowHelp]
  }

  it should "return 'Show Help' operation when '--help' is passed together with other supported flag" in {
    val actualOperation = CommandLineArgsParser.parse(Seq("--base64decode", "--help"))

    actualOperation shouldBe a[ShowHelp]
  }

  it should "return 'Show Help' operation when '--help' is passed together with unsupported flag" in {
    val actualOperation = CommandLineArgsParser.parse(Seq("--test", "--help"))

    actualOperation shouldBe a[ShowHelp]
  }

  it should "return 'Discard' operation when too many file locations are passed" in {
    val givenPaths = Seq("/dev/test/cert0.pem", "/dev/test/cert1.pem")
    val expectedDiscardOperation = Discard(
      s"Only single file/directory path is supported. Too many locations: ${givenPaths.mkString(", ")}"
    )

    val actualParsedOperation = CommandLineArgsParser.parse(givenPaths)

    actualParsedOperation shouldBe expectedDiscardOperation
  }

  it should "return 'Parse Ceftificate' operation when certificate location is provided without additional parameters" in {
    val givenCertPath = "/dev/test/cert0.pem"
    val expectedParseCertificateOperation = ParseCertificate(
      location = Some(File(givenCertPath)),
      decodeBase64 = false,
      outputValuesFormat = OutputValuesFormat.HumanFriendly
    )

    val actualParsedOperation = CommandLineArgsParser.parse(Seq(givenCertPath))

    actualParsedOperation shouldBe expectedParseCertificateOperation
  }

  it should "return 'Parse Certificate' operation when location is not provided and additional parameters are empty" in {
    val expectedParseCertificateOperation = ParseCertificate(
      location = None,
      decodeBase64 = false,
      outputValuesFormat = OutputValuesFormat.HumanFriendly
    )

    val actualParsedOperation = CommandLineArgsParser.parse(Seq())

    actualParsedOperation shouldBe expectedParseCertificateOperation
  }

  it should "return 'Parse Certificate' operation with 'decodeBase64' option when '--decode-base64' is provided in args" in {
    val args = Seq("--decode-base64")
    val expectedParseCertificateOperation = ParseCertificate(
      location = None,
      decodeBase64 = true,
      outputValuesFormat = OutputValuesFormat.HumanFriendly
    )

    val actualParsedOperation = CommandLineArgsParser.parse(args)

    actualParsedOperation shouldBe expectedParseCertificateOperation
  }

  it should "return 'Parse Certificate' operation with raw values in output when '--raw-values' is provided in args" in {
    val args = Seq("--raw-values")
    val expectedParseCertificateOperation = ParseCertificate(
      location = None,
      decodeBase64 = false,
      outputValuesFormat = OutputValuesFormat.Raw
    )

    val actualParsedOperation = CommandLineArgsParser.parse(args)

    actualParsedOperation shouldBe expectedParseCertificateOperation
  }
}
