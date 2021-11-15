package dev.dkikolski.akai.parser

import org.bouncycastle.asn1.ASN1Boolean
import org.bouncycastle.asn1.ASN1Encodable
import org.bouncycastle.asn1.ASN1Integer
import org.bouncycastle.asn1.DEROctetString
import org.bouncycastle.asn1.DERSequence
import org.bouncycastle.asn1.DERTaggedObject
import org.scalatest.EitherValues
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import org.scalatest.prop.Tables.Table

class ASN1TypeNarrowedSeqSpec extends AnyFlatSpec with EitherValues with Matchers {

  "getBooleanAt" should "return Boolean if it is present at given index" in {
    val derSeq      = DERSequence(Array[ASN1Encodable](ASN1Boolean.TRUE))
    val narrowedSeq = ASN1TypeNarrowedSeq(derSeq)

    val result = narrowedSeq.getBooleanAt(0)

    result.value shouldEqual true
  }

  it should "return OutOfSequenceRange failure when Boolean is not present at given index" in {
    createEmptyASN1TypeNarrowedSeq()
      .getBooleanAt(99)
      .left
      .value shouldBe OutOfSequenceRange(99, 0)
  }

  "getIntAt" should "return Int if it is present at given index" in {
    val expectedInt = 1
    val derSeq      = DERSequence(Array[ASN1Encodable](ASN1Integer(expectedInt)))
    val narrowedSeq = ASN1TypeNarrowedSeq(derSeq)

    val result = narrowedSeq.getIntAt(0)

    result.value shouldEqual expectedInt
  }

  it should "return OutOfSequenceRange failure when Int is not present at given index" in {
    createEmptyASN1TypeNarrowedSeq()
      .getIntAt(99)
      .left
      .value shouldBe OutOfSequenceRange(99, 0)
  }

  "getStringAt" should "return String if it is present at given index" in {
    val expectedString = "Hello"
    val derSeq         = DERSequence(Array[ASN1Encodable](DEROctetString(expectedString.getBytes)))
    val narrowedSeq    = ASN1TypeNarrowedSeq(derSeq)

    val result = narrowedSeq.getStringAt(0)

    result.value shouldEqual expectedString
  }

  it should "return OutOfSequenceRange failure when String is not present at given index" in {
    createEmptyASN1TypeNarrowedSeq()
      .getStringAt(99)
      .left
      .value shouldBe OutOfSequenceRange(99, 0)
  }

  "getBytesAt" should "return Array[Byte] if it is present at given index" in {
    val expectedBytes = "Hello".getBytes
    val derSeq        = DERSequence(Array[ASN1Encodable](DEROctetString(expectedBytes)))
    val narrowedSeq   = ASN1TypeNarrowedSeq(derSeq)

    val result = narrowedSeq.getBytesAt(0)

    result.value shouldEqual expectedBytes
  }

  it should "return OutOfSequenceRange failure when Array[Byte] is not present at given index" in {
    createEmptyASN1TypeNarrowedSeq()
      .getBytesAt(99)
      .left
      .value shouldBe OutOfSequenceRange(99, 0)
  }

  "getBytesOrEmptyAt" should "return Array[Byte] if it is present at given index" in {
    val expectedBytes = "Hello".getBytes
    val derSeq        = DERSequence(Array[ASN1Encodable](DEROctetString(expectedBytes)))
    val narrowedSeq   = ASN1TypeNarrowedSeq(derSeq)

    val result = narrowedSeq.getBytesOrEmptyAt(0)

    result.value shouldEqual expectedBytes
  }

  it should "return empty Array[Byte] if sequence is empty" in {
    val derSeq      = DERSequence(Array[ASN1Encodable]())
    val narrowedSeq = ASN1TypeNarrowedSeq(derSeq)

    val result = narrowedSeq.getBytesOrEmptyAt(0)

    result.value.isEmpty shouldBe true
  }

  it should "return empty Array[Byte] if given index exceeds sequence size" in {
    val expectedBytes = "Hello".getBytes
    val derSeq        = DERSequence(Array[ASN1Encodable](DEROctetString(expectedBytes)))
    val narrowedSeq = ASN1TypeNarrowedSeq(derSeq)

    val result = narrowedSeq.getBytesOrEmptyAt(2)

    result.value.isEmpty shouldBe true
  }

  "getTaggedObjectsAt" should "return ASN1TypeNarrowedTaggedObjects if it is present at given index" in {
    val firstTagValue      = 1 -> 10
    val secondTagValue     = 2 -> 20
    val firstTaggedObject  = DERTaggedObject(firstTagValue._1, ASN1Integer(firstTagValue._2))
    val secondTaggedObject = DERTaggedObject(secondTagValue._1, ASN1Integer(secondTagValue._2))
    val taggedObjectsSeq = DERSequence(Array[ASN1Encodable](firstTaggedObject, secondTaggedObject))
    val derSeq           = DERSequence(Array[ASN1Encodable](taggedObjectsSeq))
    val narrowedSeq      = ASN1TypeNarrowedSeq(derSeq)

    val result = narrowedSeq.getTaggedObjectsAt(0)

    result.value.getOptionalIntAt(firstTagValue._1).value shouldBe Some(firstTagValue._2)
    result.value.getOptionalIntAt(secondTagValue._1).value shouldBe Some(secondTagValue._2)
  }

  "parseTaggedObjectsAt" should "return OutOfSequenceRange if given index exceeds sequence size" in {
    createEmptyASN1TypeNarrowedSeq()
      .getTaggedObjectsAt(99)
      .left
      .value shouldBe OutOfSequenceRange(99, 0)
  }

  it should "return OutOfSequenceRange failure when ASN1TypeNarrowedTaggedObjects is not present at given index" in {
    createEmptyASN1TypeNarrowedSeq()
      .getStringAt(99)
      .left
      .value shouldBe OutOfSequenceRange(99, 0)
  }

  private def createEmptyASN1TypeNarrowedSeq(): ASN1TypeNarrowedSeq =
    ASN1TypeNarrowedSeq(DERSequence(Array[ASN1Encodable]()))
}
