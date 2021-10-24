package dev.dkikolski.akai.cli

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.GivenWhenThen

class CommandLineArgsParserSpec extends AnyFlatSpec {

  "Parser" should "return Unsupported action when flag is not recognized" in {
    val usnupportedFlag = "--test"
    val expected        = Discard(s"Unsupported option(s): ${usnupportedFlag}")
    val actual          = CommandLineArgsParser.parse(Seq(usnupportedFlag))

    assert(actual === expected)
  }

  it should "return ShowHelp action when '--help' is only flag" in {
    val actual = CommandLineArgsParser.parse(Seq("--help"))

    assert(actual.isInstanceOf[ShowHelp])
  }

  it should "return ShowHelp action when '--help' is passed together with other supported flag" in {
    val actual = CommandLineArgsParser.parse(Seq("--base64decode", "--help"))

    assert(actual.isInstanceOf[ShowHelp])
  }

  it should "return ShowHelp action when '--help' is passed together with unsupported flag" in {
    val actual = CommandLineArgsParser.parse(Seq("--test", "--help"))

    assert(actual.isInstanceOf[ShowHelp])
  }

  it should "return Unsupported action when too many file localizations are passed" in {
    val givenPaths = Seq("/dev/test/cert0.pem", "/dev/test/cert1.pem")
    val expected = Discard(
      s"Only single file/directory path is supported. Too many locations: ${givenPaths.mkString(", ")}"
    )
    val actual = CommandLineArgsParser.parse(givenPaths)

    assert(actual === expected)
  }
}
