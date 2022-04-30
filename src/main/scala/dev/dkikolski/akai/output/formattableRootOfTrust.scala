package dev.dkikolski.akai.output

import dev.dkikolski.akai.schema.RootOfTrust

import UniversalConversions.convertToString

private[output] sealed trait FormattableRootOfTrust {
  def verifiedBootKey: String
  def deviceLocked: String
  def verifiedBootState: String
  def verifiedBootHash: String
}

private[output] class CommonRootOfTrust(rot: RootOfTrust) extends FormattableRootOfTrust {
  def verifiedBootKey: String   = convertToString(rot.verifiedBootHash)
  def deviceLocked: String      = convertToString(rot.deviceLocked)
  def verifiedBootState: String = convertToString(rot.verifiedBootState)
  def verifiedBootHash: String  = convertToString(rot.verifiedBootHash)
}

private[output] class BlankRootOfTrust extends FormattableRootOfTrust {
  def verifiedBootKey: String   = ""
  def deviceLocked: String      = ""
  def verifiedBootState: String = ""
  def verifiedBootHash: String  = ""
}
