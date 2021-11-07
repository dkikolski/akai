package dev.dkikolski.akai.parser

import org.bouncycastle.asn1.ASN1Boolean
import org.bouncycastle.asn1.ASN1Encodable
import org.bouncycastle.asn1.ASN1Enumerated
import org.bouncycastle.asn1.ASN1Integer
import org.bouncycastle.asn1.ASN1OctetString
import org.bouncycastle.asn1.ASN1Sequence

import ASN1TypeConversions._

private[parser] final class ASN1TypeNarrowedSeq(private val seq: ASN1Sequence) {

  def parseBooleanAt(index: Int): Either[ParsingFailure, Boolean] =
    getAt(index).flatMap(convertToBoolean)

  def parseIntAt(index: Int): Either[ParsingFailure, Int] =
    getAt(index).flatMap(convertToInt)

  def parseStringAt(index: Int): Either[ParsingFailure, String] =
    getAt(index).flatMap(convertToString)

  def parseTaggedObjectsAt(index: Int): Either[ParsingFailure, ASN1TypeNarrowedTaggedObjects] =
    getAt(index)
      .flatMap(convertToASN1Sequence)
      .map(ASN1TypeNarrowedTaggedObjects(_))

  def parseBytesAt(index: Int): Either[ParsingFailure, Array[Byte]] =
    getAt(index)
      .flatMap(convertToASN1OctetString)
      .map(_.getOctets)

  def parseBytesOrEmptyAt(index: Int): Either[ParsingFailure, Array[Byte]] =
    if (seq.size() > index) Right(Array.emptyByteArray)
    else parseBytesAt(index)

  private[this] def convertToASN1Sequence(
      encodable: ASN1Encodable
  ): Either[ParsingFailure, ASN1Sequence] =
    encodable match {
      case seq: ASN1Sequence => Right(seq)
      case other             => Left(TypeMismatch(other, other.getClass, classOf[ASN1Sequence]))
    }

  private[this] def convertToASN1OctetString(
      encodable: ASN1Encodable
  ): Either[ParsingFailure, ASN1OctetString] =
    encodable match {
      case octets: ASN1OctetString => Right(octets)
      case other => Left(TypeMismatch(other, other.getClass, classOf[ASN1OctetString]))
    }

  private[this] def getAt(index: Int): Either[ParsingFailure, ASN1Encodable] = {
    if (seq.size() > index) Right(seq.getObjectAt(index))
    else Left(OutOfSequenceRange(index, seq.size()))
  }
}

object ASN1TypeNarrowedSeq {
  def apply(seq: ASN1Sequence): ASN1TypeNarrowedSeq = new ASN1TypeNarrowedSeq(seq)
}
