package dev.dkikolski.akai.output

import UniversalConversions.bytesToHex
import UniversalConversions.convertToString
import dev.dkikolski.akai.schema.AuthorizationList
import java.time.Duration
import java.time.Instant

private[output] sealed trait FormattableAuthorizationList {
  def purpose: String
  def algorithm: String
  def keySize: String
  def digest: String
  def padding: String
  def ecCurve: String
  def rsaPublicExponent: String
  def rollbackResistance: String
  def activeDateTime: String
  def originationExpireDateTime: String
  def usageExpireDateTime: String
  def noAuthRequired: String
  def userAuthType: String
  def authTimeout: String
  def allowWhileOnBody: String
  def trustedUserPresenceRequired: String
  def trustedConfirmationRequired: String
  def unlockedDeviceRequired: String
  def allApplications: String
  def applicationId: String
  def creationDateTime: String
  def origin: String
  def osVersion: String
  def osPatchLevel: String
  def attestationIdBrand: String
  def attestationIdDevice: String
  def attestationIdProduct: String
  def attestationIdSerial: String
  def attestationIdImei: String
  def attestationIdMeid: String
  def attestationIdManufacturer: String
  def attestationIdModel: String
  def vendorPatchLevel: String
  def bootPatchLevel: String
  def rootOfTrust: FormattableRootOfTrust
  def attestationApplicationId: FormattableAttestationApplicationId
}

private[output] class RawValuesAuthorizationList(al: AuthorizationList)
    extends FormattableAuthorizationList {
  def purpose: String                     = convertToString(al.purpose)
  def algorithm: String                   = convertToString(al.algorithm)
  def keySize: String                     = convertToString(al.keySize)
  def digest: String                      = convertToString(al.digest)
  def padding: String                     = convertToString(al.padding)
  def ecCurve: String                     = convertToString(al.ecCurve)
  def rsaPublicExponent: String           = convertToString(al.rsaPublicExponent)
  def rollbackResistance: String          = convertToString(al.rollbackResistance)
  def activeDateTime: String              = convertToString(al.activeDateTime)
  def originationExpireDateTime: String   = convertToString(al.originationExpireDateTime)
  def usageExpireDateTime: String         = convertToString(al.usageExpireDateTime)
  def noAuthRequired: String              = convertToString(al.noAuthRequired)
  def userAuthType: String                = convertToString(al.userAuthType)
  def authTimeout: String                 = convertToString(al.authTimeout)
  def allowWhileOnBody: String            = convertToString(al.allowWhileOnBody)
  def trustedUserPresenceRequired: String = convertToString(al.trustedUserPresenceRequired)
  def trustedConfirmationRequired: String = convertToString(al.trustedConfirmationRequired)
  def unlockedDeviceRequired: String      = convertToString(al.unlockedDeviceRequired)
  def allApplications: String             = convertToString(al.allApplications)
  def applicationId: String               = convertToString(al.applicationId)
  def creationDateTime: String            = convertToString(al.creationDateTime)
  def origin: String                      = convertToString(al.origin)
  def osVersion: String                   = convertToString(al.osVersion)
  def osPatchLevel: String                = convertToString(al.osPatchLevel)
  def attestationIdBrand: String          = convertToString(al.attestationIdBrand)
  def attestationIdDevice: String         = convertToString(al.attestationIdDevice)
  def attestationIdProduct: String        = convertToString(al.attestationIdProduct)
  def attestationIdSerial: String         = convertToString(al.attestationIdSerial)
  def attestationIdImei: String           = convertToString(al.attestationIdImei)
  def attestationIdMeid: String           = convertToString(al.attestationIdMeid)
  def attestationIdManufacturer: String   = convertToString(al.attestationIdManufacturer)
  def attestationIdModel: String          = convertToString(al.attestationIdModel)
  def vendorPatchLevel: String            = convertToString(al.vendorPatchLevel)
  def bootPatchLevel: String              = convertToString(al.bootPatchLevel)
  def rootOfTrust: FormattableRootOfTrust =
    al.rootOfTrust.map(CommonRootOfTrust(_)).getOrElse(BlankRootOfTrust())
  def attestationApplicationId: FormattableAttestationApplicationId = al.attestationApplicationId
    .map(CommonAttestationApplicationId(_))
    .getOrElse(BlankAttestationApplicationId())
}

