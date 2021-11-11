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

  "parseBooleanAt" should "return Boolean if it is present at given index" in {
    val derSeq      = DERSequence(Array[ASN1Encodable](ASN1Boolean.TRUE))
    val narrowedSeq = ASN1TypeNarrowedSeq(derSeq)

    val result = narrowedSeq.parseBooleanAt(0)

    result.value shouldEqual true
  }

  it should "return OutOfSequenceRange failure when Boolean is not present at given index" in {
    createEmptyASN1TypeNarrowedSeq()
      .parseBooleanAt(99)
      .left
      .value shouldBe OutOfSequenceRange(99, 0)
  }

  "parseIntAt" should "return Int if it is present at given index" in {
    val expectedInt = 1
    val derSeq      = DERSequence(Array[ASN1Encodable](ASN1Integer(expectedInt)))
    val narrowedSeq = ASN1TypeNarrowedSeq(derSeq)

    val result = narrowedSeq.parseIntAt(0)

    result.value shouldEqual expectedInt
  }

  it should "return OutOfSequenceRange failure when Int is not present at given index" in {
    createEmptyASN1TypeNarrowedSeq()
      .parseIntAt(99)
      .left
      .value shouldBe OutOfSequenceRange(99, 0)
  }

  "parseStringAt" should "return String if it is present at given index" in {
    val expectedString = "Hello"
    val derSeq         = DERSequence(Array[ASN1Encodable](DEROctetString(expectedString.getBytes)))
    val narrowedSeq    = ASN1TypeNarrowedSeq(derSeq)

    val result = narrowedSeq.parseStringAt(0)

    result.value shouldEqual expectedString
  }

  it should "return OutOfSequenceRange failure when String is not present at given index" in {
    createEmptyASN1TypeNarrowedSeq()
      .parseStringAt(99)
      .left
      .value shouldBe OutOfSequenceRange(99, 0)
  }

  "parseBytesAt" should "return Array[Byte] if it is present at given index" in {
    val expectedBytes = "Hello".getBytes
    val derSeq        = DERSequence(Array[ASN1Encodable](DEROctetString(expectedBytes)))
    val narrowedSeq   = ASN1TypeNarrowedSeq(derSeq)

    val result = narrowedSeq.parseBytesAt(0)

    result.value shouldEqual expectedBytes
  }

  it should "return OutOfSequenceRange failure when Array[Byte] is not present at given index" in {
    createEmptyASN1TypeNarrowedSeq()
      .parseBytesAt(99)
      .left
      .value shouldBe OutOfSequenceRange(99, 0)
  }

  "parseBytesOrEmptyAt" should "return Array[Byte] if it is present at given index" in {
    val expectedBytes = "Hello".getBytes
    val derSeq        = DERSequence(Array[ASN1Encodable](DEROctetString(expectedBytes)))
    val narrowedSeq   = ASN1TypeNarrowedSeq(derSeq)

    val result = narrowedSeq.parseBytesOrEmptyAt(0)

    result.value shouldEqual expectedBytes
  }

  it should "return empty Array[Byte] if index exceeds sequence size" in {
    val derSeq      = DERSequence(Array[ASN1Encodable]())
    val narrowedSeq = ASN1TypeNarrowedSeq(derSeq)

    val result = narrowedSeq.parseBytesOrEmptyAt(0)

    result.value.isEmpty shouldBe true
  }

  "parseTaggedObjectsAt" should "return ASN1TypeNarrowedTaggedObjects if it is present at given index" in {
    val firstTagValue      = 1 -> 10
    val secondTagValue     = 2 -> 20
    val firstTaggedObject  = DERTaggedObject(firstTagValue._1, ASN1Integer(firstTagValue._2))
    val secondTaggedObject = DERTaggedObject(secondTagValue._1, ASN1Integer(secondTagValue._2))
    val taggedObjectsSeq = DERSequence(Array[ASN1Encodable](firstTaggedObject, secondTaggedObject))
    val derSeq           = DERSequence(Array[ASN1Encodable](taggedObjectsSeq))
    val narrowedSeq      = ASN1TypeNarrowedSeq(derSeq)

    val result = narrowedSeq.parseTaggedObjectsAt(0)

    result.value.getInt(firstTagValue._1).value shouldBe Some(firstTagValue._2)
    result.value.getInt(secondTagValue._1).value shouldBe Some(secondTagValue._2)
  }

  "parseTaggedObjectsAt" should "return OutOfSequenceRange if given index exceeds sequence size" in {
    createEmptyASN1TypeNarrowedSeq()
      .parseTaggedObjectsAt(99)
      .left
      .value shouldBe OutOfSequenceRange(99, 0)
  }

  it should "return OutOfSequenceRange failure when ASN1TypeNarrowedTaggedObjects is not present at given index" in {
    createEmptyASN1TypeNarrowedSeq()
      .parseStringAt(99)
      .left
      .value shouldBe OutOfSequenceRange(99, 0)
  }

  private def createEmptyASN1TypeNarrowedSeq(): ASN1TypeNarrowedSeq =
    ASN1TypeNarrowedSeq(DERSequence(Array[ASN1Encodable]()))
}
