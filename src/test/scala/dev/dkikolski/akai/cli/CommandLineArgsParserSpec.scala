package dev.dkikolski.akai.cli

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.GivenWhenThen
import org.scalatest.matchers.should.Matchers._

class CommandLineArgsParserSpec extends AnyFlatSpec {

  "Parser" should "return Discard operation when flag is not recognized" in {
    val usnupportedFlag  = "--test"
    val discardOperation = Discard(s"Unsupported option(s): ${usnupportedFlag}")
    val parsedOperation  = CommandLineArgsParser.parse(Seq(usnupportedFlag))

    parsedOperation shouldBe discardOperation
  }

  it should "return Show Help operation when '--help' is only flag" in {
    val operation = CommandLineArgsParser.parse(Seq("--help"))

    operation shouldBe a[ShowHelp]
  }

  it should "return Show Help operation when '--help' is passed together with other supported flag" in {
    val operation = CommandLineArgsParser.parse(Seq("--base64decode", "--help"))

    operation shouldBe a[ShowHelp]
  }

  it should "return Show Help operation when '--help' is passed together with unsupported flag" in {
    val operation = CommandLineArgsParser.parse(Seq("--test", "--help"))

    operation shouldBe a[ShowHelp]
  }

  it should "return Discard operation when too many file locations are passed" in {
    val givenPaths = Seq("/dev/test/cert0.pem", "/dev/test/cert1.pem")
    val discardOperation = Discard(
      s"Only single file/directory path is supported. Too many locations: ${givenPaths.mkString(", ")}"
    )
    val parsedOperation = CommandLineArgsParser.parse(givenPaths)

    parsedOperation shouldBe discardOperation
  }
}
