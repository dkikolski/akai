package dev.dkikolski.akai.parser

import org.bouncycastle.asn1.{
  ASN1Boolean,
  ASN1Encodable,
  ASN1Enumerated,
  ASN1Integer,
  ASN1OctetString,
  ASN1Sequence,
  ASN1Set
}
import ASN1Conversions.*

private[parser] final class ASN1TypeNarrowedSeq(private val seq: ASN1Sequence) {

  def getBooleanAt(index: Int): Either[ParsingFailure, Boolean] =
    getAt(index).flatMap(convertToBoolean).left.map(addContextToFailure(_, index))

  def getIntAt(index: Int): Either[ParsingFailure, Int] =
    getAt(index).flatMap(convertToInt).left.map(addContextToFailure(_, index))

  def getLongAt(index: Int): Either[ParsingFailure, Long] =
    getAt(index)
      .map(_.toASN1Primitive)
      .flatMap(convertToLong)
      .left
      .map(addContextToFailure(_, index))

  def getStringAt(index: Int): Either[ParsingFailure, String] =
    getAt(index).flatMap(convertToString).left.map(addContextToFailure(_, index))

  def getTaggedObjectsAt(index: Int): Either[ParsingFailure, ASN1TypeNarrowedTaggedObjects] =
    getAt(index)
      .flatMap(convertToASN1Sequence)
      .map(ASN1TypeNarrowedTaggedObjects(_))
      .left
      .map(addContextToFailure(_, index))

  def getBytesAt(index: Int): Either[ParsingFailure, Array[Byte]] =
    getAt(index)
      .flatMap(convertToASN1OctetString)
      .map(_.getOctets)
      .left
      .map(addContextToFailure(_, index))

  def getBytesOrEmptyAt(index: Int): Either[ParsingFailure, Array[Byte]] =
    if (seq.size() <= index) Right(Array.emptyByteArray)
    else getBytesAt(index).left.map(addContextToFailure(_, index))

  def getOptionalTypeNarrowedSequencesAt(
      index: Int
  ): Either[ParsingFailure, Set[ASN1TypeNarrowedSeq]] = {
    getAt(index) match {
      case Left(outOfRangeFailure) => Right(Set.empty)
      case Right(encodable) =>
        convertToASN1Set(encodable)
          .flatMap(convertToSequencesSet)
          .left
          .map(addContextToFailure(_, index))
    }
  }

  private[this] def addContextToFailure(f: ParsingFailure, index: Int): ParsingFailure = {
    val seqHex = seq.getEncoded.map(String.format("%02x", _)).mkString
    f.updateContextMessage(
      s"Parsing index: '$index' in TypeNarrowedSequence HEX($seqHex)"
    )
  }

  private[this] def convertToSequencesSet(
      asn1Set: ASN1Set
  ): Either[ParsingFailure, Set[ASN1TypeNarrowedSeq]] = {
    asn1Set.toArray.toSet
      .map(convertToASN1Sequence)
      .map(_.map(ASN1TypeNarrowedSeq(_)))
      .foldRight(Right(Set.empty): Either[ParsingFailure, Set[ASN1TypeNarrowedSeq]])((e, acc) => {
        for (xs <- acc; x <- e) yield xs + x
      })
  }

  def getOptionalBytesSeqenceAt(
      index: Int
  ): Either[ParsingFailure, Set[Array[Byte]]] = {
    getAt(index) match {
      case Left(failure)    => Right(Set.empty)
      case Right(encodable) => convertToASN1Set(encodable).flatMap(convertToByteArraysSets).left.map(addContextToFailure(_, index))
    }
  }

  private[this] def convertToByteArraysSets(
      asn1Set: ASN1Set
  ): Either[ParsingFailure, Set[Array[Byte]]] = {
    asn1Set.toArray.toSet
      .map(convertToASN1OctetString)
      .map(_.map(_.getOctets))
      .foldRight(Right(Set.empty): Either[ParsingFailure, Set[Array[Byte]]])((e, acc) => {
        for (xs <- acc; x <- e) yield xs + x
      })
  }

  private[this] def convertToASN1Sequence(
      encodable: ASN1Encodable
  ): Either[ParsingFailure, ASN1Sequence] =
    encodable match {
      case seq: ASN1Sequence => Right(seq)
      case other             => Left(TypeMismatch(other, "ASN1Sequence"))
    }

  private[this] def convertToASN1Set(
      encodable: ASN1Encodable
  ): Either[ParsingFailure, ASN1Set] =
    encodable match {
      case set: ASN1Set => Right(set)
      case other        => Left(TypeMismatch(other, "ASN1Set"))
    }

  private[this] def convertToASN1OctetString(
      encodable: ASN1Encodable
  ): Either[ParsingFailure, ASN1OctetString] =
    encodable match {
      case octets: ASN1OctetString => Right(octets)
      case other                   => Left(TypeMismatch(other, "ASN1OctetString"))
    }

  private[this] def getAt(index: Int): Either[ParsingFailure, ASN1Encodable] = {
    if (seq.size() > index) Right(seq.getObjectAt(index))
    else Left(OutOfSequenceRange(index, seq.size()))
  }
}

object ASN1TypeNarrowedSeq {
  def apply(seq: ASN1Sequence): ASN1TypeNarrowedSeq = new ASN1TypeNarrowedSeq(seq)
}
