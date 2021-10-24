package dev.dkikolski.akai.schema

case class KeyDescription(
    val attestationVersion: Int,
    val attestationSecurityLevel: SecurityLevel,
    val keymasterVersion: Int,
    val keymasterSecurityLevel: SecurityLevel,
    val attestationChallenge: String,
    val uniqueId: String,
    val softwareEnforced: AuthorizationList, 
    val teeEnforced: AuthorizationList
)

enum SecurityLevel(val intValue: Int) {
  case Software extends SecurityLevel(0)
  case TrustedEnvironment extends SecurityLevel(1)
  case StrongBox extends SecurityLevel(2)
}

object SecurityLevel {
  def fromInt(i: Int): SecurityLevel = SecurityLevel.fromOrdinal(i) 
}
