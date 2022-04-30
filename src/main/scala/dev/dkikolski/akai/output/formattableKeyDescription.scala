package dev.dkikolski.akai.output

import UniversalConversions.convertToString
import UniversalConversions.bytesToHex

import dev.dkikolski.akai.schema.KeyDescription

private[output] sealed trait FormattableKeyDescription {
  def attestationVersion: String
  def attestationSecurityLevel: String
  def keymasterVersion: String
  def keymasterSecurityLevel: String
  def attestationChallenge: String
  def uniqueId: String
  def softwareEnforced: FormattableAuthorizationList
  def teeEnforced: FormattableAuthorizationList
}

private[output] class HumanFriendlyKeyDescription(kd: KeyDescription)
    extends FormattableKeyDescription {
  def attestationVersion: String       = kd.attestationVersion.toString
  def attestationSecurityLevel: String = kd.attestationSecurityLevel.toString
  def keymasterVersion: String         = kd.keymasterVersion.toString
  def keymasterSecurityLevel: String   = kd.keymasterSecurityLevel.toString
  def attestationChallenge: String     = convertToString(kd.attestationChallenge)
  def uniqueId: String                 = convertToString(kd.uniqueId)
  def softwareEnforced: FormattableAuthorizationList =
    HumanFriendlyAuthorizationList(kd.softwareEnforced)
  def teeEnforced: FormattableAuthorizationList = HumanFriendlyAuthorizationList(kd.teeEnforced)
}

private[output] class RawValuesKeyDescription(kd: KeyDescription) extends FormattableKeyDescription {
  def attestationVersion: String       = kd.attestationVersion.toString
  def attestationSecurityLevel: String = kd.attestationSecurityLevel.toString
  def keymasterVersion: String         = kd.keymasterVersion.toString
  def keymasterSecurityLevel: String   = kd.keymasterSecurityLevel.toString
  def attestationChallenge: String     = convertToString(kd.attestationChallenge)
  def uniqueId: String                 = convertToString(kd.attestationChallenge)
  def softwareEnforced: FormattableAuthorizationList = RawValuesAuthorizationList(
    kd.softwareEnforced
  )
  def teeEnforced: FormattableAuthorizationList = RawValuesAuthorizationList(kd.teeEnforced)
}
