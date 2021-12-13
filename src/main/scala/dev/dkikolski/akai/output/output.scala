package dev.dkikolski.akai

import java.time.Duration
import java.time.Instant

package object output {
  private val UInt32MaxValue: Long = (Int.MaxValue.toLong << 1) + 1

  def bytesToHex(bytes: Array[Byte]): Array[String] =
    bytes.map(String.format("%02x", _))

  def bytesToPrintableCharString(bytes: Array[Byte]): String = 
    bytes.map(b => if (b >= 32) b.toChar else '.').mkString("")

  def unmatched(i: Int) = s"($i)???"

  def purposeFromInt(i: Int): String = i match {
    case 0 => "Encrypt"
    case 1 => "Decrypt"
    case 2 => "Sign"
    case 3 => "Verify"
    case 4 => "DeriveKey"
    case 5 => "WrapKey"
    case x => unmatched(x)
  }

  def algorithmFromInt(i: Int): String = i match {
    case 1   => "RSA"
    case 3   => "EC"
    case 32  => "AES"
    case 128 => "HMAC"
    case x   => unmatched(x)
  }

  def digestFromInt(i: Int): String = i match {
    case 0 => "NONE"
    case 1 => "MD5"
    case 2 => "SHA1"
    case 3 => "SHA-2-224"
    case 4 => "SHA-2-256"
    case 5 => "SHA-2-384"
    case 6 => "SHA-2-512"
    case x => unmatched(x)
  }

  def paddingFromInt(i: Int): String = i match {
    case 1  => "NONE"
    case 2  => "RSA-OAEP"
    case 3  => "RSA-PSS"
    case 4  => "RSA-PKCS1-1-5-ENCRYPT"
    case 5  => "RSA-PKCS1-1-5-SIGN"
    case 64 => "PKCS7"
    case x  => unmatched(x)
  }

  def ecCurveFromInt(i: Int): String = i match {
    case 0 => "P224"
    case 1 => "P256"
    case 2 => "P384"
    case 3 => "P521"
    case x => unmatched(x)
  }

  def originFromInt(i: Int): String = i match {
    case 0 => "Generated"
    case 1 => "Derived"
    case 2 => "Imported"
    case 3 => "Unknown"
    case x => unmatched(x)
  }

  def userAuthTypeFromLong(v: Long): Set[String] = {
    if (v == 0) Set("NONE")
    else {
      List(
        ((v & 1L) == 1L, "Password"),
        ((v & 2L) == 2L, "Fingerprint"),
        (v == UInt32MaxValue, "Any")
      )
        .filter(_._1 == true)
        .map(_._2)
        .toSet
    }
  }

  def durationStringFromSeconds(seconds: Long): String = {
    Duration.ofSeconds(seconds).toString
  }

  def instantStringFromMillis(millis: Long): String = {
    Instant.ofEpochMilli(millis).toString
  }
}
