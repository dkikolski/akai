package dev.dkikolski.akai.parser

import org.bouncycastle.asn1.ASN1Boolean
import org.bouncycastle.asn1.ASN1Encodable
import org.bouncycastle.asn1.ASN1Enumerated
import org.bouncycastle.asn1.ASN1Integer
import org.bouncycastle.asn1.ASN1OctetString
import org.bouncycastle.asn1.ASN1Sequence

import ASN1TypesConversions._

private[parser] final class ASN1TypeNarrowedSeq(private val seq: ASN1Sequence) {
  def tryGetBooleanAt(index: Int): Either[ParsingFailure, Boolean] =
    getAt(index).flatMap(convertToBoolean)

  def tryGgetIntAt(index: Int): Either[ParsingFailure, Int] =
    getAt(index).flatMap(convertToInt)

  def tryGetStringAt(index: Int): Either[ParsingFailure, String] =
    getAt(index).flatMap(convertToString)

  def tryGetTaggedValuesAt(index: Int): Either[ParsingFailure, ASN1TypeNarrowedTaggedObjects] =
    getAt(index).map(_.asInstanceOf[ASN1Sequence]).map(ASN1TypeNarrowedTaggedObjects(_)) //TODO: make it a safe cast

  def tryGetBytesAt(index: Int): Either[ParsingFailure, Array[Byte]] =
    getAt(index).map(_.asInstanceOf[ASN1OctetString]).map(_.getOctets) //TODO: make it a safe cast

  def tryGetBytesOrEmptyAt(index: Int): Either[ParsingFailure, Array[Byte]] =
    if (seq.size() > index) Right(Array.emptyByteArray)
    else tryGetBytesAt(index)

  private[this] def convertToASN1Sequence(
      encodable: ASN1Encodable
  ): Either[ParsingFailure, ASN1Sequence] =
    encodable match {
      case seq: ASN1Sequence => Right(seq)
      case other             => Left(TypeMismatch(other, other.getClass, classOf[ASN1Sequence]))
    }

  private[this] def getAt(index: Int): Either[ParsingFailure, ASN1Encodable] = {
    if (seq.size() > index) Right(seq.getObjectAt(index))
    else Left(OutOfSequenceRange(index, seq.size()))
  }
}

object ASN1TypeNarrowedSeq {
  def apply(seq: ASN1Sequence): ASN1TypeNarrowedSeq = new ASN1TypeNarrowedSeq(seq)
}