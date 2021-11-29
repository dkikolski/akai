package dev.dkikolski.akai.parser

import org.bouncycastle.asn1.ASN1Encodable
import org.bouncycastle.asn1.ASN1Integer
import org.bouncycastle.asn1.ASN1Enumerated
import org.bouncycastle.asn1.ASN1Boolean
import org.bouncycastle.asn1.ASN1OctetString
import org.bouncycastle.asn1.ASN1Private
import org.bouncycastle.asn1.ASN1Primitive

private[parser] object ASN1Conversions {

  def convertToInt(encodable: ASN1Encodable): Either[ParsingFailure, Int] =
    convertToInt(encodable.toASN1Primitive)

  def convertToInt(primitive: ASN1Primitive): Either[ParsingFailure, Int] =
    primitive match {
      case i: ASN1Integer    => Right(convertToInt(i))
      case e: ASN1Enumerated => Right(convertToInt(e))
      case other             => Left(TypeMismatch(other, "Int"))
    }

  def convertToLong(primitive: ASN1Primitive): Either[ParsingFailure, Long] =
    primitive match {
      case i: ASN1Integer => Right(convertToLong(i))
      case other          => Left(TypeMismatch(other, "Long"))
    }

  def convertToBoolean(encodable: ASN1Encodable): Either[ParsingFailure, Boolean] =
    encodable match {
      case b: ASN1Boolean => Right(convertToBoolean(b))
      case other          => Left(TypeMismatch(other, "Boolean"))
    }

  def convertToString(encodable: ASN1Encodable): Either[ParsingFailure, String] =
    encodable match {
      case s: ASN1OctetString => Right(convertToString(s))
      case other              => Left(TypeMismatch(other, "String"))
    }

  def convertToBytes(primitive: ASN1Primitive): Either[ParsingFailure, Array[Byte]] =
    primitive match {
      case s: ASN1OctetString => Right(s.getOctets)
      case other              => Left(TypeMismatch(other, "Array[Byte]"))
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