package dev.dkikolski.akai.output

import dev.dkikolski.akai.schema.KeyDescription
import dev.dkikolski.akai.schema.AuthorizationList
import dev.dkikolski.akai.schema.RootOfTrust
import scala.collection.immutable.TreeMap
import java.time.Duration

private[output] class IntermediateModel(private val humanFriendlyFormat: Boolean) {
  private val UInt32MaxValue: Long = (Int.MaxValue.toLong << 1) + 1

  trait ConvertableValue[A, B]

  final case class RawValue[A](val value: A) extends ConvertableValue[A, A]

  final case class HumanFriendlyValueCandidate[A, B](val value: A, private val f: A => B)
      extends ConvertableValue[A, B]

  def convert(keyDescription: KeyDescription): Seq[(String, Any)] = {
    Seq(
      "attestationVersion"       -> RawValue(keyDescription.attestationVersion),
      "attestationSecurityLevel" -> RawValue(keyDescription.attestationSecurityLevel),
      "keymasterVersion"         -> RawValue(keyDescription.keymasterVersion),
      "keymasterSecurityLevel"   -> RawValue(keyDescription.keymasterSecurityLevel),
      "attestationChallenge"     -> RawValue(keyDescription.attestationChallenge),
      "uniqueId"                 -> RawValue(keyDescription.uniqueId),
      "softwareEnforced"         -> RawValue(convert(keyDescription.softwareEnforced)),
      "teeEnforced"              -> RawValue(convert(keyDescription.teeEnforced))
    ).map(applyFormatting)
  }

  private[this] def convert(authList: AuthorizationList): Seq[(String, Any)] = {
    Seq[(String, Any)](
      "purpose"   -> HumanFriendlyValueCandidate(authList.purpose, _.map(purposeFromInt)),
      "algorithm" -> HumanFriendlyValueCandidate(authList.algorithm, _.map(algorithmFromInt)),
      "keySize"   -> RawValue(authList.keySize),
      "digest"    -> HumanFriendlyValueCandidate(authList.digest, _.map(digestFromInt)),
      "padding"   -> HumanFriendlyValueCandidate(authList.padding, _.map(paddingFromInt)),
      "ecCurve"   -> HumanFriendlyValueCandidate(authList.ecCurve, _.map(ecCurveFromInt)),
      "rsaPublicExponent"         -> RawValue(authList.rsaPublicExponent),
      "rollbackResistance"        -> RawValue(authList.rollbackResistance),
      "activeDateTime"            -> RawValue(authList.activeDateTime),
      "originationExpireDateTime" -> RawValue(authList.originationExpireDateTime),
      "usageExpireDateTime"       -> RawValue(authList.usageExpireDateTime),
      "noAuthRequired"            -> RawValue(authList.noAuthRequired),
      "userAuthType" -> HumanFriendlyValueCandidate(
        authList.userAuthType,
        _.map(userAuthTypeFromLong)
      ),
      "authTimeout" -> HumanFriendlyValueCandidate(
        authList.authTimeout,
        _.map(durationStringFromSeconds)
      ),
      "allowWhileOnBody"            -> RawValue(authList.allowWhileOnBody),
      "trustedUserPresenceRequired" -> RawValue(authList.trustedUserPresenceRequired),
      "trustedConfirmationRequired" -> RawValue(authList.trustedConfirmationRequired),
      "unlockedDeviceRequired"      -> RawValue(authList.unlockedDeviceRequired),
      "allApplications"             -> RawValue(authList.allApplications),
      "applicationId"               -> RawValue(authList.applicationId),
      "creationDateTime"            -> RawValue(authList.creationDateTime),
      "origin"       -> HumanFriendlyValueCandidate(authList.origin, _.map(originFromInt)),
      "rootOfTrust"  -> RawValue(convert(authList.rootOfTrust)),
      "osVersion"    -> RawValue(authList.osVersion),
      "osPatchLevel" -> RawValue(authList.osPatchLevel),
      "attestationApplicationId" -> HumanFriendlyValueCandidate(
        authList.attestationApplicationId,
        bytesToPrintableCharString
      ),
      "attestationIdBrand" -> HumanFriendlyValueCandidate(
        authList.attestationIdBrand,
        bytesToPrintableCharString
      ),
      "attestationIdDevice" -> HumanFriendlyValueCandidate(
        authList.attestationIdDevice,
        bytesToPrintableCharString
      ),
      "attestationIdProduct" -> HumanFriendlyValueCandidate(
        authList.attestationIdProduct,
        bytesToPrintableCharString
      ),
      "attestationIdSerial" -> HumanFriendlyValueCandidate(
        authList.attestationIdSerial,
        bytesToPrintableCharString
      ),
      "attestationIdImei" -> HumanFriendlyValueCandidate(
        authList.attestationIdImei,
        bytesToPrintableCharString
      ),
      "attestationIdMeid" -> HumanFriendlyValueCandidate(
        authList.attestationIdMeid,
        bytesToPrintableCharString
      ),
      "attestationIdManufacturer" -> HumanFriendlyValueCandidate(
        authList.attestationIdManufacturer,
        bytesToPrintableCharString
      ),
      "attestationIdModel" -> HumanFriendlyValueCandidate(
        authList.attestationIdModel,
        bytesToPrintableCharString
      ),
      "vendorPatchLevel" -> RawValue(authList.vendorPatchLevel),
      "bootPatchLevel"   -> RawValue(authList.bootPatchLevel)
    ).map(applyFormatting)
  }

  private[this] def convert(rot: Option[RootOfTrust]): Seq[(String, Any)] = {
    Seq(
      "verifiedBootKey"   -> RawValue(rot.map(_.verifiedBootKey)),
      "deviceLocked"      -> RawValue(rot.map(_.deviceLocked)),
      "verifiedBootState" -> RawValue(rot.map(_.verifiedBootState)),
      "verifiedBootHash"  -> RawValue(rot.map(_.verifiedBootHash))
    ).map(applyFormatting)
  }

  private[this] def applyFormatting[A, B](entry: (String, Any)): (String, Any) = entry match {
    case (key: String, RawValue(value)) => (key, value)
    case (key: String, HumanFriendlyValueCandidate(value, mapping)) =>
      (key, if (humanFriendlyFormat) mapping(value) else value)
    case (key: String, value: Any) => (key, value)
  }

  private[this] def unmatched(i: Int) = s"($i)???"

  private[this] def purposeFromInt(i: Int): String = i match {
    case 0 => "Encrypt"
    case 1 => "Decrypt"
    case 2 => "Sign"
    case 3 => "Verify"
    case 4 => "DeriveKey"
    case 5 => "WrapKey"
    case x => unmatched(x)
  }

  private[this] def algorithmFromInt(i: Int): String = i match {
    case 1   => "RSA"
    case 3   => "EC"
    case 32  => "AES"
    case 128 => "HMAC"
    case x   => unmatched(x)
  }

  private[this] def digestFromInt(i: Int): String = i match {
    case 0 => "NONE"
    case 1 => "MD5"
    case 2 => "SHA1"
    case 3 => "SHA-2-224"
    case 4 => "SHA-2-256"
    case 5 => "SHA-2-384"
    case 6 => "SHA-2-512"
    case x => unmatched(x)
  }

  private[this] def paddingFromInt(i: Int): String = i match {
    case 1  => "NONE"
    case 2  => "RSA-OAEP"
    case 3  => "RSA-PSS"
    case 4  => "RSA-PKCS1-1-5-ENCRYPT"
    case 5  => "RSA-PKCS1-1-5-SIGN"
    case 64 => "PKCS7"
    case x  => unmatched(x)
  }

  private[this] def ecCurveFromInt(i: Int): String = i match {
    case 0 => "P224"
    case 1 => "P256"
    case 2 => "P384"
    case 3 => "P521"
    case x => unmatched(x)
  }

  private[this] def originFromInt(i: Int): String = i match {
    case 0 => "Generated"
    case 1 => "Derived"
    case 2 => "Imported"
    case 3 => "Unknown"
    case x => unmatched(x)
  }

  private[this] def userAuthTypeFromLong(v: Long): Set[String] = {
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

  private[this] def durationStringFromSeconds(seconds: Long): String = {
    Duration.ofSeconds(seconds).toString()
  }
}