private[output] class HumanFriendlyAuthorizationList(al: AuthorizationList)
    extends FormattableAuthorizationList {
  def purpose: String            = convertToString(al.purpose.map(purposeFromInt))
  def algorithm: String          = convertToString(al.algorithm.map(algorithmFromInt))
  def keySize: String            = convertToString(al.keySize)
  def digest: String             = convertToString(al.digest.map(digestFromInt))
  def padding: String            = convertToString(al.padding.map(paddingFromInt))
  def ecCurve: String            = convertToString(al.ecCurve.map(ecCurveFromInt))
  def rsaPublicExponent: String  = convertToString(al.rsaPublicExponent)
  def rollbackResistance: String = convertToString(al.rollbackResistance)
  def activeDateTime: String     = convertToString(al.activeDateTime.map(instantStringFromMillis))
  def originationExpireDateTime: String = convertToString(
    al.originationExpireDateTime.map(instantStringFromMillis)
  )
  def usageExpireDateTime: String = convertToString(
    al.usageExpireDateTime.map(instantStringFromMillis)
  )
  def noAuthRequired: String   = convertToString(al.noAuthRequired)
  def userAuthType: String     = convertToString(al.userAuthType.map(userAuthTypeFromLong))
  def authTimeout: String      = convertToString(al.authTimeout.map(durationStringFromSeconds))
  def allowWhileOnBody: String = convertToString(al.allowWhileOnBody)
  def trustedUserPresenceRequired: String = convertToString(al.trustedUserPresenceRequired)
  def trustedConfirmationRequired: String = convertToString(al.trustedConfirmationRequired)
  def unlockedDeviceRequired: String      = convertToString(al.unlockedDeviceRequired)
  def allApplications: String             = convertToString(al.allApplications)
  def applicationId: String               = convertToString(al.applicationId)
  def creationDateTime: String   = convertToString(al.creationDateTime.map(instantStringFromMillis))
  def origin: String             = convertToString(al.origin.map(originFromInt))
  def osVersion: String          = convertToString(al.osVersion)
  def osPatchLevel: String       = convertToString(al.osPatchLevel)
  def attestationIdBrand: String = convertToString(al.attestationIdBrand)
  def attestationIdDevice: String       = convertToString(al.attestationIdDevice)
  def attestationIdProduct: String      = convertToString(al.attestationIdProduct)
  def attestationIdSerial: String       = convertToString(al.attestationIdSerial)
  def attestationIdImei: String         = convertToString(al.attestationIdImei)
  def attestationIdMeid: String         = convertToString(al.attestationIdMeid)
  def attestationIdManufacturer: String = convertToString(al.attestationIdManufacturer)
  def attestationIdModel: String        = convertToString(al.attestationIdModel)
  def vendorPatchLevel: String          = convertToString(al.vendorPatchLevel)
  def bootPatchLevel: String            = convertToString(al.bootPatchLevel)
  def rootOfTrust: FormattableRootOfTrust = al.rootOfTrust
    .map(CommonRootOfTrust(_))
    .getOrElse(BlankRootOfTrust())
  def attestationApplicationId: FormattableAttestationApplicationId = al.attestationApplicationId
    .map(CommonAttestationApplicationId(_))
    .getOrElse(BlankAttestationApplicationId())

  private def purposeFromInt(i: Int): String = i match {
    case 0 => "Encrypt"
    case 1 => "Decrypt"
    case 2 => "Sign"
    case 3 => "Verify"
    case 4 => "DeriveKey"
    case 5 => "WrapKey"
    case x => unmatched(x)
  }

  private def algorithmFromInt(i: Int): String = i match {
    case 1   => "RSA"
    case 3   => "EC"
    case 32  => "AES"
    case 128 => "HMAC"
    case x   => unmatched(x)
  }

  private def digestFromInt(i: Int): String = i match {
    case 0 => "NONE"
    case 1 => "MD5"
    case 2 => "SHA1"
    case 3 => "SHA-2-224"
    case 4 => "SHA-2-256"
    case 5 => "SHA-2-384"
    case 6 => "SHA-2-512"
    case x => unmatched(x)
  }

  private def paddingFromInt(i: Int): String = i match {
    case 1  => "NONE"
    case 2  => "RSA-OAEP"
    case 3  => "RSA-PSS"
    case 4  => "RSA-PKCS1-1-5-ENCRYPT"
    case 5  => "RSA-PKCS1-1-5-SIGN"
    case 64 => "PKCS7"
    case x  => unmatched(x)
  }

  private def ecCurveFromInt(i: Int): String = i match {
    case 0 => "P224"
    case 1 => "P256"
    case 2 => "P384"
    case 3 => "P521"
    case x => unmatched(x)
  }

  private def originFromInt(i: Int): String = i match {
    case 0 => "Generated"
    case 1 => "Derived"
    case 2 => "Imported"
    case 3 => "Unknown"
    case x => unmatched(x)
  }

  private def userAuthTypeFromLong(v: Long): Set[String] = {
    val UInt32MaxValue: Long = (Int.MaxValue.toLong << 1) + 1

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

  private def durationStringFromSeconds(seconds: Long): String = {
    Duration.ofSeconds(seconds).toString
  }

  private def instantStringFromMillis(millis: Long): String = {
    Instant.ofEpochMilli(millis).toString
  }

  private def unmatched(i: Int) = s"($i)???"
}
