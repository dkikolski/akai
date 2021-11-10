package dev.dkikolski.akai.parser

import org.bouncycastle.asn1.ASN1Boolean
import org.bouncycastle.asn1.ASN1Enumerated
import org.bouncycastle.asn1.ASN1Integer
import org.bouncycastle.asn1.ASN1OctetString
import org.bouncycastle.asn1.DEROctetString
import org.scalatest.EitherValues
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers._

import ASN1TypeConversions._

class ASN1TypeConversionsSpec extends AnyFlatSpec with EitherValues {

  "convertToInt" should "return Int value when valid ASN1Integer is used" in {
    val expected = 123
    val result   = convertToInt(new ASN1Integer(expected))

    result.value shouldEqual expected
  }

  it should "return Int value when valid ASN1Enumerated is used" in {
    val expected = 123
    val result   = convertToInt(new ASN1Enumerated(expected))

    result.value shouldEqual expected
  }

  it should "return Type Mismatch failure when unsupported ASN1 type is used" in {
    val asn1Boolean = ASN1Boolean.getInstance(false)
    val result      = convertToInt(asn1Boolean)

    result.left.value shouldBe TypeMismatch(asn1Boolean, "Int")
  }

  "convertToLong" should "return Long value when valid ASN1Integer is used" in {
    val expected = 123L
    val result   = convertToLong(new ASN1Integer(expected))

    result.value shouldEqual expected
  }

  it should "return Type Mismatch failure when unsupported ASN1 type is used" in {
    val asn1Boolean = ASN1Boolean.getInstance(false)
    val result      = convertToLong(asn1Boolean)

    result.left.value shouldBe TypeMismatch(asn1Boolean, "Long")
  }

  "convertToBoolean" should "return boolean value when ASN1Boolean is used" in {
    val asn1Boolean = ASN1Boolean.getInstance(true)
    val result      = convertToBoolean(asn1Boolean)

    result.value shouldBe true
  }

  it should "return Type Mismatch failure when unsupported ASN1 type is used" in {
    val asn1Integer = new ASN1Integer(1)
    val result      = convertToBoolean(asn1Integer)

    result.left.value shouldBe TypeMismatch(asn1Integer, "Boolean")
  }

  "convertToString" should "return String representation of ASN1OctetString" in {
    val expected = "Hello"
    val result   = convertToString(new DEROctetString(expected.getBytes))

    result.value shouldEqual expected
  }

  it should "return Type Mismatch failure when unsupported ASN1 type is used" in {
    val asn1Boolean = ASN1Boolean.getInstance(true)
    val result      = convertToString(asn1Boolean)

    result.left.value shouldBe TypeMismatch(asn1Boolean, "String")
  }

  "convertToBytes" should "return String representation of ASN1OctetString" in {
    val expected = "Hello".getBytes
    val result   = convertToBytes(new DEROctetString(expected))

    result.value shouldEqual expected
  }

  it should "return Type Mismatch failure when unsupported ASN1 type is used" in {
    val asn1Boolean = ASN1Boolean.getInstance(true)
    val result      = convertToBytes(asn1Boolean)

    result.left.value shouldBe TypeMismatch(asn1Boolean, "Array[Byte]")
  }
}
