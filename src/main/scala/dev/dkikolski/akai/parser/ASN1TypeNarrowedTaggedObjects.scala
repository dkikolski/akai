package dev.dkikolski.akai.parser

import org.bouncycastle.asn1.ASN1Encodable
import org.bouncycastle.asn1.ASN1Primitive
import org.bouncycastle.asn1.ASN1Sequence
import org.bouncycastle.asn1.ASN1Set
import org.bouncycastle.asn1.ASN1TaggedObject

import java.time.Duration
import java.time.Instant

import ASN1TypeConversions._

private[parser] final class ASN1TypeNarrowedTaggedObjects(private val taggedValues: Map[Int, ASN1Primitive]) {
  def getIntSet(tag: Int): Either[ParsingFailure, Set[Int]] =
    taggedValues
      .get(tag)
      .map(_.asInstanceOf[ASN1Set]) // todo: make it safe 
      .map(_.toArray)
      .map(parseToIntSet)
      .getOrElse(Right(Set.empty))

  def getInt(tag: Int): Either[ParsingFailure, Option[Int]] =
    taggedValues
      .get(tag)
      .map(it => convertToInt(it).map(Some(_)))
      .getOrElse(Right(None))

  def getLong(tag: Int): Either[ParsingFailure, Option[Long]] =
    taggedValues
      .get(tag)
      .map(it => convertToLong(it).map(Some(_)))
      .getOrElse(Right(None))

  def getBoolean(tag: Int): Boolean = taggedValues.get(tag).isDefined

  def getInstant(tag: Int): Either[ParsingFailure, Option[Instant]] =
    taggedValues
      .get(tag)
      .map(it => parseToInstant(it).map(Some(_)))
      .getOrElse(Right(None))

  def getDuration(tag: Int): Either[ParsingFailure, Option[Duration]] =
    taggedValues
      .get(tag)
      .map(it => parseToDurationFromSeconds(it).map(Some(_)))
      .getOrElse(Right(None))

  def getBytes(tag: Int): Either[ParsingFailure, Array[Byte]] = 
    taggedValues
    .get(tag)
    .map(it => convertToBytes(it))
    .getOrElse(Right(Array.emptyByteArray))

  def getASN1TypeNarrowedSeq(tag: Int): Either[ParsingFailure, Option[ASN1TypeNarrowedSeq]] = 
    taggedValues
    .get(tag)
    .map(it => Right(Some(it.asInstanceOf[ASN1Sequence]).map(ASN1TypeNarrowedSeq(_)))) // TODO: Fix unsafe conversion
    .getOrElse(Right(None))

  private def parseToInstant(primitive: ASN1Primitive): Either[ParsingFailure, Instant] = 
    convertToLong(primitive).map(Instant.ofEpochMilli)

  private def parseToDurationFromSeconds(primitive: ASN1Primitive): Either[ParsingFailure, Duration] = 
    convertToLong(primitive).map(Duration.ofSeconds)

  private def parseToIntSet(encodables: Array[ASN1Encodable]): Either[ParsingFailure, Set[Int]] =
    encodables
      .map(ASN1TypeConversions.convertToInt)
      .foldRight(Right(Set.empty): Either[ParsingFailure, Set[Int]])((e, acc) => for (xs <- acc; x <- e) yield xs + x)
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
