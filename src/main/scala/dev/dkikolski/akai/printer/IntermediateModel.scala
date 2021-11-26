package dev.dkikolski.akai.printer

import dev.dkikolski.akai.schema.KeyDescription
import dev.dkikolski.akai.schema.AuthorizationList
import dev.dkikolski.akai.schema.RootOfTrust
import scala.collection.immutable.TreeMap

class IntermediateModel(private val humanFriendlyFormat: Boolean) {

  trait ConvertableValue[A, B]

  final case class RawValue[A](val value: A) extends ConvertableValue[A, A]

  final case class HumanFriendlyValueCandidate[A, B](val value: A, private val f: A => B)
      extends ConvertableValue[A, B]

  def applyFormatting[A, B](entry: (String, Any)): (String, Any) = entry match {
    case (key: String, RawValue(value)) => (key, value)
    case (key: String, HumanFriendlyValueCandidate(value, mapping)) =>
      (key, if (humanFriendlyFormat) mapping(value) else value)
    case (key: String, value: Any) => (key, value)
  }

  def convert(keyDescription: KeyDescription): Map[String, Any] = {
    TreeMap(
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

  def convert(authList: AuthorizationList): Map[String, Any] = {
    TreeMap[String, Any](
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
      "authTimeout"                 -> RawValue(authList.authTimeout),
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

  def convert(rot: Option[RootOfTrust]): Map[String, Any] = {
    TreeMap(
      "verifiedBootKey"   -> RawValue(rot.map(_.verifiedBootKey)),
      "deviceLocked"      -> RawValue(rot.map(_.deviceLocked)),
      "verifiedBootState" -> RawValue(rot.map(_.verifiedBootState)),
      "verifiedBootHash"  -> RawValue(rot.map(_.verifiedBootHash))
    ).map(applyFormatting)
  }
}
