package dev.dkikolski.akai.schema

final case class KeyDescription(
    attestationVersion: Int,
    attestationSecurityLevel: SecurityLevel,
    keymasterVersion: Int,
    keymasterSecurityLevel: SecurityLevel,
    attestationChallenge: Array[Byte],
    uniqueId: Array[Byte],
    softwareEnforced: AuthorizationList,
    teeEnforced: AuthorizationList
)

enum SecurityLevel(val intValue: Int) {
  case Software extends SecurityLevel(0)
  case TrustedEnvironment extends SecurityLevel(1)
  case StrongBox extends SecurityLevel(2)
}
