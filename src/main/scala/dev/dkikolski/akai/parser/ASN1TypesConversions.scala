package dev.dkikolski.akai.parser

import org.bouncycastle.asn1.ASN1Encodable
import org.bouncycastle.asn1.ASN1Integer
import org.bouncycastle.asn1.ASN1Enumerated
import org.bouncycastle.asn1.ASN1Boolean
import org.bouncycastle.asn1.ASN1OctetString
import org.bouncycastle.asn1.ASN1Private
import org.bouncycastle.asn1.ASN1Primitive

// TODO: refactor to package object?
private[parser] object ASN1TypesConversions {

  def convertToInt(encodable: ASN1Encodable): Either[ParsingFailure, Int] =
    encodable match {
      case i: ASN1Integer    => Right(convertToInt(i))
      case e: ASN1Enumerated => Right(convertToInt(e))
      case anyOther          => Left(TypeMismatch(anyOther, encodable.getClass, Int.getClass))
    }

  def convertToInt(primitive: ASN1Primitive): Either[ParsingFailure, Int] =
    primitive match {
      case i: ASN1Integer => Right(convertToInt(i))
      case other          => Left(TypeMismatch(other, primitive.getClass, Int.getClass))
    }

  def convertToLong(primitive: ASN1Primitive): Either[ParsingFailure, Long] =
    primitive match {
      case i: ASN1Integer => Right(convertToLong(i))
      case other          => Left(TypeMismatch(other, primitive.getClass, Long.getClass))
    }

  def convertToBoolean(encodable: ASN1Encodable): Either[ParsingFailure, Boolean] =
    encodable match {
      case b: ASN1Boolean => Right(convertToBoolean(b))
      case anyOther       => Left(TypeMismatch(anyOther, encodable.getClass, Boolean.getClass))
    }

  def convertToString(encodable: ASN1Encodable): Either[ParsingFailure, String] =
    encodable match {
      case s: ASN1OctetString => Right(convertToString(s))
      case anyOther           => Left(TypeMismatch(anyOther, encodable.getClass, classOf[String]))
    }

  def convertToBytes(primitive: ASN1Primitive): Either[ParsingFailure, Array[Byte]] =
    primitive match {
      case s: ASN1OctetString => Right(s.getOctets)
      case other              => Left(TypeMismatch(other, primitive.getClass, classOf[Array[Byte]]))
    }

  private[this] def convertToInt(enumerated: ASN1Enumerated): Int =
    enumerated.getValue.intValueExact

  private[this] def convertToInt(i: ASN1Integer): Int =
    i.getValue.intValueExact

  private[this] def convertToLong(i: ASN1Integer): Long =
    i.getValue.longValue

  private[this] def convertToString(octetString: ASN1OctetString): String =
    String(octetString.getOctets)

  private[this] def convertToBoolean(b: ASN1Boolean): Boolean = b.isTrue

}
