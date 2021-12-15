package dev.dkikolski.akai.parser

import org.bouncycastle.asn1.ASN1Encodable
import org.bouncycastle.asn1.ASN1Primitive
import org.bouncycastle.asn1.ASN1Sequence
import org.bouncycastle.asn1.ASN1Set
import org.bouncycastle.asn1.ASN1TaggedObject
import org.bouncycastle.asn1.DEROctetString

import ASN1Conversions.*

private[parser] final class ASN1TypeNarrowedTaggedObjects(
    private val taggedValues: Map[Int, ASN1Primitive]
) {
  def getIntSetOrEmptyAt(tag: Int): Either[ParsingFailure, Set[Int]] =
    taggedValues
      .get(tag)
      .map(parseToIntSet)
      .getOrElse(Right(Set.empty))
      .left.map(addContextToFailure(_, tag))

  def getOptionalIntAt(tag: Int): Either[ParsingFailure, Option[Int]] =
    taggedValues
      .get(tag)
      .map(convertToInt(_).map(Some(_)))
      .getOrElse(Right(None))
      .left.map(addContextToFailure(_, tag))

  def getOptionalLongAt(tag: Int): Either[ParsingFailure, Option[Long]] =
    taggedValues
      .get(tag)
      .map(convertToLong(_).map(Some(_)))
      .getOrElse(Right(None))
      .left.map(addContextToFailure(_, tag))

  def getBooleanAt(tag: Int): Boolean = taggedValues.contains(tag)

  def getBytesOrEmptyAt(tag: Int): Either[ParsingFailure, Array[Byte]] =
    taggedValues
      .get(tag)
      .map(convertToBytes)
      .getOrElse(Right(Array.emptyByteArray))
      .left.map(addContextToFailure(_, tag))

  def getOptionalASN1SeqAt(
      tag: Int
  ): Either[ParsingFailure, Option[ASN1TypeNarrowedSeq]] =
    taggedValues
      .get(tag)
      .map(parseAsASN1TypeNarrowedSeq(_).map(Some(_)))
      .getOrElse(Right(None))
      .left.map(addContextToFailure(_, tag))

  private[this] def parseAsASN1TypeNarrowedSeq(
      primitive: ASN1Primitive
  ): Either[ParsingFailure, ASN1TypeNarrowedSeq] = {
    primitive match {
      case seq: ASN1Sequence => Right(ASN1TypeNarrowedSeq(seq))
      case octetString: DEROctetString =>
        Right(
          ASN1TypeNarrowedSeq(
            ASN1Primitive.fromByteArray(octetString.getOctets).asInstanceOf[ASN1Sequence]
          )
        )
      case other => Left(TypeMismatch(other, "ASN1Sequence"))
    }
  }

  private[this] def parseToIntSet(primitive: ASN1Primitive): Either[ParsingFailure, Set[Int]] = {
    primitive match {
      case set: ASN1Set => parseToIntSet(set.toArray)
      case other        => Left(TypeMismatch(other, "ASN1Set"))
    }
  }

  private[this] def parseToIntSet(
      encodables: Array[ASN1Encodable]
  ): Either[ParsingFailure, Set[Int]] =
    encodables
      .map(ASN1Conversions.convertToInt)
      .foldRight(Right(Set.empty): Either[ParsingFailure, Set[Int]])((e, acc) =>
        for (xs <- acc; x <- e) yield xs + x
      )

  private[this] def addContextToFailure(f: ParsingFailure, tag: Int): ParsingFailure =
    f.updateContextMessage(
      s"Parsing tag: '$tag' in type narrowed tagged objects ${this.taggedValues}"
   )
}

object ASN1TypeNarrowedTaggedObjects {
  def apply(seq: ASN1Sequence): ASN1TypeNarrowedTaggedObjects = {
    val taggedValues: Map[Int, ASN1Primitive] =
      seq.toArray
        .map(_.asInstanceOf[ASN1TaggedObject])
        .map(it => it.getTagNo -> it.getObject)
        .toMap

    new ASN1TypeNarrowedTaggedObjects(taggedValues)
  }
}
