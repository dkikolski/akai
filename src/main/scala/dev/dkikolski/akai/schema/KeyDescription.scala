package dev.dkikolski.akai.schema

final case class KeyDescription(
    val attestationVersion: Int,
    val attestationSecurityLevel: SecurityLevel,
    val keymasterVersion: Int,
    val keymasterSecurityLevel: SecurityLevel,
    val attestationChallenge: Array[Byte],
    val uniqueId: Array[Byte],
    val softwareEnforced: AuthorizationList, 
    val teeEnforced: AuthorizationList
)

enum SecurityLevel(val intValue: Int) {
  case Software extends SecurityLevel(0)
  case TrustedEnvironment extends SecurityLevel(1)
  case StrongBox extends SecurityLevel(2)
}
