package dev.dkikolski.akai.parser

import org.bouncycastle.asn1.ASN1InputStream
import org.bouncycastle.asn1.ASN1OctetString
import org.bouncycastle.asn1.ASN1Sequence

import java.io.ByteArrayInputStream
import java.io.File
import java.io.FileInputStream
import java.io.InputStream
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate
import scala.util.Failure
import scala.util.Success
import scala.util.Try
import scala.util.Using

object CertificateParser {
  private val KeyDescriptionObjectId = "1.3.6.1.4.1.11129.2.1.17"

  def parse(file: File): Either[ParsingFailure, X509Certificate] = {
    Using(FileInputStream(file))(parse) match {
      case Success(result) => result
      case Failure(exception) =>
        Left(CertificateParsingFailure(s"Parsing file failed: ${exception.getMessage()}"))
    }
  }

  def parse(bytes: Array[Byte]): Either[ParsingFailure, X509Certificate] = {
    Using(ByteArrayInputStream(bytes))(parse) match {
      case Success(result) => result
      case Failure(exception) =>
        Left(
          CertificateParsingFailure(
            s"Parsing certificate from StdIn failed: ${exception.getMessage()}"
          )
        )
    }
  }

  // To be considered: Parse chain instead of single cert and verify it
  private def parse(is: InputStream): Either[ParsingFailure, X509Certificate] = {
    Try {
      CertificateFactory.getInstance("X.509").generateCertificate(is).asInstanceOf[X509Certificate]
    } match {
      case Success(certificate) => Right(certificate)
      case Failure(exception) =>
        Left(
          CertificateParsingFailure(s"Cannot generate X509 Certificate: ${exception.getMessage()}")
        )
    }
  }

  def getKeyDescriptionExtension(cert: X509Certificate): Either[ParsingFailure, ASN1Sequence] = {
    Option(cert.getExtensionValue(KeyDescriptionObjectId))
      .filter(_.length > 0)
      .map(bytesToASN1Sequence)
      .getOrElse(
        Left(
          CertificateParsingFailure(
            s"Certifcate does not contain Key Description with OID ${KeyDescriptionObjectId}"
          )
        )
      )
  }

  private def bytesToASN1Sequence(bytes: Array[Byte]): Either[ParsingFailure, ASN1Sequence] = {
    Using.Manager { use =>
      val asn1InputStream = use(new ASN1InputStream(bytes))
      val derSeq          = asn1InputStream.readObject().asInstanceOf[ASN1OctetString].getOctets
      val seqInputStream  = use(new ASN1InputStream(derSeq))
      seqInputStream.readObject().asInstanceOf[ASN1Sequence]
    } match {
      case Success(asn1Seq) => Right(asn1Seq)
      case Failure(exception) =>
        Left(
          CertificateParsingFailure(
            s"Cannot convert key description to ASN1Sequence: ${exception.getMessage()}"
          )
        )
    }
  }
}
